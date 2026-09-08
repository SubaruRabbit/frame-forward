package com.frameforward.course.model.entity;
import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableName;
@TableName("lesson_progress")
public class LessonProgressEntity {
    public String accountId, contentVersionId, lessonId;
    public Instant completedAt;
    public String getAccountId() {
        return accountId;
    }
    public String getContentVersionId() {
        return contentVersionId;
    }
    public String getLessonId() {
        return lessonId;
    }
}
