package com.frameforward.portfolio.model.dto;
import java.util.List;
import java.util.Map;
public record Page(List<Map<String, Object>> items, String nextCursor) {
}
