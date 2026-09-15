package com.frameforward.course.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("lesson_assignment_feedback")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentFeedbackEntity {

	@TableId
	public String id;

	public String accountId, contentVersionId, lessonId, mediaId, feedbackTaskId, lessonObjective;

	public Instant createdAt;

}
