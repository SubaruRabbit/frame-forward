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

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 课程内容版本唯一标识。 */
	public String contentVersionId;

	/** 课时唯一标识。 */
	public String lessonId;

	/** 课时完成时间。 */
	public Instant completedAt;

}
