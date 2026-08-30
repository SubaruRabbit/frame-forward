package com.frameforward.course;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;
@TableName("course_content_versions") public class CourseContentVersionEntity {
 @TableId public String id; public String courseId,contentVersion,modelId,promptVersion,sourceMaterialVersion; public Instant createdAt;
 public String getId(){return id;} public String getCourseId(){return courseId;} public String getContentVersion(){return contentVersion;}
}
