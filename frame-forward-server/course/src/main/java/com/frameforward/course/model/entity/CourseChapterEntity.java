package com.frameforward.course.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

@TableName("course_chapters")
@Getter
@Setter
public class CourseChapterEntity {

	/** 章节标识。 */
	@TableId
	public String id;

	/** 所属课程内容版本标识。 */
	public String contentVersionId;

	/** 章节标题。 */
	public String title;

	/** 章节展示顺序。 */
	public int sequenceNumber;

}
