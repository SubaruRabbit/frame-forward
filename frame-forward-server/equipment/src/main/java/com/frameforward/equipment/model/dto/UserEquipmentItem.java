package com.frameforward.equipment.model.dto;

public record UserEquipmentItem(
		String id,
		String kind,
		String catalogItemId,
		String nickname,
		boolean primary) {

}
