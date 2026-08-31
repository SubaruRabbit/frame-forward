package com.frameforward.generation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frameforward.ai.AiTaskCompletionProcessor;
import com.frameforward.ai.AiTaskEntity;
import com.frameforward.media.MediaService;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReferenceImageCompletionProcessor extends AiTaskCompletionProcessor {
  private final ReferenceImageMapper references; private final MediaService media;
  public ReferenceImageCompletionProcessor(ReferenceImageMapper references, MediaService media) { this.references=references; this.media=media; }
  public boolean supports(String operationType) { return "reference-image-generation".equals(operationType); }
  @Transactional public void complete(AiTaskEntity task, Map<String,Object> result) {
    ReferenceImageEntity reference=references.selectOne(new LambdaQueryWrapper<ReferenceImageEntity>().eq(ReferenceImageEntity::getAiTaskId,task.id));
    if(reference==null || reference.generatedMediaId!=null) return;
    var generated=media.registerGenerated(task.accountId, String.valueOf(result.get("imageUrl")), ((Number)result.get("width")).intValue(), ((Number)result.get("height")).intValue());
    reference.generatedMediaId=generated.id(); references.updateById(reference); result.put("referenceImageId",reference.id); result.put("mediaId",generated.id());
  }
}
