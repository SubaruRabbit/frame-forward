package com.frameforward.media;

/** 删除作品时由作品集模块调用的媒体清理端口。 */
public interface WorkMediaCleanup {
    void deleteForWork(String accountId, String mediaId);
}
