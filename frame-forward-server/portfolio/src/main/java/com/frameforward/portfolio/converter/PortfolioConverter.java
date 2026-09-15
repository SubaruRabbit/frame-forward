package com.frameforward.portfolio.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.frameforward.portfolio.model.dto.DeletionJob;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;

@Mapper
public interface PortfolioConverter {

	@Mapping(target = "jobId", source = "id")
	DeletionJob toDeletionJob(PortfolioWorkDeletionJobEntity source);

}
