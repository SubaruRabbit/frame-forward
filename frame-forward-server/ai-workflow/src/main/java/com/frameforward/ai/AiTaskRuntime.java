package com.frameforward.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;

/** 提供 AI 任务的服务入口、异步调度与 SSE 状态推送。 */
@Service
public class AiTaskRuntime {
    public enum State {
        QUEUED, RUNNING, SUCCEEDED, FAILED, CANCELLED
    }

    private final AiTaskBusiness business;
    private final Executor executor;
    private final Map<String, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public AiTaskRuntime(AiTaskBusiness business, @Qualifier("applicationTaskExecutor") Executor executor) {
        this.business = business;
        this.executor = executor;
    }

    @PostConstruct
    @Transactional
    public void recoverInterruptedTasks() {
        business.recoverInterruptedTasks();
    }

    public Created create(String accessToken, String key, CreateRequest request) {
        AiTaskBusiness.Creation creation = business.create(accessToken, key, request);
        if (creation.schedule())
            schedule(creation.created().taskId());
        return creation.created();
    }

    public Status get(String accessToken, String id) {
        return business.get(accessToken, id);
    }

    public SseEmitter events(String accessToken, String id) {
        Status status = get(accessToken, id);
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.computeIfAbsent(id, ignored -> new ArrayList<>()).add(emitter);
        emitter.onCompletion(() -> subscribers.getOrDefault(id, List.of()).remove(emitter));
        emit(emitter, status);
        return emitter;
    }

    void run(String id) {
        business.run(id, this::publish);
    }

    static boolean legal(State from, State to) {
        return AiTaskBusiness.legal(from, to);
    }

    private void schedule(String taskId) {
        if (TransactionSynchronizationManager.isActualTransactionActive())
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    executor.execute(() -> run(taskId));
                }
            });
        else
            executor.execute(() -> run(taskId));
    }

    private void publish(Status status) {
        for (SseEmitter emitter : subscribers.getOrDefault(status.taskId, List.of()))
            emit(emitter, status);
    }

    private void emit(SseEmitter emitter, Status status) {
        try {
            emitter.send(SseEmitter.event().name("progress").data(status, MediaType.APPLICATION_JSON));
        } catch (Exception exception) {
            emitter.complete();
        }
    }

    public static class CreateRequest {
        public String operationType;
        public Map<String, Object> input;
    }

    public record Created(String taskId, State state) {
    }

    public record Trace(String workflowVersion, String modelId, String promptVersion, String ruleVersion,
            String schemaVersion) {
    }

    public record Status(String taskId, State state, Map<String, Object> result, String errorCode, Trace trace) {
    }

    public static class BadRequest extends RuntimeException {
    }

    public static class NotFound extends RuntimeException {
    }
}
