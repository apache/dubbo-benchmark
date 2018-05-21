package org.apache.dubbo.benchmark.service;


import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;

public interface UserService {
    public boolean existUser(String email);

    public boolean createUser(User user);

    public User getUser(long id);

    public Page<User> listUser(int pageNo);

}

