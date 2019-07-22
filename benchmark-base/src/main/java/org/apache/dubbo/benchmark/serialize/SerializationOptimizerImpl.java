package org.apache.dubbo.benchmark.serialize;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.service.UserService;
import org.apache.dubbo.common.serialize.support.SerializationOptimizer;

import java.util.Arrays;
import java.util.Collection;

public class SerializationOptimizerImpl implements SerializationOptimizer {
    @Override
    public Collection<Class<?>> getSerializableClasses() {
        return Arrays.asList(User.class, Page.class, UserService.class);
    }
}
