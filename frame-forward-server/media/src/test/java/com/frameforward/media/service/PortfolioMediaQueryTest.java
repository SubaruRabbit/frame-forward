package com.frameforward.media.service;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;
class PortfolioMediaQueryTest {
    private final MediaManager manager = mock(MediaManager.class);
    private final PortfolioMediaQuery query = new PortfolioMediaQuery(manager, new ObjectMapper());
    @Test
    void mapsOwnedProjectionAndKeepsRepositoryOrdering() {
        var first = entity("z", "{\"camera\":\"Sony\"}");
        var second = entity("a", null);
        when(manager.listOwnedDescending("owner")).thenReturn(List.of(first, second));
        var items = query.listOwned("owner");
        assertThat(items).extracting("mediaId").containsExactly("z", "a");
        assertThat(items.getFirst().exif()).isEqualTo(Map.of("camera", "Sony"));
        assertThat(items.getLast().exif()).isEmpty();
        assertThat(items.getFirst().width()).isEqualTo(20);
        assertThat(items.getFirst().height()).isEqualTo(10);
        verify(manager).listOwnedDescending("owner");
    }
    @Test
    void returnsNullForMissingOrUnownedProjection() {
        assertThat(query.findOwned("other", "id")).isNull();
        when(manager.findOwnedEntity("owner", "id")).thenReturn(entity("id", "{}"));
        assertThat(query.findOwned("owner", "id").mediaId()).isEqualTo("id");
    }
    @Test void invalidExifRetainsDiagnosticFailure() {
        when(manager.findOwnedEntity("owner", "id")).thenReturn(entity("id", "invalid"));
        assertThatThrownBy(() -> query.findOwned("owner", "id")).isInstanceOf(IllegalStateException.class)
                .hasMessage("媒体 EXIF 无法读取").hasCauseInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }
    private static MediaEntity entity(String id, String exif) {
        return new MediaEntity(id, "owner", "hash", 20, 10, "original", "copy", exif);
    }
}
