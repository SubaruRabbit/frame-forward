package com.frameforward.equipment.business;
import com.frameforward.equipment.model.dto.CompatibilityResult;

public final class CompatibilityRules {
    private CompatibilityRules() {
    }

    public static CompatibilityResult evaluate(String cameraMount, String cameraFormat, String lensMount,
            String lensFormat) {
        boolean sameMount = cameraMount.equals(lensMount);
        boolean cropRequired = sameMount && "FULL_FRAME".equals(cameraFormat) && "APS_C".equals(lensFormat);
        return new CompatibilityResult(sameMount, cropRequired,
                sameMount ? (cropRequired ? "APS_C_CROP" : "NATIVE") : "CROSS_MOUNT");
    }

}
