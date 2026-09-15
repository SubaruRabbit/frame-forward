package com.frameforward.equipment.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.frameforward.equipment.model.dto.AccessoryType;
import com.frameforward.equipment.model.dto.Camera;
import com.frameforward.equipment.model.dto.Lens;
import com.frameforward.equipment.model.dto.UserEquipmentItem;
import com.frameforward.equipment.model.entity.AccessoryTypeEntity;
import com.frameforward.equipment.model.entity.CameraEntity;
import com.frameforward.equipment.model.entity.LensEntity;
import com.frameforward.equipment.model.entity.UserEquipmentEntity;

@Mapper
public interface EquipmentConverter {

	Camera toCamera(CameraEntity source);

	Lens toLens(LensEntity source);

	AccessoryType toAccessoryType(AccessoryTypeEntity source);

	@Mapping(target = "primary", source = "isPrimary")
	UserEquipmentItem toUserEquipmentItem(UserEquipmentEntity source);

}
