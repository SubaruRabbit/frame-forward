package com.frameforward.course.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("lesson_progress")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressEntity {

	public String accountId, contentVersionId, lessonId;

	public Instant completedAt;

}
