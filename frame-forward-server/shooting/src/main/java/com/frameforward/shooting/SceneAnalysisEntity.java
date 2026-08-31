package com.frameforward.shooting;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;

@TableName("scene_analyses")
public class SceneAnalysisEntity {
  @TableId public String id;
  public String accountId;
  public String environmentMediaId;
  public String subjectType;
  public String subjectText;
  public String targetStyle;
  public int timeConstraintMinutes;
  public String equipmentSnapshotJson;
  public String aiTaskId;
  public Instant createdAt;
  public String getAiTaskId() { return aiTaskId; }
  public String getAccountId() { return accountId; }
  public String getId() { return id; }
}
