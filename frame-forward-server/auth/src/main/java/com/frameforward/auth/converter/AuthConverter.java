package com.frameforward.auth.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.frameforward.auth.model.dto.AccountDeletionJob;
import com.frameforward.auth.model.entity.AccountDeletionJobEntity;

@Mapper
public interface AuthConverter {

	@Mapping(target = "jobId", source = "job.id")
	@Mapping(target = "deletionToken", source = "deletionToken")
	AccountDeletionJob toDeletionJob(AccountDeletionJobEntity job, String deletionToken);

}
