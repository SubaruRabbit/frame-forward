package com.frameforward.equipment.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.frameforward.equipment.model.dto.AccessoryType;
import com.frameforward.equipment.model.dto.Camera;
import com.frameforward.equipment.model.dto.Lens;
import com.frameforward.equipment.model.dto.UserEquipmentItem;
import com.frameforward.equipment.model.entity.AccessoryTypeEntity;
import com.frameforward.equipment.model.entity.CameraEntity;
import com.frameforward.equipment.model.entity.LensEntity;
import com.frameforward.equipment.model.entity.UserEquipmentEntity;

class EquipmentConverterTest {

	private final EquipmentConverter converter = Mappers.getMapper(EquipmentConverter.class);

	@Test
	void convertsCatalogEntitiesFieldByField() {
		var camera = CameraEntity.builder().id("camera").brand("Sony").model("A1").mount("E").sensorFormat("FULL_FRAME")
				.build();
		var lens = LensEntity.builder().id("lens").brand("Sony").model("FE 50").mount("E").focalLengthMinMm((short) 50)
				.focalLengthMaxMm((short) 50).maximumAperture(new BigDecimal("1.2")).sensorFormat("FULL_FRAME").build();
		var accessory = AccessoryTypeEntity.builder().id("tripod").displayName("Tripod").build();

		assertThat(converter.toCamera(camera)).usingRecursiveComparison()
				.isEqualTo(new Camera("camera", "Sony", "A1", "E", "FULL_FRAME"));
		assertThat(converter.toLens(lens)).usingRecursiveComparison().isEqualTo(
				new Lens("lens", "Sony", "FE 50", "E", (short) 50, (short) 50, new BigDecimal("1.2"), "FULL_FRAME"));
		assertThat(converter.toAccessoryType(accessory)).usingRecursiveComparison()
				.isEqualTo(new AccessoryType("tripod", "Tripod"));
	}

	@Test
	void keepsMybatisBooleanAccessorAndMapsOwnedEquipment() {
		var entity = UserEquipmentEntity.builder().id("owned").accountId("account").kind("CAMERA")
				.catalogItemId("camera").nickname("Main").isPrimary(true).build();

		assertThat(entity.getIsPrimary()).isTrue();
		assertThat(converter.toUserEquipmentItem(entity)).usingRecursiveComparison()
				.isEqualTo(new UserEquipmentItem("owned", "CAMERA", "camera", "Main", true));
	}

}
