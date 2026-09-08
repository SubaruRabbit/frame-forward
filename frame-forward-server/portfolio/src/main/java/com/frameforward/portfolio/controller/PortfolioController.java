package com.frameforward.portfolio.controller;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frameforward.auth.service.AuthService;
import com.frameforward.portfolio.model.dto.DeletionJob;
import com.frameforward.portfolio.model.dto.Favorite;
import com.frameforward.portfolio.model.dto.FavoriteRequest;
import com.frameforward.portfolio.model.dto.Filter;
import com.frameforward.portfolio.model.dto.Page;
import com.frameforward.portfolio.service.PortfolioService;

@RestController
@RequestMapping("/portfolio/works")
public class PortfolioController {
    private final PortfolioService portfolio;
    public PortfolioController(PortfolioService portfolio) {
        this.portfolio = portfolio;
    }
    @GetMapping
    Page list(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String cursor, @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String subject, @RequestParam(required = false) String camera,
            @RequestParam(required = false) String lens, @RequestParam(required = false) Boolean favorite) {
        return portfolio.list(AuthService.bearer(authorization),
                new Filter(cursor, limit, subject, camera, lens, favorite));
    }
    @GetMapping("/{mediaId}")
    Map<String, Object> detail(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String mediaId) {
        return portfolio.detail(AuthService.bearer(authorization), mediaId);
    }
    @PutMapping("/{mediaId}")
    Favorite favorite(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String mediaId, @RequestBody FavoriteRequest request) {
        return portfolio.setFavorite(AuthService.bearer(authorization), mediaId, request.favorite);
    }
    @DeleteMapping("/{mediaId}")
    org.springframework.http.ResponseEntity<DeletionJob> delete(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String mediaId) {
        return org.springframework.http.ResponseEntity.accepted()
                .body(portfolio.delete(AuthService.bearer(authorization), mediaId));
    }

}
