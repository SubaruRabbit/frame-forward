package com.frameforward.course.service;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.frameforward.ai.model.dto.AiTaskCreateRequest;
import com.frameforward.ai.model.dto.AiTaskCreated;
import com.frameforward.ai.model.dto.AiTaskState;
import com.frameforward.ai.service.AiTaskRuntime;
import com.frameforward.auth.service.AuthService;
import com.frameforward.course.business.CourseBusiness;
import com.frameforward.course.component.CourseCatalog;
import com.frameforward.course.model.dto.LessonContext;
import com.frameforward.course.model.dto.ProgressSnapshot;
import com.frameforward.course.model.entity.CourseContentVersionEntity;
import com.frameforward.media.service.MediaService;

class CourseServiceTest {
    private final AuthService auth = mock(AuthService.class);
    private final MediaService media = mock(MediaService.class);
    private final AiTaskRuntime ai = mock(AiTaskRuntime.class);
    private final CourseBusiness business = mock(CourseBusiness.class);
    private final CourseService service = new CourseService(auth, media, ai, business);
    @Test
    void authenticatesCatalogCourseAndProgress() {
        var catalog = CourseCatalog.p0();
        when(auth.requireAccountId("token")).thenReturn("account");
        when(business.catalog()).thenReturn(catalog);
        when(business.course("course")).thenReturn(catalog.getFirst());
        when(business.progress("account", "course")).thenReturn(new ProgressSnapshot("course", "v1", 1, 4));
        assertThat(service.catalog("token")).isSameAs(catalog);
        assertThat(service.course("token", "course")).isSameAs(catalog.getFirst());
        assertThat(service.progress("token", "course").completedLessons()).isEqualTo(1);
        verify(auth, times(3)).requireAccountId("token");
    }
    @Test
    void submitsOwnedMediaWithOriginalAiInputBeforeRecordingProgress() {
        var version = new CourseContentVersionEntity();
        version.id = "version";
        version.contentVersion = "v1";
        var lesson = new LessonContext(version, "lesson", "objective");
        when(auth.requireAccountId("token")).thenReturn("account");
        when(business.lesson("course", "lesson")).thenReturn(lesson);
        when(ai.create(eq("token"), eq("key"), any())).thenReturn(new AiTaskCreated("task", AiTaskState.QUEUED));
        when(business.progress("account", "course")).thenReturn(new ProgressSnapshot("course", "v1", 1, 4));
        var result = service.submit("token", "course", "lesson", "media", "key");
        assertThat(result.feedbackTaskId()).isEqualTo("task");
        assertThat(result.lessonObjective()).isEqualTo("objective");
        var request = ArgumentCaptor.forClass(AiTaskCreateRequest.class);
        var order = inOrder(media, ai, business);
        order.verify(business).lesson("course", "lesson");
        order.verify(media).get("account", "media");
        order.verify(ai).create(eq("token"), eq("key"), request.capture());
        order.verify(business).recordSubmission("account", lesson, "media", "task");
        assertThat(request.getValue().operationType).isEqualTo("course-feedback");
        assertThat(request.getValue().input)
                .isEqualTo(Map.of("lessonObjective", "objective", "mediaId", "media", "contentVersion", "v1"));
    }
    @Test
    void rejectedAuthenticationDoesNotReachBusiness() {
        when(auth.requireAccountId("invalid")).thenThrow(new IllegalArgumentException("invalid"));
        assertThatThrownBy(() -> service.catalog("invalid")).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(business, media, ai);
    }
    @Test
    void regeneratesWithoutChangingVersionMetadata() {
        var item = new CourseContentVersionEntity();
        item.id = "id";
        item.courseId = "course";
        item.contentVersion = "v2";
        item.modelId = "model";
        item.promptVersion = "prompt";
        item.sourceMaterialVersion = "source";
        when(business.regenerate("course", "v2")).thenReturn(item);
        var result = service.regenerate("course", "v2");
        assertThat(result.id()).isEqualTo("id");
        assertThat(result.courseId()).isEqualTo("course");
        assertThat(result.contentVersion()).isEqualTo("v2");
        assertThat(result.modelId()).isEqualTo("model");
        assertThat(result.promptVersion()).isEqualTo("prompt");
        assertThat(result.sourceMaterialVersion()).isEqualTo("source");
    }
}
