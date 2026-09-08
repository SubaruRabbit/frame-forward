package com.frameforward.ai.gateway;

import java.util.Map;

import com.frameforward.ai.model.dto.AiTaskCompletionContext;

/** 在任务成功转换状态前持久化业务结果，仅传递消费者所需的任务身份。 */
public abstract class AiTaskCompletionProcessor {
    public abstract boolean supports(String operationType);
    public abstract void complete(AiTaskCompletionContext task, Map<String, Object> result);
}
