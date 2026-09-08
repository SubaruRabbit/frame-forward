package com.frameforward.auth.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.auth.model.entity.RefreshSessionEntity;
@Mapper
public interface RefreshSessionMapper extends BaseMapper<RefreshSessionEntity> {
}
