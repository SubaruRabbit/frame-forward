package com.frameforward.portfolio.service;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.model.dto.PortfolioEvaluationDetail;
import com.frameforward.evaluation.service.PortfolioEvaluationQuery;
import com.frameforward.media.model.dto.PortfolioMediaItem;
import com.frameforward.media.service.PortfolioMediaQuery;
import com.frameforward.portfolio.business.PortfolioBusiness;
import com.frameforward.portfolio.business.PortfolioNotFound;
import com.frameforward.portfolio.model.dto.*;
class PortfolioBoundaryTest {
    private final AuthService auth = mock(AuthService.class);
    private final PortfolioMediaQuery media = mock(PortfolioMediaQuery.class);
    private final PortfolioEvaluationQuery evaluations = mock(PortfolioEvaluationQuery.class);
    private final PortfolioBusiness business = mock(PortfolioBusiness.class);
    private final PortfolioService service = new PortfolioService(auth, media, evaluations, business);
    @Test
    void normalizesPageLimitsAndBlankFiltersWithoutChangingCursor() {
        when(auth.requireAccountId("token")).thenReturn("owner");
        var items = IntStream.range(0, 105).mapToObj(i -> new PortfolioMediaItem(String.format("%03d", 105-i), 1, 1, Map.<String,String>of())).toList();
        when(media.listOwned("owner")).thenReturn(items);
        when(evaluations.findOwned(eq("owner"), any())).thenReturn(new PortfolioEvaluationDetail(null, null, null, null));
        assertThat(service.list("token", null).items()).hasSize(20);
        assertThat(service.list("token", new Filter(null, null, null, " ", "", null)).items()).hasSize(20);
        assertThat(service.list("token", new Filter(null, 0, null, null, null, null)).items()).hasSize(1);
        assertThat(service.list("token", new Filter(null, 200, null, null, null, null)).items()).hasSize(100);
        var page = service.list("token", new Filter("003", 20, null, null, null, null));
        assertThat(page.items()).extracting(item -> item.get("mediaId")).containsExactly("002", "001");
        assertThat(page.nextCursor()).isNull();
    }
    @Test
    void missingOrDeletingDetailsRemainNotFound() {
        when(auth.requireAccountId("token")).thenReturn("owner");
        assertThatThrownBy(() -> service.detail("token", "missing")).isInstanceOf(PortfolioNotFound.class);
        when(business.deletionStarted("owner", "deleting")).thenReturn(true);
        assertThatThrownBy(() -> service.detail("token", "deleting")).isInstanceOf(PortfolioNotFound.class);
        verify(media, never()).findOwned("owner", "deleting");
    }
    @Test
    void mutationsAndStatusKeepAccountAndOwnershipContext() {
        when(auth.requireAccountId("token")).thenReturn("owner");
        when(media.findOwned("owner", "media")).thenReturn(new PortfolioMediaItem("media", 1, 1, Map.of()));
        var favorite = new Favorite("media", true);
        var job = new DeletionJob("job", "media", "COMPLETED", null);
        when(business.setFavorite("owner", "media", true, true)).thenReturn(favorite);
        when(business.delete("owner", "media", true)).thenReturn(job);
        when(business.deletionStatus("owner", "job")).thenReturn(job);
        assertThat(service.setFavorite("token", "media", true)).isSameAs(favorite);
        assertThat(service.delete("token", "media")).isSameAs(job);
        assertThat(service.deletionStatus("token", "job")).isSameAs(job);
        service.setFavorite("token", "missing", false);
        service.delete("token", "missing");
        verify(business).setFavorite("owner", "missing", false, false);
        verify(business).delete("owner", "missing", false);
    }
}
