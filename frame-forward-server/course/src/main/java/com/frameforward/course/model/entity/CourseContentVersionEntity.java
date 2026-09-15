package com.frameforward.course.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("course_content_versions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseContentVersionEntity {

	@TableId
	public String id;

	public String courseId, contentVersion, modelId, promptVersion, sourceMaterialVersion;

	public Instant createdAt;

}
