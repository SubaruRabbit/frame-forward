package com.frameforward.portfolio;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.auth.AuthService;
import com.frameforward.evaluation.PortfolioEvaluationQuery;
import com.frameforward.evaluation.WorkEvaluationCleanup;
import com.frameforward.media.PortfolioMediaQuery;
import com.frameforward.media.WorkMediaCleanup;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {
  private final AuthService auth;
  private final PortfolioMediaQuery media;
  private final PortfolioEvaluationQuery evaluations;
  private final PortfolioFavoriteMapper favorites;
  private final PortfolioWorkDeletionJobMapper deletionJobs;
  private final WorkEvaluationCleanup evaluationCleanup;
  private final WorkMediaCleanup mediaCleanup;

  public PortfolioService(AuthService auth, PortfolioMediaQuery media, PortfolioEvaluationQuery evaluations, PortfolioFavoriteMapper favorites, PortfolioWorkDeletionJobMapper deletionJobs, WorkEvaluationCleanup evaluationCleanup, WorkMediaCleanup mediaCleanup) {
    this.auth = auth; this.media = media; this.evaluations = evaluations; this.favorites = favorites; this.deletionJobs = deletionJobs; this.evaluationCleanup = evaluationCleanup; this.mediaCleanup = mediaCleanup;
  }

  public Page list(String token, Filter filter) {
    String accountId = auth.requireAccountId(token); Filter safe = filter == null ? new Filter(null, 20, null, null, null, null) : filter.normalized();
    List<Map<String, Object>> all = new ArrayList<>();
    for (PortfolioMediaQuery.Item item : media.listOwned(accountId)) {
      if (deletionStarted(accountId, item.mediaId())) continue;
      if (safe.cursor != null && item.mediaId().compareTo(safe.cursor) >= 0) continue;
      boolean favorite = favorite(accountId, item.mediaId());
      if ((safe.favorite == null || safe.favorite == favorite) && matches(item.exif(), safe.subject, safe.camera, safe.lens)) all.add(summary(accountId, item, favorite));
    }
    List<Map<String, Object>> items = all.subList(0, Math.min(safe.limit, all.size()));
    String nextCursor = all.size() > items.size() ? String.valueOf(items.getLast().get("mediaId")) : null;
    return new Page(items, nextCursor);
  }

  public Map<String, Object> detail(String token, String mediaId) {
    String accountId = auth.requireAccountId(token); if (deletionStarted(accountId, mediaId)) throw new NotFound(); PortfolioMediaQuery.Item item = media.findOwned(accountId, mediaId);
    if (item == null) throw new NotFound();
    Map<String, Object> response = summary(accountId, item, favorite(accountId, mediaId));
    PortfolioEvaluationQuery.Detail detail = evaluations.findOwned(accountId, mediaId);
    response.put("exif", item.exif().isEmpty() ? null : item.exif()); response.put("evaluation", detail.evaluation()); response.put("sourcePlan", detail.sourcePlan()); response.put("retake", detail.retake());
    return response;
  }

  @Transactional public Favorite setFavorite(String token, String mediaId, boolean value) {
    String accountId = auth.requireAccountId(token); if (deletionStarted(accountId, mediaId) || media.findOwned(accountId, mediaId) == null) throw new NotFound();
    PortfolioFavoriteEntity current = favorites.selectOne(new LambdaQueryWrapper<PortfolioFavoriteEntity>().eq(PortfolioFavoriteEntity::getMediaId, mediaId).eq(PortfolioFavoriteEntity::getAccountId, accountId));
    if (value && current == null) { PortfolioFavoriteEntity created = new PortfolioFavoriteEntity(); created.mediaId = mediaId; created.accountId = accountId; created.createdAt = Instant.now(); favorites.insert(created); }
    if (!value && current != null) favorites.deleteById(current.mediaId);
    return new Favorite(mediaId, value);
  }

  @Transactional public DeletionJob delete(String token, String mediaId) {
    String accountId = auth.requireAccountId(token);
    PortfolioWorkDeletionJobEntity job = deletionJobs.selectOne(new LambdaQueryWrapper<PortfolioWorkDeletionJobEntity>().eq(PortfolioWorkDeletionJobEntity::getAccountId, accountId).eq(PortfolioWorkDeletionJobEntity::getMediaId, mediaId));
    if (job == null) {
      if (media.findOwned(accountId, mediaId) == null) throw new NotFound();
      job = new PortfolioWorkDeletionJobEntity(); job.id = UUID.randomUUID().toString(); job.accountId = accountId; job.mediaId = mediaId; job.state = "PENDING"; job.createdAt = Instant.now(); job.updatedAt = job.createdAt; deletionJobs.insert(job);
    }
    if (!"COMPLETED".equals(job.state)) process(job);
    return deletionJob(job);
  }

  public DeletionJob deletionStatus(String token, String jobId) {
    String accountId = auth.requireAccountId(token);
    PortfolioWorkDeletionJobEntity job = deletionJobs.selectOne(new LambdaQueryWrapper<PortfolioWorkDeletionJobEntity>().eq(PortfolioWorkDeletionJobEntity::getId, jobId).eq(PortfolioWorkDeletionJobEntity::getAccountId, accountId));
    if (job == null) throw new NotFound();
    return deletionJob(job);
  }

  private void process(PortfolioWorkDeletionJobEntity job) {
    job.state = "IN_PROGRESS"; job.failureReason = null; job.updatedAt = Instant.now(); deletionJobs.updateById(job);
    try {
      evaluationCleanup.deleteForWork(job.accountId, job.mediaId);
      favorites.delete(new LambdaQueryWrapper<PortfolioFavoriteEntity>().eq(PortfolioFavoriteEntity::getMediaId, job.mediaId).eq(PortfolioFavoriteEntity::getAccountId, job.accountId));
      mediaCleanup.deleteForWork(job.accountId, job.mediaId);
      job.state = "COMPLETED";
    } catch (RuntimeException exception) {
      job.state = "FAILED"; job.failureReason = "关联数据清理失败，请重试。";
    }
    job.updatedAt = Instant.now(); deletionJobs.updateById(job);
  }

  private Map<String, Object> summary(String accountId, PortfolioMediaQuery.Item item, boolean favorite) {
    PortfolioEvaluationQuery.Detail detail = evaluations.findOwned(accountId, item.mediaId()); Map<String, Object> result = new LinkedHashMap<>();
    result.put("mediaId", item.mediaId()); result.put("width", item.width()); result.put("height", item.height()); result.put("subject", subject(item.exif())); result.put("camera", camera(item.exif())); result.put("lens", lens(item.exif())); result.put("favorite", favorite);
    result.put("availability", Map.of("exif", !item.exif().isEmpty(), "evaluation", detail.evaluation() != null, "sourcePlan", detail.sourcePlan() != null, "retake", detail.retake() != null)); return result;
  }
  private boolean favorite(String accountId, String mediaId) { return favorites.selectCount(new LambdaQueryWrapper<PortfolioFavoriteEntity>().eq(PortfolioFavoriteEntity::getMediaId, mediaId).eq(PortfolioFavoriteEntity::getAccountId, accountId)) > 0; }
  private boolean deletionStarted(String accountId, String mediaId) { return deletionJobs.selectCount(new LambdaQueryWrapper<PortfolioWorkDeletionJobEntity>().eq(PortfolioWorkDeletionJobEntity::getAccountId, accountId).eq(PortfolioWorkDeletionJobEntity::getMediaId, mediaId)) > 0; }
  private static DeletionJob deletionJob(PortfolioWorkDeletionJobEntity job) { return new DeletionJob(job.id, job.mediaId, job.state, job.failureReason); }
  private static boolean matches(Map<String, String> exif, String subject, String camera, String lens) { return contains(subject(exif), subject) && contains(camera(exif), camera) && contains(lens(exif), lens); }
  private static boolean contains(String value, String filter) { return filter == null || (value != null && value.toLowerCase().contains(filter.toLowerCase())); }
  private static String camera(Map<String, String> exif) { return first(exif, "camera", "cameraModel", "model"); }
  private static String lens(Map<String, String> exif) { return first(exif, "lens", "lensModel"); }
  private static String subject(Map<String, String> exif) { return first(exif, "subject"); }
  private static String first(Map<String, String> values, String... keys) { for (String key : keys) if (values.get(key) != null) return values.get(key); return null; }

  public record Filter(String cursor, Integer limit, String subject, String camera, String lens, Boolean favorite) { Filter normalized() { return new Filter(cursor, limit == null ? 20 : Math.max(1, Math.min(100, limit)), subject, blank(camera), blank(lens), favorite); } private static String blank(String value) { return value == null || value.isBlank() ? null : value; } }
  public record Page(List<Map<String, Object>> items, String nextCursor) {}
  public record Favorite(String mediaId, boolean favorite) {}
  public record DeletionJob(String jobId, String mediaId, String state, String failureReason) {}
  public static class NotFound extends RuntimeException {}
}
