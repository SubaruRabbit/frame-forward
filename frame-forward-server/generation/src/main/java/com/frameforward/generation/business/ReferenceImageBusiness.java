package com.frameforward.generation.business;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.frameforward.generation.manager.ReferenceImageManager;
import com.frameforward.generation.model.dto.NewReference;
import com.frameforward.generation.model.dto.ReferenceImageRequest;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;

@Component
public class ReferenceImageBusiness {
    private final ReferenceImageManager manager;

    public ReferenceImageBusiness(ReferenceImageManager manager) {
        this.manager = manager;
    }

    public void validate(ReferenceImageRequest request) {
        if (!hasRequiredContext(request) || !hasRequiredPlanText(request.selectedPlan)
                || !hasStructuredPlanFields(request.selectedPlan)) {
            throw new InvalidRequest();
        }
    }

    public ShootingPlanEntity findOwnedPlan(String accountId, String planId) {
        return manager.findOwnedPlan(accountId, planId);
    }

    public String controlledPrompt(Map<String, Object> selectedPlan) {
        return "摄影参考图；保留现场空间结构、光线方向、关键背景、主体位置、目标构图、镜头视角与画幅方向。机位：" + selectedPlan.get("position") + "；高度："
                + selectedPlan.get("cameraHeight") + "；构图：" + selectedPlan.get("composition") + "；方向："
                + selectedPlan.get("orientation") + "；焦段：" + selectedPlan.get("focalLengthMm") + "mm；光线起始："
                + selectedPlan.get("exposure") + "。仅表达构图与氛围，不宣称真实结果。";
    }

    public void persistReference(String accountId, String environmentMediaId, String shootingPlanId, String taskId,
            Map<String, Object> selectedPlan, String prompt) {
        manager.persistReferenceIfAbsent(new NewReference(accountId, environmentMediaId, shootingPlanId, taskId,
                String.valueOf(selectedPlan.get("label")), prompt));
    }

    private static boolean hasRequiredContext(ReferenceImageRequest request) {
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
}
