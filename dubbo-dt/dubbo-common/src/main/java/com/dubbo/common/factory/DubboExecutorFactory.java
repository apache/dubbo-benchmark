package com.dubbo.common.factory;

import com.alibaba.fastjson2.JSON;
import com.dubbo.common.aop.DubboInvokeStat;
import com.dubbo.common.entry.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


@Slf4j
public class DubboExecutorFactory implements DubbTestExecutorFactory{

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationConfig applicationConfig;
    private final RegistryConfig registryConfig;

    public DubboExecutorFactory() {
        applicationConfig = new ApplicationConfig();
        applicationConfig.setName("dubbo-consumer-1");
        applicationConfig.setQosEnable(false);

        registryConfig = new RegistryConfig();
        registryConfig.setAddress("nacos://10.6.5.179:8848");
        registryConfig.setProtocol("nacos");
        registryConfig.setCheck(false);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("registry-type", "service");
        registryConfig.setParameters(parameters);
    }
    public Map<String, Object> executeAllMethods(TestConfig config, List<Method> methods) {
        Map<String, Object> results = new HashMap<>();

        for (Method method : methods) {
            try {
                DubboInvokeStat annotation = method.getAnnotation(DubboInvokeStat.class);
                if (annotation == null) {
                    continue;
                }
                Class<?> argClass =  annotation.argValue();
                String argKey = annotation.argKey();
                String argValue = processArgValueClass(argClass, argKey);
                Object result = executeMethod(config, method, argValue);
                String key = method.getDeclaringClass().getSimpleName() + "." + method.getName();
                results.put(key, result);
            } catch (Exception e) {
                String key = method.getDeclaringClass().getSimpleName() + "." + method.getName();
                results.put(key, "Execute Fail: " + e.getMessage());
            }
        }
        return results;
    }
    private static String processArgValueClass(Class<?> argClass, String methodName) {
        if (argClass.isEnum() && StringUtils.hasText(methodName)) {
            try {
                return findEnumByName(argClass, methodName);
            } catch (IllegalArgumentException e1) {
                try {
                    return findEnumByProperties(argClass, methodName);
                } catch (Exception e2) {
                    System.out.println("找不到对应的枚举常量: " + methodName);
                }
            }
        }
        return null;
    }

    private static String findEnumByName(Class<?> enumClass, String name) {
        String upperName = name.toUpperCase();
        Method valueOfMethod = null;
        try {
            valueOfMethod = enumClass.getMethod("valueOf", String.class);
            Enum<?> enumValue = (Enum<?>) valueOfMethod.invoke(null, upperName);
            return JSON.toJSON(enumValue.getClass().getMethod("getValue").invoke(enumValue)).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String findEnumByProperties(Class<?> enumClass, String searchValue) throws Exception {
        Object[] enumConstants = enumClass.getEnumConstants();

        for (Object constant : enumConstants) {
            Enum<?> enumConstant = (Enum<?>) constant;
            if (searchValue.equalsIgnoreCase(enumConstant.toString())) {
                return enumConstant.name();
            }
            try {
                Field valueField = enumClass.getDeclaredField("value");
                valueField.setAccessible(true);
                Object fieldValue = valueField.get(enumConstant);
                if (fieldValue != null) {
                    if (fieldValue instanceof String) {
                        if (searchValue.equalsIgnoreCase((String) fieldValue)) {
                            return enumConstant.toString();
                        }
                    }
                    else if (searchValue.equalsIgnoreCase(fieldValue.toString())) {
                        return enumConstant.toString();
                    }
                }
            } catch (NoSuchFieldException e) {
            }

            Field[] fields = enumClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == String.class && !field.isEnumConstant() && !field.isSynthetic()) {
                    field.setAccessible(true);
                    String fieldValue = (String) field.get(enumConstant);
                    if (searchValue.equalsIgnoreCase(fieldValue)) {
                        return enumConstant.toString();
                    }
                }
            }
        }

        throw new IllegalArgumentException("not Find enum");
    }

    public Object executeMethod(TestConfig config, Method method, String argValue) {
        ReferenceConfig<?> reference = null;
        try {
            Class<?> serviceInterface = method.getDeclaringClass();
            reference = new ReferenceConfig<>();
            reference.setApplication(applicationConfig);
            reference.setRegistry(registryConfig);
            reference.setInterface(serviceInterface);
            reference.setLoadbalance(config.getLocadbance());
            reference.setCheck(false);
            reference.setTimeout(5000);
            reference.setParameters(Collections.singletonMap("serialization", config.getSerialization()));
            Object service = reference.get();
            Object[] args = parseArgValue(argValue, method.getParameterTypes());
            beforeInvokeFilter(method, args, serviceInterface);
            Object result = method.invoke(service, args);
            afterInvokeFilter(method, args, result, serviceInterface);
            return result;
        } catch (Exception e) {
            exceptionInvokeFilter(method, argValue, e);
            throw new RuntimeException("do Dubbo func fail : " + method.getName(), e);
        } finally {
            if (reference != null) {
                try {
                    reference.destroy();
                } catch (Exception e) {
                }
            }
        }
    }


    private Object[] parseArgValue(String argValue, Class<?>[] parameterTypes) throws Exception {
        if (argValue == null || argValue.trim().isEmpty()) {
            return new Object[0];
        }

        Object parsed = objectMapper.readValue(argValue, Object.class);

        if (parameterTypes.length == 0) {
            return new Object[0];
        } else if (parameterTypes.length == 1) {
            Object converted = convertSingleValue(parsed, parameterTypes[0]);
            return new Object[]{converted};
        } else if (parsed instanceof List) {
            List<?> list = (List<?>) parsed;
            Object[] args = new Object[Math.min(list.size(), parameterTypes.length)];
            for (int i = 0; i < args.length; i++) {
                args[i] = convertSingleValue(list.get(i), parameterTypes[i]);
            }
            return args;
        } else {
            return new Object[0];
        }
    }

    private Object convertSingleValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return value;
        }

        if (targetType == String.class) {
            return value.toString();
        }

        if (targetType == Integer.class || targetType == int.class) {
            return convertToInteger(value);
        }
        if (targetType == Long.class || targetType == long.class) {
            return convertToLong(value);
        }
        if (targetType == Double.class || targetType == double.class) {
            return convertToDouble(value);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return convertToBoolean(value);
        }

        if (targetType.isArray()) {
            return convertToArray(value, targetType);
        }
        if (List.class.isAssignableFrom(targetType)) {
            return convertToList(value, targetType);
        }
        if (Map.class.isAssignableFrom(targetType)) {
            return convertToMap(value, targetType);
        }

        try {
            return objectMapper.convertValue(value, targetType);
        } catch (Exception e) {
        }

        if (value instanceof String) {
            try {
                return objectMapper.readValue((String) value, targetType);
            } catch (Exception e) {
            }
        }

        throw new IllegalArgumentException(
                " no recover " + value + " (" + value.getClass() + ") to " + targetType
        );
    }

    private Integer convertToInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("not recover Integer: " + value);
            }
        } else {
            throw new IllegalArgumentException("not recover Integer: " + value);
        }
    }

    private Long convertToLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("not recover Long: " + value);
            }
        } else {
            throw new IllegalArgumentException("not recover Long: " + value);
        }
    }

    private Double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("not recover Double: " + value);
            }
        } else {
            throw new IllegalArgumentException("not recover Double: " + value);
        }
    }

    private Boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        } else {
            throw new IllegalArgumentException("not recover Boolean: " + value);
        }
    }

    private Object convertToArray(Object value, Class<?> targetType) {
        Class<?> componentType = targetType.getComponentType();

        if (value instanceof List) {
            List<?> list = (List<?>) value;
            Object array = java.lang.reflect.Array.newInstance(componentType, list.size());

            for (int i = 0; i < list.size(); i++) {
                Object element = convertSingleValue(list.get(i), componentType);
                java.lang.reflect.Array.set(array, i, element);
            }

            return array;
        }

        Object array = java.lang.reflect.Array.newInstance(componentType, 1);
        Object element = convertSingleValue(value, componentType);
        java.lang.reflect.Array.set(array, 0, element);
        return array;
    }

    private List<?> convertToList(Object value, Class<?> targetType) {
        if (value instanceof List) {
            return (List<?>) value;
        }

        List<Object> list = new ArrayList<>();
        list.add(value);
        return list;
    }

    private Map<?, ?> convertToMap(Object value, Class<?> targetType) {
        if (value instanceof Map) {
            return (Map<?, ?>) value;
        }

        throw new IllegalArgumentException("not recover Map: " + value);
    }

    private String[] getParameterTypeNames(Class<?>[] parameterTypes) {
        String[] typeNames = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            typeNames[i] = parameterTypes[i].getName();
        }
        return typeNames;
    }

    @Override
    public void beforeInvokeFilter(Method method, Object[] args, Class<?> serviceInterface) {
    }

    @Override
    public void afterInvokeFilter(Method method, Object[] args, Object result, Class<?> serviceInterface) {

    }

    @Override
    public void exceptionInvokeFilter(Method method, String argValue, Exception e) {

    }
}