package com.frameforward.equipment;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
interface CameraMapper extends BaseMapper<CameraEntity> {
}
@Mapper
interface LensMapper extends BaseMapper<LensEntity> {
}
@Mapper
interface AccessoryTypeMapper extends BaseMapper<AccessoryTypeEntity> {
}
