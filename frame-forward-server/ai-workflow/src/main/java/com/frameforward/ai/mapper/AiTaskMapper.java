package com.frameforward.ai.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.ai.model.entity.AiTaskEntity;
@Mapper
public interface AiTaskMapper extends BaseMapper<AiTaskEntity> {
}
