package com.lmxdawn.him.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class HimApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HimApiApplication.class, args);
    }

}
/**
 * 1.选择女游客登入
 * 会依次请求
 * /api/user/login/byTourist
 * /api/user/loginInfo
 * 会打印用户登入   用户登入即建立长连接
 * /api/user/friend/lists
 * /api/group/user/lists
 *
 */

