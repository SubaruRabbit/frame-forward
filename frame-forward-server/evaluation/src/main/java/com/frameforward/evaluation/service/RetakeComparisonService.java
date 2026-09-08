package com.frameforward.evaluation.service;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frameforward.ai.business.RetakeComparisonGraph;
import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.business.RetakeComparisonInvalid;
import com.frameforward.evaluation.business.RetakeComparisonNotFound;
import com.frameforward.evaluation.manager.EvaluationManager;
import com.frameforward.evaluation.model.dto.RetakeComparisonRequest;
import com.frameforward.evaluation.model.entity.PhotoEvaluationEntity;
import com.frameforward.evaluation.model.entity.RetakeLinkEntity;
import com.frameforward.evaluation.model.entity.ShootingSessionEntity;
import com.frameforward.media.manager.MediaManager;
import com.frameforward.media.model.entity.MediaEntity;

@Service
public class RetakeComparisonService {
    private final AuthService auth;
    private final EvaluationManager manager;
    private final MediaManager media;
    private final RetakeComparisonGraph graph;
    private final ObjectMapper json;
    public RetakeComparisonService(AuthService auth, EvaluationManager manager, MediaManager media,
            RetakeComparisonGraph graph, ObjectMapper json) {
        this.auth = auth;
        this.manager = manager;
        this.media = media;
        this.graph = graph;
        this.json = json;
    }
    @Transactional
    public Map<String, Object> create(String token, RetakeComparisonRequest request) {
        validate(request);
        String account = auth.requireAccountId(token);
        PhotoEvaluationEntity original = owned(account, request.originalEvaluationId);
        PhotoEvaluationEntity retake = owned(account, request.retakeEvaluationId);
        requireComparableSession(account, original, retake);
        Map<String, Object> result = compare(original, retake);
        createLinkIfAbsent(account, original, retake);
        return result;
    }
    private void validate(RetakeComparisonRequest request) {
        if (request == null || blank(request.originalEvaluationId) || blank(request.retakeEvaluationId)
                || request.originalEvaluationId.equals(request.retakeEvaluationId))
            throw new RetakeComparisonInvalid();
    }
    private void requireComparableSession(String account, PhotoEvaluationEntity original,
            PhotoEvaluationEntity retake) {
        if (blank(original.sessionId) || !Objects.equals(original.sessionId, retake.sessionId)
                || !manager.hasOwnedSession(account, original.sessionId))
            throw new RetakeComparisonInvalid();
    }
    private void createLinkIfAbsent(String account, PhotoEvaluationEntity original, PhotoEvaluationEntity retake) {
        manager.saveRetakeIfAbsent(account, original, retake);
    }
    public Map<String, Object> get(String token, String retakeId) {
        String account = auth.requireAccountId(token);
        RetakeLinkEntity link = manager.findOwnedRetake(account, retakeId);
        if (link == null)
            throw new RetakeComparisonNotFound();
        return compare(owned(account, link.originalEvaluationId), owned(account, link.retakeEvaluationId));
    }
    private Map<String, Object> compare(PhotoEvaluationEntity original, PhotoEvaluationEntity retake) {
        if (blank(original.resultJson) || blank(retake.resultJson))
            throw new RetakeComparisonInvalid();
        Map<String, Object> before = read(original.resultJson), after = read(retake.resultJson);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("original", snapshot(original, before));
        response.put("retake", snapshot(retake, after));
        response.put("scoreDelta", number(after.get("total")) - number(before.get("total")));
        response.put("dimensionChanges", dimensions(before, after));
        ShootingSessionEntity session = manager.findSession(original.sessionId);
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
        MediaEntity item = media.findEntityById(mediaId);
        return item == null || blank(item.exifJson) ? Map.of() : read(item.exifJson);
    }
    private PhotoEvaluationEntity owned(String account, String id) {
        PhotoEvaluationEntity item = manager.findOwnedEvaluation(account, id);
        if (item == null)
            throw new RetakeComparisonNotFound();
        return item;
    }
    private Map<String, Object> read(String source) {
        try {
            return json.readValue(source, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new RetakeComparisonInvalid();
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

}
