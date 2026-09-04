package com.frameforward.generation;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.ai.AiTaskRuntime;
import com.frameforward.auth.AuthService;
import com.frameforward.media.MediaEntity;
import com.frameforward.media.MediaManager;
import com.frameforward.shooting.ShootingPlanEntity;
import com.frameforward.shooting.ShootingPlanMapper;

@Service
public class ReferenceImageService {
    private final AuthService auth;
    private final MediaManager media;
    private final ShootingPlanMapper plans;
    private final ReferenceImageMapper references;
    private final AiTaskRuntime tasks;
    public ReferenceImageService(AuthService auth, MediaManager media, ShootingPlanMapper plans,
            ReferenceImageMapper references, AiTaskRuntime tasks) {
        this.auth = auth;
        this.media = media;
        this.plans = plans;
        this.references = references;
        this.tasks = tasks;
    }
    @Transactional
    public AiTaskRuntime.Created create(String token, String key, Request request) {
        validate(request);
        String account = auth.requireAccountId(token);
        MediaEntity scene = media.findOwned(account, request.environmentMediaId);
        ShootingPlanEntity plan = plans.selectOne(new LambdaQueryWrapper<ShootingPlanEntity>()
                .eq(ShootingPlanEntity::getId, request.shootingPlanId).eq(ShootingPlanEntity::getAccountId, account));
        if (scene == null || plan == null)
            throw new OwnershipMissing();
        String prompt = controlledPrompt(request.selectedPlan);
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("environmentMediaId", scene.id);
        input.put("shootingPlanId", plan.id);
        input.put("selectedPlan", request.selectedPlan);
        input.put("prompt", prompt);
        if (request.mockOutput != null)
            input.put("mockOutput", request.mockOutput);
        AiTaskRuntime.CreateRequest aiRequest = new AiTaskRuntime.CreateRequest();
        aiRequest.operationType = "reference-image-generation";
        aiRequest.input = input;
        AiTaskRuntime.Created task = tasks.create(token, key, aiRequest);
        if (references.selectOne(new LambdaQueryWrapper<ReferenceImageEntity>().eq(ReferenceImageEntity::getAiTaskId,
                task.taskId())) == null) {
            ReferenceImageEntity entity = new ReferenceImageEntity();
            entity.id = UUID.randomUUID().toString();
            entity.accountId = account;
            entity.environmentMediaId = scene.id;
            entity.shootingPlanId = plan.id;
            entity.aiTaskId = task.taskId();
            entity.selectedPlanLabel = String.valueOf(request.selectedPlan.get("label"));
            entity.promptText = prompt;
            entity.createdAt = Instant.now();
            references.insert(entity);
        }
        return task;
    }
    static String controlledPrompt(Map<String, Object> selectedPlan) {
        return "摄影参考图；保留现场空间结构、光线方向、关键背景、主体位置、目标构图、镜头视角与画幅方向。机位：" + selectedPlan.get("position") + "；高度："
                + selectedPlan.get("cameraHeight") + "；构图：" + selectedPlan.get("composition") + "；方向："
                + selectedPlan.get("orientation") + "；焦段：" + selectedPlan.get("focalLengthMm") + "mm；光线起始："
                + selectedPlan.get("exposure") + "。仅表达构图与氛围，不宣称真实结果。";
    }
    private static void validate(Request request) {
        if (!hasRequiredContext(request) || !hasRequiredPlanText(request.selectedPlan)
                || !hasStructuredPlanFields(request.selectedPlan))
            throw new InvalidRequest();
    }
    private static boolean hasRequiredContext(Request request) {
        return request != null && !blank(request.environmentMediaId) && !blank(request.shootingPlanId);
    }
    private static boolean hasRequiredPlanText(Map<String, Object> selectedPlan) {
        return selectedPlan != null && !blank(selectedPlan.get("label")) && !blank(selectedPlan.get("position"))
                && !blank(selectedPlan.get("cameraHeight")) && !blank(selectedPlan.get("composition"))
                && !blank(selectedPlan.get("orientation"));
    }
    private static boolean hasStructuredPlanFields(Map<String, Object> selectedPlan) {
        return selectedPlan != null && selectedPlan.get("focalLengthMm") instanceof Number
                && selectedPlan.get("exposure") instanceof Map<?, ?>;
    }
    private static boolean blank(Object value) {
        return !(value instanceof String text) || text.isBlank();
    }
    public static class Request {
        public String environmentMediaId, shootingPlanId;
        public Map<String, Object> selectedPlan, mockOutput;
    }
    public static class InvalidRequest extends RuntimeException {
    }
    public static class OwnershipMissing extends RuntimeException {
    }
}
