package com.frameforward.portfolio;

import com.frameforward.auth.AuthController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio/work-deletions")
public class PortfolioWorkDeletionController {
  private final PortfolioService portfolio;

  public PortfolioWorkDeletionController(PortfolioService portfolio) { this.portfolio = portfolio; }

  @GetMapping("/{jobId}")
  PortfolioService.DeletionJob status(@RequestHeader(name = "Authorization", required = false) String authorization, @PathVariable String jobId) {
    return portfolio.deletionStatus(AuthController.bearer(authorization), jobId);
  }
}
