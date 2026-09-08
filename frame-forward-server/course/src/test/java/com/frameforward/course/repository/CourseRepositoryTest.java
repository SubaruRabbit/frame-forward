package com.frameforward.course.repository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.frameforward.course.mapper.*;
import com.frameforward.course.model.entity.*;
class CourseRepositoryTest {
    @Test
    void queriesKeepCourseVersionAndAccountLessonFilters() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "versions"),
                CourseContentVersionEntity.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "progress"),
                LessonProgressEntity.class);
        var versions = mock(CourseContentVersionMapper.class);
        var progress = mock(LessonProgressMapper.class);
        var repository = new CourseRepository(versions, progress, mock(AssignmentFeedbackMapper.class));
        when(versions.selectOne(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("course_id", "content_version");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("course", "v1");
            return null;
        });
        when(progress.selectCount(any())).thenAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("account_id", "content_version_id");
            assertThat(query.getParamNameValuePairs().values()).contains("account", "version");
            return 1L;
        });
        assertThat(repository.findVersion("course", "v1")).isNull();
        assertThat(repository.countCompletedLessons("account", "version")).isEqualTo(1);
        doAnswer(call -> {
            LambdaQueryWrapper<?> query = call.getArgument(0);
            assertThat(query.getSqlSegment()).contains("account_id", "content_version_id", "lesson_id");
            assertThat(query.getParamNameValuePairs().values()).containsExactlyInAnyOrder("account", "version",
                    "lesson");
            return 0L;
        }).when(progress).selectCount(any());
        assertThat(repository.countLessonProgress("account", "version", "lesson")).isZero();
    }
}
