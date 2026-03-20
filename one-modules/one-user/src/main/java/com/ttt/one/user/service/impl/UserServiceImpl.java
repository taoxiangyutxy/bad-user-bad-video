package com.ttt.one.user.service.impl;

import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import com.ttt.one.user.exception.PhoneExistException;
import com.ttt.one.user.exception.UsernameExistException;
import com.ttt.one.user.vo.UserLoginVo;
import com.ttt.one.user.vo.UserRegisterVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ttt.one.user.dao.UserDao;
import com.ttt.one.user.entity.UserEntity;
import com.ttt.one.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserEntity> page = this.page(
                new Query<UserEntity>().getPage(params),
                new QueryWrapper<UserEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegisterVo vo) {
        UserDao baseMapper = this.baseMapper;
        UserEntity entity = new UserEntity();

        //检查用户名和手机号是否唯一 ,抛出异常
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUsername());

        entity.setUsername(vo.getUsername());
        entity.setMobile(vo.getPhone());
        // BCryptPasswordEncoder 密码加密  可自动解密并与明文判断
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);
        entity.setSign(vo.getPassword());
        entity.setCreateTime(new Date());
        entity.setStatus(1);

        baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws  PhoneExistException{
        Integer count = this.baseMapper.selectCount(new QueryWrapper<UserEntity>().eq("mobile", phone));
        if(count> 0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String username) throws  UsernameExistException{
        Integer count = this.baseMapper.selectCount(new QueryWrapper<UserEntity>().eq("username", username));
        if(count> 0){
            throw new UsernameExistException();
        }
    }

    @Override
    public UserEntity login(UserLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        UserEntity userEntity = this.baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", loginacct));
        if(userEntity == null){
            //登录失败
            return null;
        }else{
            String passwordDb = userEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //密码匹配
            boolean b = passwordEncoder.matches(password, passwordDb);
            if(b){
                return userEntity;
            }else{
                return null;
            }
        }
    }
    // @Transactional
    @Override
    public void updateByUserId(UserEntity user) {
        this.baseMapper.updateById(user);
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return this.baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("username", username));
    }

    @Override
    public void resetPassword(String username, String newPassword) {
        UserEntity user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 使用BCrypt加密新密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        user.setSign(newPassword); // 同时更新明文密码字段（如果存在）

        this.baseMapper.updateById(user);
    }
}
