package com.frameforward.equipment.model.dto;
import java.util.List;
public record CatalogResponse<T>(String version, List<T> items) {
}
