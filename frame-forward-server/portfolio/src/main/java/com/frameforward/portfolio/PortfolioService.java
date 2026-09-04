package com.frameforward.portfolio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.auth.AuthService;
import com.frameforward.evaluation.PortfolioEvaluationQuery;
import com.frameforward.media.PortfolioMediaQuery;

@Service
public class PortfolioService {
    private final AuthService auth;
    private final PortfolioMediaQuery media;
    private final PortfolioEvaluationQuery evaluations;
    private final PortfolioBusiness business;

    public PortfolioService(AuthService auth, PortfolioMediaQuery media, PortfolioEvaluationQuery evaluations,
            PortfolioBusiness business) {
        this.auth = auth;
        this.media = media;
        this.evaluations = evaluations;
        this.business = business;
    }

    public Page list(String token, Filter filter) {
        String accountId = auth.requireAccountId(token);
        Filter safe = safeFilter(filter);
        List<Map<String, Object>> all = new ArrayList<>();
        collectMatchingSummaries(accountId, safe, all);
        return page(all, safe.limit);
    }
    private static Filter safeFilter(Filter filter) {
        return filter == null ? new Filter(null, 20, null, null, null, null) : filter.normalized();
    }
    private void collectMatchingSummaries(String accountId, Filter filter, List<Map<String, Object>> all) {
        for (PortfolioMediaQuery.Item item : media.listOwned(accountId))
            addSummaryIfMatching(accountId, filter, all, item);
    }
    private void addSummaryIfMatching(String accountId, Filter filter, List<Map<String, Object>> all,
            PortfolioMediaQuery.Item item) {
        if (business.deletionStarted(accountId, item.mediaId()) || afterCursor(item, filter.cursor))
            return;
        boolean favorite = business.favorite(accountId, item.mediaId());
        if ((filter.favorite == null || filter.favorite == favorite)
                && matches(item.exif(), filter.subject, filter.camera, filter.lens))
            all.add(summary(accountId, item, favorite));
    }
    private static boolean afterCursor(PortfolioMediaQuery.Item item, String cursor) {
        return cursor != null && item.mediaId().compareTo(cursor) >= 0;
    }
    private static Page page(List<Map<String, Object>> all, int limit) {
        List<Map<String, Object>> items = all.subList(0, Math.min(limit, all.size()));
        String nextCursor = all.size() > items.size() ? String.valueOf(items.getLast().get("mediaId")) : null;
        return new Page(items, nextCursor);
    }

    public Map<String, Object> detail(String token, String mediaId) {
        String accountId = auth.requireAccountId(token);
        if (business.deletionStarted(accountId, mediaId))
            throw new NotFound();
        PortfolioMediaQuery.Item item = media.findOwned(accountId, mediaId);
        if (item == null)
            throw new NotFound();
        Map<String, Object> response = summary(accountId, item, business.favorite(accountId, mediaId));
        PortfolioEvaluationQuery.Detail detail = evaluations.findOwned(accountId, mediaId);
        response.put("exif", item.exif().isEmpty() ? null : item.exif());
        response.put("evaluation", detail.evaluation());
        response.put("sourcePlan", detail.sourcePlan());
        response.put("retake", detail.retake());
        response.put("workflowContext", detail.workflowContext());
        return response;
    }

    @Transactional
    public Favorite setFavorite(String token, String mediaId, boolean value) {
        String accountId = auth.requireAccountId(token);
        return business.setFavorite(accountId, mediaId, value, media.findOwned(accountId, mediaId) != null);
    }

    @Transactional
    public DeletionJob delete(String token, String mediaId) {
        String accountId = auth.requireAccountId(token);
        return business.delete(accountId, mediaId, media.findOwned(accountId, mediaId) != null);
    }

    public DeletionJob deletionStatus(String token, String jobId) {
        String accountId = auth.requireAccountId(token);
        return business.deletionStatus(accountId, jobId);
    }

    private Map<String, Object> summary(String accountId, PortfolioMediaQuery.Item item, boolean favorite) {
        PortfolioEvaluationQuery.Detail detail = evaluations.findOwned(accountId, item.mediaId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mediaId", item.mediaId());
        result.put("width", item.width());
        result.put("height", item.height());
        result.put("subject", subject(item.exif()));
        result.put("camera", camera(item.exif()));
        result.put("lens", lens(item.exif()));
        result.put("favorite", favorite);
        result.put("availability", Map.of("exif", !item.exif().isEmpty(), "evaluation", detail.evaluation() != null,
                "sourcePlan", detail.sourcePlan() != null, "retake", detail.retake() != null));
        return result;
    }
    private static boolean matches(Map<String, String> exif, String subject, String camera, String lens) {
        return contains(subject(exif), subject) && contains(camera(exif), camera) && contains(lens(exif), lens);
    }
    private static boolean contains(String value, String filter) {
        return filter == null || (value != null && value.toLowerCase().contains(filter.toLowerCase()));
    }
    private static String camera(Map<String, String> exif) {
        return first(exif, "camera", "cameraModel", "model");
    }
    private static String lens(Map<String, String> exif) {
        return first(exif, "lens", "lensModel");
    }
    private static String subject(Map<String, String> exif) {
        return first(exif, "subject");
    }
    private static String first(Map<String, String> values, String... keys) {
        for (String key : keys)
            if (values.get(key) != null)
                return values.get(key);
        return null;
    }

    public record Filter(String cursor, Integer limit, String subject, String camera, String lens, Boolean favorite) {
        Filter normalized() {
            return new Filter(cursor, limit == null ? 20 : Math.max(1, Math.min(100, limit)), subject, blank(camera),
                    blank(lens), favorite);
        }
        private static String blank(String value) {
            return value == null || value.isBlank() ? null : value;
        }
    }
    public record Page(List<Map<String, Object>> items, String nextCursor) {
    }
    public record Favorite(String mediaId, boolean favorite) {
    }
    public record DeletionJob(String jobId, String mediaId, String state, String failureReason) {
    }
    public static class NotFound extends RuntimeException {
    }
}
