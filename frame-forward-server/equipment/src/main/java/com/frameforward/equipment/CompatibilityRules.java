package com.frameforward.equipment;

public final class CompatibilityRules {
    private CompatibilityRules() {}

    public static Result evaluate(String cameraMount, String cameraFormat, String lensMount, String lensFormat) {
        boolean sameMount = cameraMount.equals(lensMount);
        boolean cropRequired = sameMount && "FULL_FRAME".equals(cameraFormat) && "APS_C".equals(lensFormat);
        return new Result(sameMount, cropRequired, sameMount ? (cropRequired ? "APS_C_CROP" : "NATIVE") : "CROSS_MOUNT");
    }

    public record Result(boolean compatible, boolean cropModeRequired, String mode) {}
}
