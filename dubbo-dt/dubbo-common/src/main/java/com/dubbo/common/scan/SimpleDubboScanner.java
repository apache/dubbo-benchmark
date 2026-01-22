package com.dubbo.common.scan;

import com.dubbo.common.aop.DubboInvokeStat;
import com.dubbo.common.aop.EnableDubboTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Component
public class SimpleDubboScanner implements CommandLineRunner {

    private static final String RESOURCE_PATTERN = "**/*.class";
    private static final String TEST_MODEL_CONSUMER = "consumer";
    private static final String TEST_MODEL_PROVIDER = "provider";
    private static final String TEST_MODEL_ALL = "ALL";

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<Class<?>, List<Method>> interfaceMethods = new HashMap<>();
    private EnableDubboTest enableDubboTest;
    // 新增：标记是否需要执行扫描（consumer/ALL=true，provider=false）
    private boolean needScan = false;

    @PostConstruct
    public void init() throws ClassNotFoundException {
        // 1. 先尝试获取主类上的EnableDubboTest注解
        boolean hasAnnotation = isAnnotationPresentOnMainClass(EnableDubboTest.class);
        if (hasAnnotation) {
            String mainClassName = getMainClassName();
            Class<?> mainClass = Class.forName(mainClassName);
            this.enableDubboTest = mainClass.getAnnotation(EnableDubboTest.class);

            // 2. 核心逻辑：根据testModel判断是否需要扫描
            if (this.enableDubboTest != null) {
                String testModel = this.enableDubboTest.testModel();
                // 统一转大写，避免大小写问题
                String upperTestModel = (testModel == null ? "" : testModel.trim().toUpperCase());
                // consumer/ALL需要扫描，provider不需要
                this.needScan = TEST_MODEL_CONSUMER.toUpperCase().equals(upperTestModel)
                        || TEST_MODEL_ALL.toUpperCase().equals(upperTestModel);

                log.info("✅ @EnableDubboTest testModel: {}, needScan: {}", testModel, this.needScan);
            } else {
                this.needScan = false;
            }
        } else {
            this.enableDubboTest = null;
            this.needScan = false;
            log.warn("❌ 主类未找到@EnableDubboTest注解，跳过扫描");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // 核心修改：仅当needScan=true时才执行扫描（consumer/ALL）
        if (!needScan) {
            log.info("📌 testModel为provider/无注解，跳过Dubbo扫描");
            return;
        }

        try {
            String[] packageNames = getBasePackages();
            verifyPackagesAccessibility(packageNames);
            for (String packageName : packageNames) {
                scanPackage(packageName);
            }
            printScanResults();
        } catch (Exception e) {
            log.error("Dubbo scan fail: " + e.getMessage(), e);
        }
    }

    private void verifyPackagesAccessibility(String[] packageNames) {
        for (String packageName : packageNames) {
            try {
                ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(packageName) + "/" + RESOURCE_PATTERN;

                Resource[] resources = resolver.getResources(searchPath);
                if (packageName.contains("common")) {
                    int commonClassCount = 0;
                    for (Resource resource : resources) {
                        if (resource.getFilename() != null && resource.getFilename().endsWith(".class")) {
                            commonClassCount++;
                        }
                    }
                }

            } catch (Exception e) {
                log.warn("Verify package accessibility fail: {}", packageName, e);
            }
        }
    }

    private String getMainClassName() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                if ("main".equals(element.getMethodName())) {
                    String mainClassName = element.getClassName();
                    return mainClassName;
                }
            }
        } catch (Exception e) {
            log.warn("main do fail : " + e.getMessage());
        }

        try {
            String mainClassFromProperty = System.getProperty("sun.java.command");
            if (mainClassFromProperty != null && !mainClassFromProperty.trim().isEmpty()) {
                String[] parts = mainClassFromProperty.split("\\s+");
                if (parts.length > 0) {
                    String mainClassName = parts[0];
                    if (mainClassName.endsWith(".jar")) {
                    } else {
                        return mainClassName;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("not get main param: " + e.getMessage());
        }
        throw new RuntimeException("not find main name");
    }

    private boolean isAnnotationPresentOnMainClass(Class<?> annotationClass) {
        try {
            String mainClassName = getMainClassName();
            Class<?> mainClass = Class.forName(mainClassName);
            Annotation[] annotations = mainClass.getAnnotations();
            for (Annotation annotation : annotations) {
                log.info("  - annotations: {}", annotation.annotationType().getSimpleName());
            }
            boolean hasAnnotation = mainClass.isAnnotationPresent((Class<? extends Annotation>) annotationClass);
            log.info("@EnableDubboTest exist: {}", hasAnnotation);

            if (hasAnnotation) {
                EnableDubboTest enableDubboTest = mainClass.getAnnotation(EnableDubboTest.class);
                log.info("✅ find @EnableDubboTest annotation: {}", enableDubboTest);
            } else {
                log.warn("❌ start class {} not find @EnableDubboTest annotation", mainClass.getName());
            }

            return hasAnnotation;

        } catch (Exception e) {
            log.error("Check annotation fail", e);
            return false;
        }
    }

    private String[] getBasePackages() {
        if (enableDubboTest != null && enableDubboTest.basePackages().length > 0) {
            return enableDubboTest.basePackages();
        }
        try {
            String mainClassName = getMainClassName();
            Class<?> mainClass = Class.forName(mainClassName);
            String basePackage = mainClass.getPackage().getName();
            return new String[]{basePackage};
        } catch (Exception e) {
            log.warn("Get base packages fail, use default: com.dubbo.dlt", e);
        }
        return new String[]{"com.dubbo.dlt"};
    }

    private void scanPackage(String basePackage) throws Exception {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) + "/" + RESOURCE_PATTERN;

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);

        Resource[] resources = resolver.getResources(packageSearchPath);

        int interfaceCount = 0;
        int methodCount = 0;
        int classCount = 0;

        for (Resource resource : resources) {
            if (!resource.isReadable()) {
                continue;
            }

            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                classCount++;
                if (metadataReader.getClassMetadata().isInterface()) {
                    Class<?> clazz = Class.forName(className);
                    int methodsInInterface = scanInterfaceMethods(clazz);
                    if (methodsInInterface > 0) {
                        interfaceCount++;
                        methodCount += methodsInInterface;
                    }
                }
            } catch (Exception e) {
                log.warn("Scan Class fail: {} - {}", resource.getFilename(), e.getMessage());
            }
        }

        log.info("📊 Scan package {} complete: classCount={}, interfaceCount={}, methodCount={}",
                basePackage, classCount, interfaceCount, methodCount);
    }

    private int scanInterfaceMethods(Class<?> interfaceClass) {
        List<Method> annotatedMethods = new ArrayList<>();

        for (Method method : interfaceClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(DubboInvokeStat.class)) {
                DubboInvokeStat annotation = method.getAnnotation(DubboInvokeStat.class);
                if (annotation != null) {
                    annotatedMethods.add(method);
                }
            }
        }

        if (!annotatedMethods.isEmpty()) {
            interfaceMethods.put(interfaceClass, annotatedMethods);

            if (interfaceClass.getName().contains("dubbo.common")) {
                log.info("✅ find annotated method from dubbo-common: {}", interfaceClass.getName());
            }
        }

        return annotatedMethods.size();
    }

    private void printScanResults() {
        if (interfaceMethods.isEmpty()) {
            log.info("📌 No annotated methods found in scanned interfaces");
            return;
        }

        long commonInterfaceCount = interfaceMethods.keySet().stream()
                .filter(cls -> cls.getName().contains("dubbo.common"))
                .count();

        log.info("📊 Final scan result: total interface={}, common interface={}, total method={}",
                interfaceMethods.size(), commonInterfaceCount,
                interfaceMethods.values().stream().mapToInt(List::size).sum());

        interfaceMethods.forEach((interfaceClass, methods) -> {
            log.info("🔍 Interface: {} - annotated methods: {}",
                    interfaceClass.getName(), methods.size());
        });
    }

    public List<Method> findAllMethodsByAnnotationValue(String annotationValue) {
        List<Method> result = new ArrayList<>();

        if (annotationValue == null || annotationValue.trim().isEmpty()) {
            return result;
        }

        annotationValue = annotationValue.trim();

        int foundCount = 0;
        for (List<Method> methods : interfaceMethods.values()) {
            for (Method method : methods) {
                DubboInvokeStat annotation = method.getAnnotation(DubboInvokeStat.class);
                if (annotation != null && annotationValue.equals(annotation.namespace())) {
                    result.add(method);
                    foundCount++;
                }
            }
        }

        log.info("🔍 Find {} methods with annotation namespace: {}", foundCount, annotationValue);
        return result;
    }
}