package com.frameforward.media.manager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.frameforward.media.mapper.MediaMapper;
import com.frameforward.media.model.entity.MediaEntity;

class MediaManagerTest {

    @Test
    void delegatesOwnedLookupListPersistenceAndDeletionToMapper() {
        MediaMapper mapper = mock(MediaMapper.class);
        MediaManager manager = new MediaManager(new com.frameforward.media.repository.MediaRepository(mapper));
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
        com.baomidou.mybatisplus.core.metadata.TableInfoHelper
                .initTableInfo(
                        new org.apache.ibatis.builder.MapperBuilderAssistant(
                                new com.baomidou.mybatisplus.core.MybatisConfiguration(), "media-test"),
                        MediaEntity.class);
        MediaMapper mapper = mock(MediaMapper.class);
        MediaManager manager = new MediaManager(new com.frameforward.media.repository.MediaRepository(mapper));
        manager.findExisting("owner", "hash");
        manager.findOwnedEntity("owner", "id");
        var queries = org.mockito.ArgumentCaptor
                .forClass(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper.class);
        org.mockito.Mockito.verify(mapper, org.mockito.Mockito.times(2)).selectOne(queries.capture());
        var existing = queries.getAllValues().get(0);
        var owned = queries.getAllValues().get(1);
        org.assertj.core.api.Assertions.assertThat(existing.getSqlSegment()).contains("owner_id", "content_hash");
        org.assertj.core.api.Assertions.assertThat(existing.getParamNameValuePairs()).containsValue("owner")
                .containsValue("hash");
        org.assertj.core.api.Assertions.assertThat(owned.getSqlSegment()).contains("owner_id", "id");
        org.assertj.core.api.Assertions.assertThat(owned.getParamNameValuePairs()).containsValue("owner")
                .containsValue("id");
        manager.listOwnedDescending("owner");
        verify(mapper).selectList(queries.capture());
        var ordered = queries.getValue();
        org.assertj.core.api.Assertions.assertThat(ordered.getSqlSegment()).contains("owner_id", "ORDER BY id DESC");
        org.assertj.core.api.Assertions.assertThat(ordered.getParamNameValuePairs()).containsValue("owner");
    }
}
