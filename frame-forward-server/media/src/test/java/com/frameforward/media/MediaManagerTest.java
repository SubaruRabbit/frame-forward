package com.frameforward.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

class MediaManagerTest {

    @Test
    void delegatesOwnedLookupListPersistenceAndDeletionToMapper() {
        MediaMapper mapper = mock(MediaMapper.class);
        MediaManager manager = new MediaManager(mapper);
        MediaEntity entity = new MediaEntity();
        when(mapper.selectOne(any())).thenReturn(entity);
        when(mapper.selectById("media-1")).thenReturn(entity);
        when(mapper.selectList(any())).thenReturn(List.of(entity));

        assertSame(entity, manager.findExisting("owner", "hash"));
        assertSame(entity, manager.findOwned("owner", "media-1"));
        assertSame(entity, manager.findById("media-1"));
        assertEquals(List.of(entity), manager.listOwned("owner"));
        assertEquals(List.of(entity), manager.listOwnedDescending("owner"));
        manager.save(entity);
        manager.delete("media-1");

        verify(mapper).insert(entity);
        verify(mapper).deleteById("media-1");
    }
}
