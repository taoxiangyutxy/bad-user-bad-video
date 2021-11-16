package com.ttt.one.user.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import com.ttt.one.user.exception.PhoneExistException;
import com.ttt.one.user.exception.UsernameExistException;
import com.ttt.one.user.vo.UserLoginVo;
import com.ttt.one.user.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.user.entity.UserEntity;
import com.ttt.one.user.service.UserService;



/**
 * 会员
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-10-17 16:24:20
 */
@RestController
@RequestMapping("user/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R login(@RequestBody UserLoginVo vo){
       UserEntity userEntity =  userService.login(vo);
       if(userEntity!=null){
           return R.ok().put("data",userEntity);
       }else {
           return R.error("登录失败！");
       }
    }

    /**
     * 注册用户
     * @param vo
     * @return
     */
    @PostMapping("/regist")
    public R regist(@RequestBody UserRegistVo vo){
        try {
            userService.regist(vo);
        }catch (PhoneExistException e){
            return R.error("手机号异常！");
        }catch (UsernameExistException e){
            return R.error("用户名异常！");
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("user:user:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
   // @RequiresPermissions("user:user:info")
    public R info(@PathVariable("id") Long id){
		UserEntity user = userService.getById(id);

        return R.ok().put("user", user);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("user:user:save")
    public R save(@RequestBody UserEntity user){
		userService.save(user);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("user:user:update")
    public R update(@RequestBody UserEntity user){
		userService.updateById(user);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
  //  @RequiresPermissions("user:user:delete")
    public R delete(@RequestBody Long[] ids){
		userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
