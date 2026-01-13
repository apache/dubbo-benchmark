package com.dubbo.common.aop;

import com.dubbo.common.consumer.NettyConsumer;
import com.dubbo.common.provider.NettyProvider;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;


public class DubboTestImportSelector implements ImportSelector {

    private static final String TEST_MODEL_CONSUMER = "consumer";
    private static final String TEST_MODEL_PROVIDER = "provider";
    private static final String TEST_MODEL_ALL = "ALL";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableDubboTest.class.getName())
        );
        if (attributes == null) {
            return new String[0];
        }

        String testModel = attributes.getString("testModel");
        if (!StringUtils.hasText(testModel)) {
            testModel = TEST_MODEL_ALL;
        }
        return switch (testModel) {
            case TEST_MODEL_CONSUMER -> new String[]{NettyConsumer.class.getName()};
            case TEST_MODEL_PROVIDER -> new String[]{NettyProvider.class.getName()};
            case TEST_MODEL_ALL -> new String[]{
                    NettyConsumer.class.getName(),
                    NettyProvider.class.getName()
            };
            default -> new String[0];
        };
    }
}