package com.frameforward.auth.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.auth.model.entity.AccountEntity;
@Mapper
public interface AccountMapper extends BaseMapper<AccountEntity> {
}
