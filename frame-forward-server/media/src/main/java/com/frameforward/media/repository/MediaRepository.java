package com.frameforward.media.repository;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.media.mapper.MediaMapper;
import com.frameforward.media.model.entity.MediaEntity;

/** 统一协调媒体持久化访问，避免业务服务直接依赖 Mapper。 */
@Repository
public class MediaRepository {
    private final MediaMapper media;

    public MediaRepository(MediaMapper media) {
        this.media = media;
    }

    public MediaEntity findExisting(String ownerId, String contentHash) {
        return media.selectOne(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getOwnerId, ownerId)
                .eq(MediaEntity::getContentHash, contentHash));
    }

    public MediaEntity findOwned(String ownerId, String mediaId) {
        return media.selectOne(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getId, mediaId)
                .eq(MediaEntity::getOwnerId, ownerId));
    }

    public MediaEntity findById(String mediaId) {
        return media.selectById(mediaId);
    }

    public List<MediaEntity> listOwned(String ownerId) {
        return media.selectList(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getOwnerId, ownerId));
    }

    public List<MediaEntity> listOwnedDescending(String ownerId) {
        return media.selectList(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getOwnerId, ownerId)
                .orderByDesc(MediaEntity::getId));
    }

    public void save(MediaEntity entity) {
        media.insert(entity);
    }

    public void delete(String mediaId) {
        media.deleteById(mediaId);
    }
}
