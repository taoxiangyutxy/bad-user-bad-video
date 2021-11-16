package com.ttt.one.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.user.entity.UserEntity;
import com.ttt.one.user.exception.PhoneExistException;
import com.ttt.one.user.exception.UsernameExistException;
import com.ttt.one.user.vo.UserLoginVo;
import com.ttt.one.user.vo.UserRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-10-17 16:24:20
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     *
     * @param vo
     */
    void regist(UserRegistVo vo);

    /**
     * 检查手机号是否唯一
     * @param phone
     * @return
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;

    /**
     * 检查用户名是否唯一
     * @param username
     * @return
     */
    void checkUserNameUnique(String username) throws UsernameExistException;

    /**
     *登录
     * @param vo:  实体类
     * @return UserEntity
     * @author txy
     * @description
     * @date 2021/11/6 19:08
     */
    UserEntity login(UserLoginVo vo);
}

