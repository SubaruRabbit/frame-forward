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

	/** 作业反馈唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 课程内容版本唯一标识。 */
	public String contentVersionId;

	/** 课时唯一标识。 */
	public String lessonId;

	/** 作业媒体唯一标识。 */
	public String mediaId;

	/** 反馈 AI 任务唯一标识。 */
	public String feedbackTaskId;

	/** 课时训练目标。 */
	public String lessonObjective;

	/** 作业反馈创建时间。 */
	public Instant createdAt;

}
