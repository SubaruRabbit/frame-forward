package com.frameforward.equipment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import com.frameforward.equipment.business.*;
import com.frameforward.equipment.manager.EquipmentManager;
import com.frameforward.equipment.mapper.*;
import com.frameforward.equipment.model.entity.*;
import com.frameforward.equipment.repository.EquipmentRepository;

class EquipmentServiceBoundaryTest {
    @org.junit.jupiter.api.BeforeAll
    static void initializeMapping() {
        com.baomidou.mybatisplus.core.metadata.TableInfoHelper.initTableInfo(
                new org.apache.ibatis.builder.MapperBuilderAssistant(
                        new com.baomidou.mybatisplus.core.MybatisConfiguration(), "equipment-test"),
                UserEquipmentEntity.class);
    }
    private final UserEquipmentMapper owned = mock(UserEquipmentMapper.class);
    private final CameraMapper cameras = mock(CameraMapper.class);
    private final LensMapper lenses = mock(LensMapper.class);
    private final AccessoryTypeMapper accessories = mock(AccessoryTypeMapper.class);
    private final EquipmentManager manager = new EquipmentManager(
            new EquipmentRepository(owned, cameras, lenses, accessories));
    private final UserEquipmentService service = new UserEquipmentService(new UserEquipmentBusiness(manager));
    private final CatalogService catalog = new CatalogService(manager);

    @Test
    void catalogMapsEntitiesAndKeepsBrandFiltering() {
        var camera = camera();
        var lens = lens();
        var accessory = new AccessoryTypeEntity();
        accessory.id = "tripod";
        accessory.displayName = "三脚架";
        when(cameras.selectList(any())).thenReturn(List.of(camera));
        when(lenses.selectList(any())).thenReturn(List.of(lens));
        when(accessories.selectList(any())).thenReturn(List.of(accessory));
        assertThat(catalog.cameras().getFirst().id()).isEqualTo("camera");
        for (String brand : new String[]{null, "", "  ", " Sony "}) {
            assertThat(catalog.lenses(brand).getFirst().id()).isEqualTo("lens");
        }
        assertThat(catalog.accessories().getFirst().displayName()).isEqualTo("三脚架");
    }

    @Test
    void catalogCompatibilityRequiresBothEntries() {
        assertThatThrownBy(() -> catalog.compatibility("camera", "lens")).isInstanceOf(CatalogNotFoundException.class);
        when(cameras.selectById("camera")).thenReturn(camera());
        assertThatThrownBy(() -> catalog.compatibility("camera", "lens")).isInstanceOf(CatalogNotFoundException.class);
        when(lenses.selectById("lens")).thenReturn(lens());
        assertThat(catalog.compatibility("camera", "lens").mode()).isEqualTo("APS_C_CROP");
    }

    @Test
    void onlyFirstCameraBecomesPrimaryAndOtherKindsRemainNonPrimary() {
        when(cameras.selectById("camera")).thenReturn(camera());
        when(owned.selectCount(any())).thenReturn(0L, 1L);
        assertThat(service.add("account", "CAMERA", "camera", "主机").primary()).isTrue();
        assertThat(service.add("account", "CAMERA", "camera", null).primary()).isFalse();
        when(lenses.selectById("lens")).thenReturn(lens());
        when(accessories.selectById("tripod")).thenReturn(new AccessoryTypeEntity());
        assertThat(service.add("account", "LENS", "lens", null).primary()).isFalse();
        assertThat(service.add("account", "ACCESSORY", "tripod", null).primary()).isFalse();
        verify(owned, times(4)).insert(any(UserEquipmentEntity.class));
    }

    @Test
    void invalidMissingAndDuplicateItemsKeepBusinessErrors() {
        for (String kind : new String[]{null, "", "camera", "UNKNOWN"}) {
            assertThatThrownBy(() -> service.add("account", kind, "missing", null))
                    .isInstanceOf(InvalidEquipmentException.class);
        }
        for (String kind : List.of("CAMERA", "LENS", "ACCESSORY")) {
            assertThatThrownBy(() -> service.add("account", kind, "missing", null))
                    .isInstanceOf(CatalogNotFoundException.class);
        }
        when(cameras.selectById("camera")).thenReturn(camera());
        when(owned.insert(any(UserEquipmentEntity.class))).thenThrow(new DuplicateKeyException("duplicate"));
        assertThatThrownBy(() -> service.add("account", "CAMERA", "camera", null))
                .isInstanceOf(DuplicateEquipmentException.class);
    }

    @Test
    void primarySwitchRequiresOwnedCameraAndClearsBeforeSetting() {
        assertThatThrownBy(() -> service.setPrimaryCamera("account", "unknown"))
                .isInstanceOf(EquipmentNotFoundException.class);
        when(owned.selectOne(any())).thenReturn(item("LENS", "lens"));
        assertThatThrownBy(() -> service.setPrimaryCamera("account", "lens")).isInstanceOf(NotCameraException.class);
        when(owned.selectOne(any())).thenReturn(item("CAMERA", "camera"));
        assertThat(service.setPrimaryCamera("account", "camera").primary()).isTrue();
        var updates = org.mockito.ArgumentCaptor
                .forClass(com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper.class);
        verify(owned, times(2)).update(updates.capture());
        var clear = updates.getAllValues().get(0);
        var select = updates.getAllValues().get(1);
        assertThat(clear.getSqlSegment()).contains("account_id", "kind");
        assertThat(clear.getParamNameValuePairs()).containsValue("account").containsValue("CAMERA")
                .containsValue(false);
        assertThat(select.getSqlSegment()).contains("account_id", "id");
        assertThat(select.getParamNameValuePairs()).containsValue("account").containsValue("camera")
                .containsValue(true);
    }

    @Test
    void removalDistinguishesMissingAndOwnedItems() {
        when(owned.delete(any(com.baomidou.mybatisplus.core.conditions.Wrapper.class))).thenReturn(0, 1);
        assertThatThrownBy(() -> service.remove("account", "id")).isInstanceOf(EquipmentNotFoundException.class);
        service.remove("account", "id");
    }

    @Test
    void listsAreSortedAndCombinationsOnlyUseCameraAndLens() {
        when(owned.selectList(any())).thenReturn(List.of(item("LENS", "lens"), item("CAMERA", "camera"), item("ACCESSORY", "tripod")));
        when(cameras.selectById("camera")).thenReturn(camera());
        when(lenses.selectById("lens")).thenReturn(lens());
        assertThat(service.list("account")).extracting("kind").containsExactly("ACCESSORY", "CAMERA", "LENS");
        var combinations = service.combinations("account");
        assertThat(combinations).hasSize(1);
        assertThat(combinations.getFirst().cameraEquipmentId()).isEqualTo("owned-camera");
        assertThat(combinations.getFirst().lensEquipmentId()).isEqualTo("owned-lens");
        assertThat(combinations.getFirst().cropModeRequired()).isTrue();
        assertThat(combinations.getFirst().defaultEligible()).isTrue();
    }

    private static UserEquipmentEntity item(String kind, String catalogId) {
        return new UserEquipmentEntity("owned-" + catalogId, "account", kind, catalogId, null, false);
    }

    private static CameraEntity camera() {
        var camera = new CameraEntity();
        camera.id = "camera";
        camera.mount = "E";
        camera.sensorFormat = "FULL_FRAME";
        return camera;
    }

    private static LensEntity lens() {
        var lens = new LensEntity();
        lens.id = "lens";
        lens.mount = "E";
        lens.sensorFormat = "APS_C";
        return lens;
    }
}
