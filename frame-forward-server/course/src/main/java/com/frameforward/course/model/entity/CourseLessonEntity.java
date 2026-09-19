package com.frameforward.course.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@TableName("course_lessons")
@Getter
@Setter
public class CourseLessonEntity {

	/** 课时标识。 */
	@TableId
	public String id;

	/** 所属章节标识。 */
	public String chapterId;

	/** 课时标题。 */
	public String title;

	/** 课时展示顺序。 */
	public int sequenceNumber;

	/** 学习目标。 */
	public String objective;

	/** 教学正文。 */
	public String content;

	/** 正确示例。 */
	public String correctExample;

	/** 错误示例。 */
	public String incorrectExample;

	/** 判断题 JSON 内容。 */
	public String exerciseJson;

	/** 课后作业。 */
	public String assignmentText;

}
