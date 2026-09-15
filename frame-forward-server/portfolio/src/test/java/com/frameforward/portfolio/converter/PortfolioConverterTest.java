package com.frameforward.portfolio.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.frameforward.portfolio.model.dto.DeletionJob;
import com.frameforward.portfolio.model.dto.FavoriteRequest;
import com.frameforward.portfolio.model.entity.PortfolioFavoriteEntity;
import com.frameforward.portfolio.model.entity.PortfolioWorkDeletionJobEntity;

class PortfolioConverterTest {

	private final PortfolioConverter converter = Mappers.getMapper(PortfolioConverter.class);

	@Test
	void buildsPortfolioModelsAndConvertsDeletionResponse() {
		var favorite = PortfolioFavoriteEntity.builder().mediaId("media").accountId("account").build();
		var deletion = PortfolioWorkDeletionJobEntity.builder().id("job").accountId("account").mediaId("media")
				.state("FAILED").failureReason("retry").build();

		assertThat(favorite.getMediaId()).isEqualTo("media");
		assertThat(deletion.getState()).isEqualTo("FAILED");
		assertThat(converter.toDeletionJob(deletion)).usingRecursiveComparison()
				.isEqualTo(new DeletionJob("job", "media", "FAILED", "retry"));
	}

	@Test
	void keepsFavoriteRequestJavaBeanDefaults() {
		assertThat(FavoriteRequest.builder().favorite(true).build().favorite).isTrue();
		assertThat(new FavoriteRequest().favorite).isFalse();
	}

}
