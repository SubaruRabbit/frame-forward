package com.frameforward.ai;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** 场景图只接受结构化模型输出，并在返回前移除不可用机位。 */
@Component
public class SceneAnalysisGraph {
  @SuppressWarnings("unchecked")
  public Map<String, Object> execute(Map<String, Object> input) {
    Object supplied = input.get("mockOutput");
    if (!(supplied instanceof Map<?, ?> raw)) return Map.of();
    Map<String, Object> result = new LinkedHashMap<>((Map<String, Object>) raw);
    List<Object> safePositions = new ArrayList<>();
    List<String> warnings = new ArrayList<>();
    Object candidates = result.get("usablePositions");
    if (candidates instanceof List<?> positions) {
      for (Object candidate : positions) {
        if (!(candidate instanceof Map<?, ?> position)) continue;
        Object zone = position.get("zone");
        if ("ROADWAY".equals(zone) || "EDGE".equals(zone)) {
          warnings.add("ROADWAY".equals(zone) ? "检测到车行道，禁止推荐在车道内取景。" : "检测到边缘危险，禁止推荐靠近边缘取景。");
        } else safePositions.add(position);
      }
    }
    result.put("usablePositions", safePositions);
    result.put("safetyWarnings", warnings);
    return result;
  }

  public boolean valid(Map<String, Object> result) {
    return result != null
        && result.get("sceneType") instanceof String sceneType && !sceneType.isBlank()
        && result.get("subjectCandidates") instanceof List<?>
        && result.get("light") instanceof Map<?, ?>
        && result.get("backgroundComplexity") instanceof String
        && result.get("compositionalStructures") instanceof List<?>
        && result.get("usablePositions") instanceof List<?>
        && result.get("accessoryOpportunities") instanceof List<?>
        && result.get("safetyWarnings") instanceof List<?>;
  }
}
