package com.frameforward.media.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.media.model.entity.MediaEntity;
@Mapper
public interface MediaMapper extends BaseMapper<MediaEntity> {
}
