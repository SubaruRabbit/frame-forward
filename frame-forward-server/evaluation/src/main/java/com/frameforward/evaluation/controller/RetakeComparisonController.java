package com.frameforward.evaluation.controller;

import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.frameforward.auth.service.AuthService;
import com.frameforward.evaluation.model.dto.RetakeComparisonRequest;
import com.frameforward.evaluation.service.RetakeComparisonService;

@RestController
@RequestMapping("/retake-comparisons")
@lombok.RequiredArgsConstructor
public class RetakeComparisonController {

	private final RetakeComparisonService comparisons;

	@PostMapping
	ResponseEntity<Map<String, Object>> create(
			@RequestHeader(name = "Authorization", required = false) String authorization,
			@RequestBody RetakeComparisonRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(comparisons.create(AuthService.bearer(authorization), request));
	}

	@GetMapping("/{retakeEvaluationId}")
	Map<String, Object> get(@RequestHeader(name = "Authorization", required = false) String authorization,
			@PathVariable String retakeEvaluationId) {
		return comparisons.get(AuthService.bearer(authorization), retakeEvaluationId);
	}

}
