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

	/** 课程内容版本唯一标识。 */
	@TableId
	public String id;

	/** 课程唯一标识。 */
	public String courseId;

	/** 课程内容版本号。 */
	public String contentVersion;

	/** 生成课程内容使用的模型标识。 */
	public String modelId;

	/** 生成课程内容使用的提示词版本。 */
	public String promptVersion;

	/** 课程源材料版本。 */
	public String sourceMaterialVersion;

	/** 课程内容版本创建时间。 */
	public Instant createdAt;

}
