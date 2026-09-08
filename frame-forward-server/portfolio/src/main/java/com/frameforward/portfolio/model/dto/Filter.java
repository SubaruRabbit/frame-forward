package com.frameforward.portfolio.model.dto;
public record Filter(String cursor, Integer limit, String subject, String camera, String lens, Boolean favorite) {
}
