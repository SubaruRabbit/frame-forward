package com.frameforward.evaluation;

import java.time.Instant;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.RetakeComparisonGraph;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaMapper;

@Service
public class RetakeComparisonService {
    private final AuthService auth;
    private final PhotoEvaluationMapper evaluations;
    private final RetakeLinkMapper links;
    private final ShootingSessionMapper sessions;
    private final MediaMapper media;
    private final RetakeComparisonGraph graph;
    private final ObjectMapper json;
    public RetakeComparisonService(AuthService auth, PhotoEvaluationMapper evaluations, RetakeLinkMapper links,
            ShootingSessionMapper sessions, MediaMapper media, RetakeComparisonGraph graph, ObjectMapper json) {
        this.auth = auth;
        this.evaluations = evaluations;
        this.links = links;
        this.sessions = sessions;
        this.media = media;
        this.graph = graph;
        this.json = json;
    }
    @Transactional
    public Map<String, Object> create(String token, Request request) {
        if (request == null || blank(request.originalEvaluationId) || blank(request.retakeEvaluationId)
                || request.originalEvaluationId.equals(request.retakeEvaluationId))
            throw new Invalid();
        String account = auth.requireAccountId(token);
        PhotoEvaluationEntity original = owned(account, request.originalEvaluationId);
        PhotoEvaluationEntity retake = owned(account, request.retakeEvaluationId);
        if (blank(original.sessionId) || !Objects.equals(original.sessionId, retake.sessionId)
                || sessions.selectOne(new LambdaQueryWrapper<ShootingSessionEntity>()
                        .eq(ShootingSessionEntity::getId, original.sessionId)
                        .eq(ShootingSessionEntity::getAccountId, account)) == null)
            throw new Invalid();
        Map<String, Object> result = compare(original, retake);
        RetakeLinkEntity link = links.selectById(retake.id);
        if (link == null) {
            link = new RetakeLinkEntity();
            link.retakeEvaluationId = retake.id;
            link.originalEvaluationId = original.id;
            link.accountId = account;
            link.sessionId = original.sessionId;
            link.createdAt = Instant.now();
            links.insert(link);
        }
        return result;
    }
    public Map<String, Object> get(String token, String retakeId) {
        String account = auth.requireAccountId(token);
        RetakeLinkEntity link = links.selectOne(new LambdaQueryWrapper<RetakeLinkEntity>()
                .eq(RetakeLinkEntity::getRetakeEvaluationId, retakeId).eq(RetakeLinkEntity::getAccountId, account));
        if (link == null)
            throw new NotFound();
        return compare(owned(account, link.originalEvaluationId), owned(account, link.retakeEvaluationId));
    }
    private Map<String, Object> compare(PhotoEvaluationEntity original, PhotoEvaluationEntity retake) {
        if (blank(original.resultJson) || blank(retake.resultJson))
            throw new Invalid();
        Map<String, Object> before = read(original.resultJson), after = read(retake.resultJson);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("original", snapshot(original, before));
        response.put("retake", snapshot(retake, after));
        response.put("scoreDelta", number(after.get("total")) - number(before.get("total")));
        response.put("dimensionChanges", dimensions(before, after));
        ShootingSessionEntity session = sessions.selectById(original.sessionId);
        response.put("compositionChanges", List.of(session.planContext));
        response.put("parameterChanges", parameters(original.mediaId, retake.mediaId));
        response.putAll(graph.summarize(before, after));
        return response;
    }
    private Map<String, Object> snapshot(PhotoEvaluationEntity evaluation, Map<String, Object> result) {
        return Map.of("mediaId", evaluation.mediaId, "score", number(result.get("total")));
    }
    private Map<String, Integer> dimensions(Map<String, Object> before, Map<String, Object> after) {
        Map<String, Integer> delta = new LinkedHashMap<>();
        Map<String, Object> a = map(before.get("dimensions")), b = map(after.get("dimensions"));
        for (String key : a.keySet())
            if (b.containsKey(key))
                delta.put(key, number(b.get(key)) - number(a.get(key)));
        return delta;
    }
    private Map<String, Object> parameters(String originalId, String retakeId) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, Object> a = exif(originalId), b = exif(retakeId);
        for (String key : List.of("fNumber", "exposureTime", "iso", "focalLength"))
            if (a.containsKey(key) || b.containsKey(key))
                result.put(key, Map.of("before", String.valueOf(a.getOrDefault(key, "未提供")), "after",
                        String.valueOf(b.getOrDefault(key, "未提供"))));
        return result;
    }
    private Map<String, Object> exif(String mediaId) {
        MediaEntity item = media.selectById(mediaId);
        return item == null || blank(item.exifJson) ? Map.of() : read(item.exifJson);
    }
    private PhotoEvaluationEntity owned(String account, String id) {
        PhotoEvaluationEntity item = evaluations.selectOne(new LambdaQueryWrapper<PhotoEvaluationEntity>()
                .eq(PhotoEvaluationEntity::getId, id).eq(PhotoEvaluationEntity::getAccountId, account));
        if (item == null)
            throw new NotFound();
        return item;
    }
    private Map<String, Object> read(String source) {
        try {
            return json.readValue(source, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new Invalid();
        }
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> map(Object value) {
        return value instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }
    private static int number(Object value) {
        return value instanceof Number n ? n.intValue() : 0;
    }
    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
    public static class Request {
        public String originalEvaluationId, retakeEvaluationId;
    }
    public static class Invalid extends RuntimeException {
    }
    public static class NotFound extends RuntimeException {
    }
}
