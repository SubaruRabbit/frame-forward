package com.frameforward.course.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@TableName("course_definitions")
@Getter
@Setter
public class CourseDefinitionEntity {

	/** 课程标识。 */
	@TableId
	public String id;

	/** 课程展示标题。 */
	public String title;

	/** 课程分类。 */
	public String category;

	/** 课程创建时间。 */
	public Instant createdAt;

}
