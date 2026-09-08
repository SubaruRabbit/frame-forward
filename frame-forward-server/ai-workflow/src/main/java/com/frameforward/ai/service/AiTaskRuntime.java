package com.frameforward.ai.service;
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

import com.frameforward.ai.business.AiTaskBusiness;
import com.frameforward.ai.business.AiTaskNotFound;
import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskCreation;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.model.dto.AiTaskStatus;
import com.frameforward.ai.model.entity.AiTaskEntity;

import jakarta.annotation.PostConstruct;

/** 提供 AI 任务的服务入口、异步调度与 SSE 状态推送。 */
@Service
public class AiTaskRuntime {

    private final AiTaskBusiness business;
    private final com.frameforward.auth.service.AuthService auth;
    private final Executor executor;
    private final Map<String, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public AiTaskRuntime(AiTaskBusiness business, com.frameforward.auth.service.AuthService auth,
            @Qualifier("applicationTaskExecutor") Executor executor) {
        this.business = business;
        this.auth = auth;
        this.executor = executor;
    }

    @PostConstruct
    @Transactional
    public void recoverInterruptedTasks() {
        business.recoverInterruptedTasks();
    }

    public AiTaskCreated create(String accessToken, String key, AiTaskCreateRequest request) {
        AiTaskBusiness.validateCreateRequest(key, request);
        AiTaskCreation creation = business.create(auth.requireAccountId(accessToken), key, request);
        if (creation.schedule())
            schedule(creation.created().taskId());
        return creation.created();
    }

    public AiTaskStatus get(String accessToken, String id) {
        AiTaskEntity task = business.findTask(id);
        if (task == null)
            throw new AiTaskNotFound();
        return business.statusForOwner(task, auth.requireAccountId(accessToken));
    }

    public SseEmitter events(String accessToken, String id) {
        AiTaskStatus status = get(accessToken, id);
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.computeIfAbsent(id, ignored -> new ArrayList<>()).add(emitter);
        emitter.onCompletion(() -> subscribers.getOrDefault(id, List.of()).remove(emitter));
        emit(emitter, status);
        return emitter;
    }

    void run(String id) {
        business.run(id, this::publish);
    }

    static boolean legal(AiTaskState from, AiTaskState to) {
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

    private void publish(AiTaskStatus status) {
        for (SseEmitter emitter : subscribers.getOrDefault(status.taskId(), List.of()))
            emit(emitter, status);
    }

    private void emit(SseEmitter emitter, AiTaskStatus status) {
        try {
            emitter.send(SseEmitter.event().name("progress").data(status, MediaType.APPLICATION_JSON));
        } catch (Exception exception) {
            emitter.complete();
        }
    }

}
