package com.ttt.one.user;

import com.ttt.one.common.aop.DongTaiProxy;
import com.ttt.one.user.service.UserService;
import com.ttt.one.user.service.impl.UserServiceImpl;
import com.ttt.one.user.vo.UserLoginVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OneUserApplicationTests {

    @Test
    void contextLoads() {
      UserService userservice = (UserService) DongTaiProxy.getDProxty(new UserServiceImpl());
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setLoginacct("123");
        userLoginVo.setPassword("abc");
        userservice.login(userLoginVo);

    }

}
