package com.frameforward.media.service;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.dto.PortfolioMediaItem;
import com.frameforward.media.model.entity.MediaEntity;

/** 为作品集提供已授权媒体的只读投影，避免跨模块访问媒体表。 */
@Component
public class PortfolioMediaQuery {
    private final MediaManager media;
    private final ObjectMapper json;

    public PortfolioMediaQuery(MediaManager media, ObjectMapper json) {
        this.media = media;
        this.json = json;
    }

    public List<PortfolioMediaItem> listOwned(String accountId) {
        return media.listOwnedDescending(accountId).stream().map(this::item).toList();
    }

    public PortfolioMediaItem findOwned(String accountId, String mediaId) {
        MediaEntity entity = media.findOwnedEntity(accountId, mediaId);
        return entity == null ? null : item(entity);
    }

    private PortfolioMediaItem item(MediaEntity entity) {
        try {
            return new PortfolioMediaItem(entity.id, entity.width, entity.height,
                    json.readValue(entity.exifJson == null ? "{}" : entity.exifJson, new TypeReference<>() {
                    }));
        } catch (Exception exception) {
            throw new IllegalStateException("媒体 EXIF 无法读取", exception);
        }
    }

}
