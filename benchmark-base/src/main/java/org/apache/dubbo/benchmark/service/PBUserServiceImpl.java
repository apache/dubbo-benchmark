package org.apache.dubbo.benchmark.service;

import com.google.protobuf.util.Timestamps;
import org.apache.dubbo.benchmark.bean.PagePB;
import org.apache.dubbo.benchmark.bean.UserServiceDubbo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author zhengzechao
 * @date 2020-03-06
 * Email ooczzoo@gmail.com
 */
public class PBUserServiceImpl implements UserServiceDubbo.IUserService {

    @Override
    public PagePB.Response createUser(PagePB.Request request) {
        final PagePB.Response.Builder builder = PagePB.Response.newBuilder();
        if (request.getUser() == null) {
            builder.setState(false);
        } else {
            builder.setState(true);
        }
        return builder.build();
    }

    @Override
    public CompletableFuture<PagePB.Response> createUserAsync(PagePB.Request request) {
        return null;
    }

    @Override
    public PagePB.Response existUser(PagePB.Request request) {
        String email = request.getEmail();
        final PagePB.Response.Builder builder = PagePB.Response.newBuilder();
        if (email == null || email.isEmpty()) {
            builder.setState(true);
        }else if (email.charAt(email.length() - 1) < '5') {
            builder.setState(false);
        }

        builder.setState(true);
        return builder.build();
    }

    @Override
    public CompletableFuture<PagePB.Response> existUserAsync(PagePB.Request request) {
        return null;
    }

    @Override
    public PagePB.Response listUser(PagePB.Request request) {

        final PagePB.Page.Builder page = PagePB.Page.newBuilder();
        List<PagePB.User> userList = new ArrayList<>(15);

        for (int i = 0; i < 15; i++) {
            final PagePB.User.Builder user = PagePB.User.newBuilder();

            user.setId(i);
            user.setName(new String("Doug Lea"));
            user.setSex(1);
            user.setBirthday(Timestamps.fromMillis(System.currentTimeMillis()));
            user.setEmail(new String("dong.lea@gmail.com"));
            user.setMobile(new String("18612345678"));
            user.setAddress(new String("北京市 中关村 中关村大街1号 鼎好大厦 1605"));
            user.setIcon(new String("https://www.baidu.com/img/bd_logo1.png"));
            user.setStatus(1);
            user.setCreateTime(Timestamps.fromMillis(System.currentTimeMillis()));
            user.setUpdateTime(user.getCreateTime());
            List<Integer> permissions = new ArrayList<Integer>(
                    Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 19, 88, 86, 89, 90, 91, 92));
            user.addAllPermissions(permissions);
            userList.add(user.build());
        }

        page.setPageNo(request.getPage());
        page.setTotal(1000);
        page.addAllUsers(userList);

        return PagePB.Response.newBuilder().setPage(page.build()).build();
    }

    @Override
    public CompletableFuture<PagePB.Response> listUserAsync(PagePB.Request request) {
        return null;
    }

    @Override
    public PagePB.Response getUser(PagePB.Request request) {
        final long id = request.getId();
        final PagePB.User.Builder user = PagePB.User.newBuilder();
        user.setId(id);
        user.setName(new String("Doug Lea"));
        user.setSex(1);
        user.setBirthday(Timestamps.fromMillis(System.currentTimeMillis()));
        user.setEmail(new String("dong.lea@gmail.com"));
        user.setMobile(new String("18612345678"));
        user.setAddress(new String("北京市 中关村 中关村大街1号 鼎好大厦 1605"));
        user.setIcon(new String("https://www.baidu.com/img/bd_logo1.png"));
        user.setStatus(1);
        user.setCreateTime(Timestamps.fromMillis(System.currentTimeMillis()));
        user.setUpdateTime(user.getCreateTime());
        List<Integer> permissions = new ArrayList<Integer>(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 19, 88, 86, 89, 90, 91, 92));
        user.addAllPermissions(permissions);

        return PagePB.Response.newBuilder().setUser(user.build()).build();
    }

    @Override
    public CompletableFuture<PagePB.Response> getUserAsync(PagePB.Request request) {
        return null;
    }

}
