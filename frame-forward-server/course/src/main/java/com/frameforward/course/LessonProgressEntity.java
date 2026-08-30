package com.frameforward.course;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;
@TableName("lesson_progress") public class LessonProgressEntity { public String accountId,contentVersionId,lessonId; public Instant completedAt; public String getAccountId(){return accountId;} public String getContentVersionId(){return contentVersionId;} public String getLessonId(){return lessonId;} }
