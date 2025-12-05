package com.ttt.one.user.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ttt.one.common.exception.BizExceptionEnum;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.R;
import com.ttt.one.user.exception.PhoneExistException;
import com.ttt.one.user.exception.UsernameExistException;
import com.ttt.one.user.vo.UserLoginVo;
import com.ttt.one.user.vo.UserRegisterVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.ttt.one.user.entity.UserEntity;
import com.ttt.one.user.service.UserService;



/**
 * 会员管理控制器
 *
 * 提供会员的注册、登录、增删改查等功能
 */
@Tag(name = "会员", description = "会员管理")
@RestController
@RequestMapping("/user/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    /**
     * 用户登录
     *
     * @param vo 登录参数
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "根据用户名和密码进行用户登录验证")
    @PostMapping("/login")
    public R login(@RequestBody UserLoginVo vo) {
        /*try {
            TimeUnit.SECONDS.sleep(8); // 延时25秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            e.printStackTrace();
        }*/

        UserEntity userEntity = userService.login(vo);
        if (userEntity != null) {
            return R.ok().setData(userEntity);
        } else {
            return R.error(
                BizExceptionEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), 
                BizExceptionEnum.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg()
            );
        }
    }

    /**
     * 用户注册
     *
     * @param vo 注册参数
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "注册新用户账号")
    @PostMapping("/regist")
    public R regist(@RequestBody UserRegisterVo vo) {
        try {
            userService.regist(vo);
            return R.ok();
        } catch (PhoneExistException e) {
            return R.error("手机号已被注册！");
        } catch (UsernameExistException e) {
            return R.error("用户名已被占用！");
        }
    }

    /**
     * 获取会员列表
     *
     * @param params 查询参数
     * @return 会员列表
     */
    @Operation(summary = "获取会员列表", description = "分页查询系统中的所有会员信息")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = userService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 根据ID获取会员信息
     *
     * @param id 会员ID
     * @return 会员信息
     */
    @Operation(summary = "根据ID获取会员信息", description = "通过会员ID查询指定会员的详细信息")
    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
		UserEntity user = userService.getById(id);

        return R.ok().put("user", user);
    }

    /**
     * 保存会员
     *
     * @param user 会员实体
     * @return 操作结果
     */
    @Operation(summary = "保存会员", description = "创建新的会员信息")
    @PostMapping("/save")
    public R save(@RequestBody UserEntity user) {
		userService.save(user);

        return R.ok();
    }

    /**
     * 更新会员
     *
     * @param user 会员实体
     * @return 操作结果
     */
    @Operation(summary = "更新会员", description = "修改现有会员的信息")
    @PostMapping("/update")
    public R update(@RequestBody UserEntity user) {
		userService.updateById(user);

        return R.ok();
    }

    /**
     * 批量删除会员
     *
     * @param ids 会员ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除会员", description = "根据会员ID数组批量删除会员信息")
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
		userService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
