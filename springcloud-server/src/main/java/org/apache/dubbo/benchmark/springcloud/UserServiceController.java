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
package org.apache.dubbo.benchmark.springcloud;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.service.UserService;
import org.apache.dubbo.benchmark.service.UserServiceServerImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserServiceController implements UserService {

    private final UserService userService = new UserServiceServerImpl();

    @PostMapping("/existUser")
    public boolean existUser(@RequestParam(name = "email") String email) {
        return userService.existUser(email);
    }

    @PostMapping("/createUser")
    public boolean createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/getUser")
    public User getUser(@RequestParam(name = "id") long id) {
        return userService.getUser(id);
    }

    @PostMapping("/listUser")
    public Page<User> listUser(@RequestParam(name = "pageNo") int pageNo) {
        return userService.listUser(pageNo);
    }
}
