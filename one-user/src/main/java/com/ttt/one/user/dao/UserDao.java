package com.ttt.one.user.dao;

import com.ttt.one.user.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author ttt
 * @email 496427196@qq.com
 * @date 2021-10-17 16:24:20
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
	
}
