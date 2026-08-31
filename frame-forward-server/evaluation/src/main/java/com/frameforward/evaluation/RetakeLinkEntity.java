package com.frameforward.evaluation;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;

@TableName("retake_links")
public class RetakeLinkEntity {
  @TableId public String retakeEvaluationId;
  public String originalEvaluationId, accountId, sessionId;
  public Instant createdAt;
  public String getRetakeEvaluationId() { return retakeEvaluationId; }
  public String getOriginalEvaluationId() { return originalEvaluationId; }
  public String getAccountId() { return accountId; }
}
