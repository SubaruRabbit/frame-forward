package com.frameforward.media.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.media.mapper.MediaMapper;
import com.frameforward.media.model.entity.MediaEntity;
import com.frameforward.media.repository.MediaRepository;

class MediaManagerTest {

	@Test
	void delegatesOwnedLookupListPersistenceAndDeletionToMapper() {
		MediaMapper mapper = mock(MediaMapper.class);
		MediaManager manager = new MediaManager(new MediaRepository(mapper));
		MediaEntity entity = new MediaEntity();
		when(mapper.selectOne(any())).thenReturn(entity);
		when(mapper.selectById("media-1")).thenReturn(entity);
		when(mapper.selectList(any())).thenReturn(List.of(entity));

		assertSame(entity, manager.findExisting("owner", "hash"));
		assertSame(entity, manager.findOwnedEntity("owner", "media-1"));
		assertSame(entity, manager.findEntityById("media-1"));
		assertEquals(List.of(entity), manager.listOwned("owner"));
		assertEquals(List.of(entity), manager.listOwnedDescending("owner"));
		manager.save(entity);
		manager.delete("media-1");

		verify(mapper).insert(entity);
		verify(mapper).deleteById("media-1");
	}

	@Test
	void queriesPreserveOwnershipHashAndDescendingOrder() {
		TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "media-test"),
				MediaEntity.class);
		MediaMapper mapper = mock(MediaMapper.class);
		MediaManager manager = new MediaManager(new MediaRepository(mapper));
		manager.findExisting("owner", "hash");
		manager.findOwnedEntity("owner", "id");
		var queries = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
		Mockito.verify(mapper, Mockito.times(2)).selectOne(queries.capture());
		var existing = queries.getAllValues().get(0);
		var owned = queries.getAllValues().get(1);
		Assertions.assertThat(existing.getSqlSegment()).contains("owner_id", "content_hash");
		Assertions.assertThat(existing.getParamNameValuePairs()).containsValue("owner").containsValue("hash");
		Assertions.assertThat(owned.getSqlSegment()).contains("owner_id", "id");
		Assertions.assertThat(owned.getParamNameValuePairs()).containsValue("owner").containsValue("id");
		manager.listOwnedDescending("owner");
		verify(mapper).selectList(queries.capture());
		var ordered = queries.getValue();
		Assertions.assertThat(ordered.getSqlSegment()).contains("owner_id", "ORDER BY id DESC");
		Assertions.assertThat(ordered.getParamNameValuePairs()).containsValue("owner");
	}

}
