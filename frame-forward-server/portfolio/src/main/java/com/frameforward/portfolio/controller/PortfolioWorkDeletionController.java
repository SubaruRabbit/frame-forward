package com.frameforward.portfolio.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.auth.service.AuthService;
import com.frameforward.portfolio.model.dto.DeletionJob;
import com.frameforward.portfolio.service.PortfolioService;

@RestController
@RequestMapping("/portfolio/work-deletions")
public class PortfolioWorkDeletionController {
    private final PortfolioService portfolio;

    public PortfolioWorkDeletionController(PortfolioService portfolio) {
        this.portfolio = portfolio;
    }

    @GetMapping("/{jobId}")
    DeletionJob status(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String jobId) {
        return portfolio.deletionStatus(AuthService.bearer(authorization), jobId);
    }
}
