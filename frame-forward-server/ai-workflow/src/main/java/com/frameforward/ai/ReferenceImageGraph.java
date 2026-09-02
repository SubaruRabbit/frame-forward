package com.frameforward.ai;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ReferenceImageGraph {
    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(Map<String, Object> input) {
        Object supplied = input.get("mockOutput");
        return supplied instanceof Map<?, ?> map ? (Map<String, Object>) map : Map.of();
    }
    public boolean valid(Map<String, Object> output) {
        return output != null && output.get("imageUrl") instanceof String url && !url.isBlank()
                && positive(output.get("width")) && positive(output.get("height"));
    }
    private boolean positive(Object value) {
        return value instanceof Number number && number.intValue() > 0;
    }
}
