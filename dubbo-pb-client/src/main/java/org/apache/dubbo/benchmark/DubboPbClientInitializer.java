package org.apache.dubbo.benchmark;

import org.apache.dubbo.benchmark.bean.PagePB;
import org.apache.dubbo.common.serialize.protobuf.support.ProtobufUtils;

import javax.annotation.PostConstruct;

public class DubboPbClientInitializer {

    @PostConstruct
    public void initProtobuf() {
        ProtobufUtils.marshaller(PagePB.Response.getDefaultInstance());
    }
}
