package com.frameforward.equipment.service;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frameforward.equipment.business.UserEquipmentBusiness;
import com.frameforward.equipment.model.dto.BodyLensCombination;
import com.frameforward.equipment.model.dto.UserEquipmentItem;

/** 用户器材用例入口。 */
@Service
public class UserEquipmentService {
    private final UserEquipmentBusiness business;
    public UserEquipmentService(UserEquipmentBusiness business) {
        this.business = business;
    }
    public List<UserEquipmentItem> list(String accountId) {
        return business.list(accountId);
    }
    @Transactional
    public UserEquipmentItem add(String accountId, String kind, String catalogItemId, String nickname) {
        return business.add(accountId, kind, catalogItemId, nickname);
    }
    public void remove(String accountId, String id) {
        business.remove(accountId, id);
    }
    @Transactional
    public UserEquipmentItem setPrimaryCamera(String accountId, String id) {
        return business.setPrimary(accountId, id);
    }
    public List<BodyLensCombination> combinations(String accountId) {
        return business.combinations(accountId);
    }

}
