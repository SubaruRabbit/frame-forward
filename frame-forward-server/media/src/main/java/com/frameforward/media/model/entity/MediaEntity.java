package com.frameforward.media.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("media")
public class MediaEntity {
    @TableId
    public String id;
    public String ownerId;
    public String contentHash;
    public int width;
    public int height;
    public String originalPath;
    public String aiCopyPath;
    public String exifJson;
    public MediaEntity() {
    }
    public MediaEntity(String id, String ownerId, String contentHash, int width, int height, String originalPath,
            String aiCopyPath, String exifJson) {
        this.id = id;
        this.ownerId = ownerId;
        this.contentHash = contentHash;
        this.width = width;
        this.height = height;
        this.originalPath = originalPath;
        this.aiCopyPath = aiCopyPath;
        this.exifJson = exifJson;
    }
    public String getId() {
        return id;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public String getContentHash() {
        return contentHash;
    }
}
