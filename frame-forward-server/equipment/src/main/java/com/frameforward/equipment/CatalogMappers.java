package com.frameforward.equipment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper interface CameraMapper extends BaseMapper<CameraEntity> {}
@Mapper interface LensMapper extends BaseMapper<LensEntity> {}
@Mapper interface AccessoryTypeMapper extends BaseMapper<AccessoryTypeEntity> {}
