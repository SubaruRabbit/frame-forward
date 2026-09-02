package com.frameforward.equipment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CompatibilityRulesTest {
    @Test
    void evaluatesNativeApsCCropAndCrossMountCombinations() {
        var nativeResult = CompatibilityRules.evaluate("E", "APS_C", "E", "APS_C");
        assertThat(nativeResult.compatible()).isTrue();
        assertThat(nativeResult.cropModeRequired()).isFalse();
        assertThat(nativeResult.mode()).isEqualTo("NATIVE");

        var cropResult = CompatibilityRules.evaluate("E", "FULL_FRAME", "E", "APS_C");
        assertThat(cropResult.compatible()).isTrue();
        assertThat(cropResult.cropModeRequired()).isTrue();
        assertThat(cropResult.mode()).isEqualTo("APS_C_CROP");

        var crossMountResult = CompatibilityRules.evaluate("Z", "FULL_FRAME", "RF", "FULL_FRAME");
        assertThat(crossMountResult.compatible()).isFalse();
        assertThat(crossMountResult.cropModeRequired()).isFalse();
        assertThat(crossMountResult.mode()).isEqualTo("CROSS_MOUNT");
    }
}
