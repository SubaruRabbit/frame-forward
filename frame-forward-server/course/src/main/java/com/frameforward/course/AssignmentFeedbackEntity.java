package com.frameforward.course;
import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
@TableName("lesson_assignment_feedback")
public class AssignmentFeedbackEntity {
    @TableId
    public String id;
    public String accountId, contentVersionId, lessonId, mediaId, feedbackTaskId, lessonObjective;
    public Instant createdAt;
}
