package com.frameforward.media;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 为作品集提供已授权媒体的只读投影，避免跨模块访问媒体表。 */
@Component
public class PortfolioMediaQuery {
  private final MediaMapper media;
  private final ObjectMapper json;

  public PortfolioMediaQuery(MediaMapper media, ObjectMapper json) { this.media = media; this.json = json; }

  public List<Item> listOwned(String accountId) {
    return media.selectList(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getOwnerId, accountId).orderByDesc(MediaEntity::getId)).stream().map(this::item).toList();
  }

  public Item findOwned(String accountId, String mediaId) {
    MediaEntity entity = media.selectOne(new LambdaQueryWrapper<MediaEntity>().eq(MediaEntity::getId, mediaId).eq(MediaEntity::getOwnerId, accountId));
    return entity == null ? null : item(entity);
  }

  private Item item(MediaEntity entity) {
    try { return new Item(entity.id, entity.width, entity.height, json.readValue(entity.exifJson == null ? "{}" : entity.exifJson, new TypeReference<>() {})); }
    catch (Exception exception) { throw new IllegalStateException("媒体 EXIF 无法读取", exception); }
  }

  public record Item(String mediaId, int width, int height, Map<String, String> exif) {}
}
