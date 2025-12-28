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

    @Autowired
    private ApplicationContext applicationContext;

    // 存储扫描到的接口和方法
    private final Map<Class<?>, List<Method>> interfaceMethods = new HashMap<>();
    private EnableDubboTest enableDubboTest;
    @PostConstruct
    public void init() throws ClassNotFoundException {
        // ========== 核心一行代码判断：启动类上是否有@EnableDubboTest ==========
        boolean hasAnnotation = isAnnotationPresentOnMainClass(EnableDubboTest.class);
        if (hasAnnotation) {
            // 获取启动类上的注解实例，拿到注解中的配置值
            String mainClassName = System.getProperty("sun.java.command").split(" ")[0];
            Class<?> mainClass = Class.forName(mainClassName);
            this.enableDubboTest = mainClass.getAnnotation(EnableDubboTest.class);

            log.info("检测到【启动类】上标注了 @EnableDubboTest 注解，启用Dubbo测试框架");
            log.info("应用名称: " + enableDubboTest.applicationName());
            log.info("注册中心: " + enableDubboTest.registryAddress());
        } else {
            log.info("【启动类】上未标注 @EnableDubboTest 注解，跳过Dubbo测试框架初始化");
            this.enableDubboTest = null;
        }
    }
    @Override
    public void run(String... args) throws Exception {
        // 如果没有@EnableDubboTest注解，直接返回
        if (enableDubboTest == null) {
            log.info("跳过Dubbo接口扫描，因为未启用@EnableDubboTest");
            return;
        }

        // 获取要扫描的包
        String[] packageNames = getBasePackages();
        log.info("扫描包: " + Arrays.toString(packageNames));

        // 扫描每个包
        for (String packageName : packageNames) {
            scanPackage(packageName);
        }

        // 打印结果
        printScanResults();
        System.out.println("=== 扫描完成 ===\n");
    }
    private boolean isAnnotationPresentOnMainClass(Class<?> annotationClass) {
        try {
            // 1. 获取 SpringBoot 启动类的全类名 (JVM原生属性，绝对可靠)
            String mainClassName = System.getProperty("sun.java.command");
            if (mainClassName == null || mainClassName.isEmpty()) {
                return false;
            }
            // 2. 处理启动命令中的参数，只提取启动类的完整类名
            String[] cmdParts = mainClassName.split(" ");
            String realMainClassName = cmdParts[0]; // 启动类永远是命令行第一个参数
            // 3. 反射加载启动类
            Class<?> mainClass = Class.forName(realMainClassName);
            // 4. 核心判断：这个启动类上 是否有 @EnableDubboTest 注解
            return mainClass.isAnnotationPresent((Class<? extends Annotation>) annotationClass);
        } catch (Exception e) {
            log.warn("获取启动类并判断注解失败:{}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取基础包名
     * 优先使用@EnableDubboTest注解配置的包
     */
    private String[] getBasePackages() {
        // 1. 如果注解中配置了basePackages，使用注解的配置
        if (enableDubboTest != null && enableDubboTest.basePackages().length > 0) {
            return enableDubboTest.basePackages();
        }

        // 2. 否则使用Spring Boot主类所在的包
        String mainClassName = System.getProperty("sun.java.command");
        if (mainClassName != null) {
            try {
                // 处理可能的命令行参数
                if (mainClassName.contains(" ")) {
                    mainClassName = mainClassName.split(" ")[0];
                }
                Class<?> mainClass = Class.forName(mainClassName);
                String basePackage = mainClass.getPackage().getName();
                return new String[]{basePackage};
            } catch (Exception e) {
                // 忽略异常
            }
        }

        // 3. 默认扫描com.dubbo.dlt包
        return new String[]{"com.dubbo.dlt"};
    }

    /**
     * 扫描指定包
     */
    private void scanPackage(String basePackage) throws Exception {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(basePackage) + "/" + RESOURCE_PATTERN;

        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);

        Resource[] resources = resolver.getResources(packageSearchPath);

        for (Resource resource : resources) {
            if (!resource.isReadable()) {
                continue;
            }

            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();

                // 检查是否是接口
                if (metadataReader.getClassMetadata().isInterface()) {
                    Class<?> clazz = Class.forName(className);
                    scanInterfaceMethods(clazz);
                }

            } catch (Exception e) {
                // 忽略扫描异常
                System.err.println("扫描类失败: " + resource.getFilename() + ", " + e.getMessage());
            }
        }
    }

    /**
     * 扫描接口的所有方法
     */
    private void scanInterfaceMethods(Class<?> interfaceClass) {
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
        }
    }

    /**
     * 打印扫描结果
     */
    private void printScanResults() {
        if (interfaceMethods.isEmpty()) {
            System.out.println("未发现任何带有@DubboTestMethod注解的接口");
            return;
        }

        System.out.println("发现 " + interfaceMethods.size() + " 个接口，包含测试方法:");

        interfaceMethods.forEach((interfaceClass, methods) -> {
            System.out.println("接口: " + interfaceClass.getSimpleName());

            for (Method method : methods) {
                DubboInvokeStat annotation = method.getAnnotation(DubboInvokeStat.class);
                System.out.println("  ├─ 方法: " + method.getName() +
                                 " - " + annotation.value());
            }
        });
    }

    /**
     * 获取所有扫描到的接口
     */
    public Set<Class<?>> getAllInterfaces() {
        return interfaceMethods.keySet();
    }

    /**
     * 获取接口的所有测试方法
     */
    public List<Method> getInterfaceMethods(Class<?> interfaceClass) {
        return interfaceMethods.getOrDefault(interfaceClass, Collections.emptyList());
    }

    /**
     * 获取所有测试方法
     */
    public List<Method> getAllMethods() {
        List<Method> allMethods = new ArrayList<>();
        interfaceMethods.values().forEach(allMethods::addAll);
        return allMethods;
    }
    /**
     * 根据注解value值查找所有方法（扁平化，不按接口分组）
     * @param annotationValue 要查找的注解value值
     * @return 所有符合条件的Method列表
     */
    public List<Method> findAllMethodsByAnnotationValue(String annotationValue) {
        List<Method> result = new ArrayList<>();

        if (annotationValue == null || annotationValue.trim().isEmpty()) {
            return result;
        }

        annotationValue = annotationValue.trim();

        // 遍历所有接口的所有方法
        for (List<Method> methods : interfaceMethods.values()) {
            for (Method method : methods) {
                DubboInvokeStat annotation = method.getAnnotation(DubboInvokeStat.class);
                if (annotation != null && annotationValue.equals(annotation.value())) {
                    result.add(method);
                }
            }
        }

        return result;
    }
}