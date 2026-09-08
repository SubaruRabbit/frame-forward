package com.frameforward.evaluation.service;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.business.*;
import com.frameforward.evaluation.manager.ShootingSessionManager;
import com.frameforward.evaluation.mapper.ShootingSessionMapper;
import com.frameforward.evaluation.model.dto.ShootingSessionRequest;
import com.frameforward.evaluation.repository.ShootingSessionRepository;
import com.frameforward.shooting.mapper.ShootingPlanMapper;
import com.frameforward.shooting.model.entity.ShootingPlanEntity;
class ShootingSessionServiceTest {
    @Test
    void validatesContextThenPersistsOnlyOwnedPlanSession() {
        var auth = mock(AuthService.class);
        var plans = mock(ShootingPlanMapper.class);
        var sessions = mock(ShootingSessionMapper.class);
        var service = new ShootingSessionService(auth,
                new ShootingSessionManager(new ShootingSessionRepository(plans, sessions)));
        assertThatThrownBy(() -> service.create("token", null)).isInstanceOf(ShootingSessionInvalid.class);
        for (var pair : new String[][]{{null, "context"}, {" ", "context"}, {"plan", null}, {"plan", " "}}) {
            assertThatThrownBy(() -> service.create("token", request(pair[0], pair[1])))
                    .isInstanceOf(ShootingSessionInvalid.class);
        }
        verifyNoInteractions(auth, plans, sessions);
        when(auth.requireAccountId("token")).thenReturn("owner");
        assertThatThrownBy(() -> service.create("token", request("plan", "context")))
                .isInstanceOf(ShootingSessionNotFound.class);
        verifyNoInteractions(sessions);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "plans"),
                ShootingPlanEntity.class);
        var plan = new ShootingPlanEntity();
        plan.id = "plan";
        when(plans.selectOne(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("id", "account_id");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("plan", "owner");
            return plan;
        });
        var result = service.create("token", request("plan", "context"));
        assertThat(result.id).isNotBlank();
        assertThat(result.accountId).isEqualTo("owner");
        assertThat(result.shootingPlanId).isEqualTo("plan");
        assertThat(result.planContext).isEqualTo("context");
        assertThat(result.createdAt).isNotNull();
        verify(sessions).insert(result);
    }
    private static ShootingSessionRequest request(String plan, String context) {
        var request = new ShootingSessionRequest();
        request.shootingPlanId = plan;
        request.planContext = context;
        return request;
    }
}
