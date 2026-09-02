package com.frameforward.portfolio;

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

import com.frameforward.auth.AuthController;

@RestController
@RequestMapping("/portfolio/works")
public class PortfolioController {
    private final PortfolioService portfolio;
    public PortfolioController(PortfolioService portfolio) {
        this.portfolio = portfolio;
    }
    @GetMapping
    PortfolioService.Page list(@RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String cursor, @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String subject, @RequestParam(required = false) String camera,
            @RequestParam(required = false) String lens, @RequestParam(required = false) Boolean favorite) {
        return portfolio.list(AuthController.bearer(authorization),
                new PortfolioService.Filter(cursor, limit, subject, camera, lens, favorite));
    }
    @GetMapping("/{mediaId}")
    Map<String, Object> detail(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String mediaId) {
        return portfolio.detail(AuthController.bearer(authorization), mediaId);
    }
    @PutMapping("/{mediaId}")
    PortfolioService.Favorite favorite(@RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String mediaId, @RequestBody FavoriteRequest request) {
        return portfolio.setFavorite(AuthController.bearer(authorization), mediaId, request.favorite);
    }
    @DeleteMapping("/{mediaId}")
    org.springframework.http.ResponseEntity<PortfolioService.DeletionJob> delete(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @PathVariable String mediaId) {
        return org.springframework.http.ResponseEntity.accepted()
                .body(portfolio.delete(AuthController.bearer(authorization), mediaId));
    }
    public static class FavoriteRequest {
        public boolean favorite;
    }
}
