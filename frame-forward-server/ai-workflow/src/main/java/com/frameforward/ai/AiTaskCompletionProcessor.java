package com.frameforward.ai;

import java.util.Map;

/** 在任务成功转换状态前，持久化该任务的业务结果。 */
public abstract class AiTaskCompletionProcessor {
  public abstract boolean supports(String operationType);
  public abstract void complete(AiTaskEntity task, Map<String, Object> result);
}
