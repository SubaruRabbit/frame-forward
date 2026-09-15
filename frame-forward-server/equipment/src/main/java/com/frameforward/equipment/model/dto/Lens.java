package com.frameforward.equipment.model.dto;

import java.math.BigDecimal;

public record Lens(
		String id,
		String brand,
		String model,
		String mount,
		short focalLengthMinMm,
		short focalLengthMaxMm,
		BigDecimal maximumAperture,
		String sensorFormat) {

}
