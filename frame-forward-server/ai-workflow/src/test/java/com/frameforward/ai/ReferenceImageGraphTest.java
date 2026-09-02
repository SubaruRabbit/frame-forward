package com.frameforward.ai;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ReferenceImageGraphTest {
    private final ReferenceImageGraph graph = new ReferenceImageGraph();

    @Test
    void acceptsOnlyACompleteImageModelResult() {
        assertTrue(graph.valid(Map.of("imageUrl", "generated/reference.jpg", "width", 1536, "height", 1024)));
        assertFalse(graph.valid(Map.of("imageUrl", "", "width", 1536, "height", 1024)));
        assertFalse(graph.valid(Map.of("imageUrl", "generated/reference.jpg", "width", 0, "height", 1024)));
    }
}
