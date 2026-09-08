package com.frameforward.portfolio.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frameforward.portfolio.model.entity.PortfolioFavoriteEntity;

@Mapper
public interface PortfolioFavoriteMapper extends BaseMapper<PortfolioFavoriteEntity> {
}
