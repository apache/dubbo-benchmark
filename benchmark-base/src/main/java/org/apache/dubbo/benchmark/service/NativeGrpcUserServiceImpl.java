/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.benchmark.service;

import io.grpc.stub.StreamObserver;
import org.apache.dubbo.benchmark.bean.PagePB;
import org.apache.dubbo.benchmark.bean.UserServiceGrpc;

public class NativeGrpcUserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final GrpcUserServiceImpl grpcUserService = new GrpcUserServiceImpl();

    @Override
    public void existUser(PagePB.Request request, StreamObserver<PagePB.Response> responseObserver) {
        grpcUserService.existUser(request, responseObserver);
    }

    @Override
    public void createUser(PagePB.Request request, StreamObserver<PagePB.Response> responseObserver) {
        grpcUserService.createUser(request, responseObserver);
    }

    @Override
    public void getUser(PagePB.Request request, StreamObserver<PagePB.Response> responseObserver) {
        grpcUserService.getUser(request, responseObserver);
    }

    @Override
    public void listUser(PagePB.Request request, StreamObserver<PagePB.Response> responseObserver) {
        grpcUserService.listUser(request, responseObserver);
    }
}
