package com.frameforward.portfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.frameforward.auth.AuthService;
import com.frameforward.evaluation.PortfolioEvaluationQuery;
import com.frameforward.evaluation.WorkEvaluationCleanup;
import com.frameforward.media.PortfolioMediaQuery;
import com.frameforward.media.WorkMediaCleanup;

class PortfolioServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final PortfolioMediaQuery media = mock(PortfolioMediaQuery.class);
    private final PortfolioEvaluationQuery evaluations = mock(PortfolioEvaluationQuery.class);
    private final PortfolioFavoriteMapper favorites = mock(PortfolioFavoriteMapper.class);
    private final PortfolioWorkDeletionJobMapper deletionJobs = mock(PortfolioWorkDeletionJobMapper.class);
    private final PortfolioManager manager = new PortfolioManager(favorites, deletionJobs,
            mock(WorkEvaluationCleanup.class), mock(WorkMediaCleanup.class));
    private final PortfolioService service = new PortfolioService(auth, media, evaluations,
            new PortfolioBusiness(manager));

    @Test
    void listsOnlyMatchingNonFavoriteWorks() {
        arrangeItems(item("work-3", "Sony", "85mm"), item("work-2", "Sony", "50mm"), item("work-1", "Nikon", "85mm"));
        when(favorites.selectCount(any())).thenReturn(0L, 1L, 0L);

        PortfolioService.Page page = service.list("token",
                new PortfolioService.Filter(null, 20, null, "sony", null, false));

        assertEquals(List.of("work-3"), mediaIds(page));
        assertEquals(null, page.nextCursor());
    }

    @Test
    void excludesWorksWhoseDeletionHasStarted() {
        arrangeItems(item("work-2", "Sony", "50mm"), item("work-1", "Sony", "35mm"));
        when(deletionJobs.selectCount(any())).thenReturn(1L, 0L);
        when(favorites.selectCount(any())).thenReturn(0L);

        PortfolioService.Page page = service.list("token",
                new PortfolioService.Filter(null, 20, null, null, null, null));

        assertEquals(List.of("work-1"), mediaIds(page));
    }

    @Test
    void returnsNextCursorAfterLimitedPage() {
        arrangeItems(item("work-3", "Sony", "85mm"), item("work-2", "Sony", "50mm"), item("work-1", "Sony", "35mm"));
        when(favorites.selectCount(any())).thenReturn(0L, 0L, 0L);

        PortfolioService.Page page = service.list("token",
                new PortfolioService.Filter(null, 2, null, null, null, null));

        assertEquals(List.of("work-3", "work-2"), mediaIds(page));
        assertEquals("work-2", page.nextCursor());
    }

    @Test
    void detailExposesTypedWorkflowContext() {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.findOwned("account-1", "work-1")).thenReturn(item("work-1", "Sony", "50mm"));
        when(favorites.selectCount(any())).thenReturn(0L);
        var context = new PortfolioEvaluationQuery.WorkflowContext("work-1", Map.of("evaluationId", "eval-1"),
                null, null, List.of());
        when(evaluations.findOwned("account-1", "work-1"))
                .thenReturn(new PortfolioEvaluationQuery.Detail(Map.of(), null, null, context));

        assertEquals(context, service.detail("token", "work-1").get("workflowContext"));
    }

    private void arrangeItems(PortfolioMediaQuery.Item... items) {
        when(auth.requireAccountId("token")).thenReturn("account-1");
        when(media.listOwned("account-1")).thenReturn(List.of(items));
        when(deletionJobs.selectCount(any())).thenReturn(0L);
        when(evaluations.findOwned(eq("account-1"), any()))
                .thenReturn(new PortfolioEvaluationQuery.Detail(null, null, null,
                        new PortfolioEvaluationQuery.WorkflowContext(null, null, null, null, List.of())));
    }

    private static PortfolioMediaQuery.Item item(String mediaId, String camera, String lens) {
        return new PortfolioMediaQuery.Item(mediaId, 6000, 4000, Map.of("camera", camera, "lens", lens));
    }

    private static List<String> mediaIds(PortfolioService.Page page) {
        return page.items().stream().map(item -> String.valueOf(item.get("mediaId"))).toList();
    }
}
