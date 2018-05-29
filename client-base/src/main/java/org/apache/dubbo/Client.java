package org.apache.dubbo;

import org.apache.dubbo.benchmark.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        UserService userService = (UserService) context.getBean("userService");
        System.out.println("existUser: " + userService.existUser("user@acme.com"));
        System.out.println("getUser: " + userService.getUser(1));
        System.out.println("createUser: " + userService.createUser(userService.getUser(2)));
        System.out.println("listUser: " + userService.listUser(1));
    }
}
