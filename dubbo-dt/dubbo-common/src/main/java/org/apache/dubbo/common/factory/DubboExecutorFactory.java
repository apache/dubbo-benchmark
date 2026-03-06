package org.apache.dubbo.common.factory;


import org.apache.dubbo.common.aop.DubboInvokeStat;
import org.apache.dubbo.common.constant.Constant;
import org.apache.dubbo.common.entry.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class DubboExecutorFactory implements DubbTestExecutorFactory{

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DubboExecutorFactory() {
    }
    public Map<String, Object> executeAllMethods(TestConfig config, List<Method> methods) {
        Map<String, Object> results = new ConcurrentHashMap<>();

        for (Method method : methods) {
            try {
                DubboInvokeStat annotation = method.getAnnotation(DubboInvokeStat.class);
                if (annotation == null) {
                    continue;
                }
                Class<?> argClass =  annotation.argValue();
                String argKey = annotation.argKey();
                Object argValue = processArgValueClass(argClass, argKey);
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
    private static Object processArgValueClass(Class<?> argClass, String methodName) {
        if (argClass.isEnum() && StringUtils.hasText(methodName)) {
            try {
                return findEnumByName(argClass, methodName);
            } catch (IllegalArgumentException e1) {
                try {
                    return findEnumByProperties(argClass, methodName);
                } catch (Exception e2) {
                    throw new RuntimeException("not Find args");
                }
            }
        }
        return null;
    }
    private static Object findEnumByName(Class<?> enumClass, String name) {
        try {
            Method valueOfMethod = enumClass.getMethod("valueOf", String.class);

            String upperName = name.toUpperCase();
            Enum<?> enumValue = (Enum<?>) valueOfMethod.invoke(null, upperName);

            Method getValueMethod = enumClass.getMethod("getValue");
            return getValueMethod.invoke(enumValue);

        } catch (NoSuchMethodException e) {
           throw new RuntimeException("convert error");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get enum value for: " + name, e);
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
    private boolean isProtobufServiceClass(Class<?> clazz) {
        if (clazz.getName().contains("Grpc$") && clazz.getName().endsWith("Base")) {
            return true;
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null &&
                (superclass.getName().contains("Grpc") ||
                        superclass.getName().contains("AbstractService"))) {
            return true;
        }

        for (Class<?> iface : clazz.getInterfaces()) {
            if (iface.getName().contains("Grpc") ||
                    iface.getName().contains("AsyncService")) {
                return true;
            }
        }

        return clazz.getName().contains("Grpc") &&
                clazz.getName().contains("$") &&
                !clazz.getName().contains("Message");
    }
    public Object executeMethod(TestConfig config, Method method, Object argValue) {
        ReferenceConfig<?> reference = null;
        Method targetMethod;
        Class<?> targetInterface;
        try {
            Class<?> serviceInterface = method.getDeclaringClass();
            Class<?>[] interfaces = serviceInterface.getInterfaces();
            reference = new ReferenceConfig<>();
            if (interfaces != null && interfaces.length > 0) {
                targetInterface = interfaces[0];
                targetMethod = targetInterface.getMethod(
                        method.getName(),
                        method.getParameterTypes()
                );
                reference.setInterface(targetInterface);
            } else {
                targetInterface = serviceInterface;
                targetMethod = method;
                reference.setInterface(serviceInterface);
            }
            reference.setLoadbalance(config.getLocadbance());
            reference.setCheck(false);
            reference.setTimeout(Constant.DUBBO_TIME_OUT);
            reference.setProtocol(config.getProtocol());
            reference.setParameters(Collections.singletonMap(Constant.SERIALIZATION, config.getSerialization()));
            Object service = reference.get();
            Object[] args = parseArgValue(argValue, method.getParameterTypes(), method.getReturnType());
            beforeInvokeFilter(method, args, serviceInterface);
            Object result = targetMethod.invoke(service, args);
            afterInvokeFilter(method, args, result, serviceInterface);
            return result;
        } catch (Exception e) {
            exceptionInvokeFilter(method, argValue, e);
            throw new RuntimeException("do Dubbo func fail : " + method.getName(), e);
        } finally {
           reference = null;
        }
    }


    private Object[] parseArgValue(Object argValue, Class<?>[] parameterTypes, Class<?> returnType) throws Exception {
        if (argValue == null) {
            return new Object[0];
        }

        if (parameterTypes.length == 0) {
            return new Object[0];
        } else if (parameterTypes.length == 1) {
            Object converted = convertSingleValue(argValue, parameterTypes[0]);
            return new Object[]{converted};
        } else if (argValue instanceof List) {
            List<?> list = (List<?>) argValue;
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
    public void exceptionInvokeFilter(Method method, Object argValue, Exception e) {

    }
}