package com.frameforward.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.media.repository.MediaRepository;

class MediaEntityMigrationTest {

	@Test
	void canonicalEntityPreservesTableAndColumns() throws Exception {
		Class<?> entity = Class.forName("com.frameforward.media.model.entity.MediaEntity");
		var table = TableInfoHelper
				.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "canonical-media"), entity);
		assertThat(table.getTableName()).isEqualTo("media");
		assertThat(table.getKeyColumn()).isEqualTo("id");
		assertThat(table.getFieldList()).extracting("column").containsExactlyInAnyOrder("owner_id", "content_hash",
				"width", "height", "original_path", "ai_copy_path", "exif_json");
	}

	@Test
	void canonicalQueriesReturnTheSameRepositoryResults() throws Exception {
		Class<?> entity = Class.forName("com.frameforward.media.model.entity.MediaEntity");
		var repository = mock(MediaRepository.class);
		var manager = new MediaManager(repository);
		var result = new MediaEntity("id", "owner", "hash", 20, 10, "original", "copy", "{}");
		when(repository.findOwned("owner", "id")).thenReturn(result);
		when(repository.findById("id")).thenReturn(result);
		var owned = MediaManager.class.getMethod("findOwnedEntity", String.class, String.class);
		var byId = MediaManager.class.getMethod("findEntityById", String.class);
		assertThat(owned.getReturnType()).isEqualTo(entity);
		assertThat(byId.getReturnType()).isEqualTo(entity);
		assertThat(owned.invoke(manager, "owner", "id")).isSameAs(result);
		assertThat(byId.invoke(manager, "id")).isSameAs(result);
		verify(repository).findOwned("owner", "id");
		verify(repository).findById("id");
	}

}
