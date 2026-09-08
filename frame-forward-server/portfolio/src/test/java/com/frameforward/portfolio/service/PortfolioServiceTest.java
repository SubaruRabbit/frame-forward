package com.frameforward.portfolio.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.gateway.WorkEvaluationCleanup;
import com.frameforward.evaluation.model.dto.PortfolioEvaluationDetail;
import com.frameforward.evaluation.model.dto.PortfolioWorkflowContext;
import com.frameforward.evaluation.service.PortfolioEvaluationQuery;
import com.frameforward.media.gateway.WorkMediaCleanup;
import com.frameforward.media.model.dto.PortfolioMediaItem;
import com.frameforward.media.service.PortfolioMediaQuery;
import com.frameforward.portfolio.business.PortfolioBusiness;
import com.frameforward.portfolio.manager.PortfolioManager;
import com.frameforward.portfolio.mapper.PortfolioFavoriteMapper;
import com.frameforward.portfolio.mapper.PortfolioWorkDeletionJobMapper;
import com.frameforward.portfolio.model.dto.Filter;
import com.frameforward.portfolio.model.dto.Page;

class PortfolioServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final PortfolioMediaQuery media = mock(PortfolioMediaQuery.class);
    private final PortfolioEvaluationQuery evaluations = mock(PortfolioEvaluationQuery.class);
    private final PortfolioFavoriteMapper favorites = mock(PortfolioFavoriteMapper.class);
    private final PortfolioWorkDeletionJobMapper deletionJobs = mock(PortfolioWorkDeletionJobMapper.class);
    private final PortfolioManager manager = new PortfolioManager(
            new com.frameforward.portfolio.repository.PortfolioRepository(favorites, deletionJobs),
            mock(WorkEvaluationCleanup.class), mock(WorkMediaCleanup.class));
    private final PortfolioService service = new PortfolioService(auth, media, evaluations,
            new PortfolioBusiness(manager));

    @Test
    void listsOnlyMatchingNonFavoriteWorks() {
        arrangeItems(item("work-3", "Sony", "85mm"), item("work-2", "Sony", "50mm"), item("work-1", "Nikon", "85mm"));
        when(favorites.selectCount(any())).thenReturn(0L, 1L, 0L);

        Page page = service.list("token", new Filter(null, 20, null, "sony", null, false));

        assertEquals(List.of("work-3"), mediaIds(page));
        assertEquals(null, page.nextCursor());
    }

    @Test
    void excludesWorksWhoseDeletionHasStarted() {
        arrangeItems(item("work-2", "Sony", "50mm"), item("work-1", "Sony", "35mm"));
        when(deletionJobs.selectCount(any())).thenReturn(1L, 0L);
        when(favorites.selectCount(any())).thenReturn(0L);

        Page page = service.list("token", new Filter(null, 20, null, null, null, null));

        assertEquals(List.of("work-1"), mediaIds(page));
    }

    @Test
    void returnsNextCursorAfterLimitedPage() {
        arrangeItems(item("work-3", "Sony", "85mm"), item("work-2", "Sony", "50mm"), item("work-1", "Sony", "35mm"));
        when(favorites.selectCount(any())).thenReturn(0L, 0L, 0L);

        Page page = service.list("token", new Filter(null, 2, null, null, null, null));

        assertEquals(List.of("work-3", "work-2"), mediaIds(page));
        assertEquals("work-2", page.nextCursor());
    }

    @Test
    void detailExposesTypedWorkflowContext() {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwned("account-1", "work-1")).thenReturn(item("work-1", "Sony", "50mm"));
        when(favorites.selectCount(any())).thenReturn(0L);
        var context = new PortfolioWorkflowContext("work-1", Map.of("evaluationId", "eval-1"),
                null, null, List.of());
        when(evaluations.findOwned("account-1", "work-1"))
                .thenReturn(new PortfolioEvaluationDetail(Map.of(), null, null, context));

        assertEquals(context, service.detail("token", "work-1").get("workflowContext"));
    }

    private void arrangeItems(PortfolioMediaItem... items) {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.listOwned("account-1")).thenReturn(List.of(items));
        when(deletionJobs.selectCount(any())).thenReturn(0L);
        when(evaluations.findOwned(eq("account-1"), any()))
                .thenReturn(new PortfolioEvaluationDetail(null, null, null,
                        new PortfolioWorkflowContext(null, null, null, null, List.of())));
    }

    private static PortfolioMediaItem item(String mediaId, String camera, String lens) {
        return new PortfolioMediaItem(mediaId, 6000, 4000, Map.of("camera", camera, "lens", lens));
    }

    private static List<String> mediaIds(Page page) {
        return page.items().stream().map(item -> String.valueOf(item.get("mediaId"))).toList();
    }
}
