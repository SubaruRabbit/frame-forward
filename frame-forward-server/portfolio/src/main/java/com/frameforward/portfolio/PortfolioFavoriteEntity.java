package com.frameforward.portfolio;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("portfolio_favorites")
public class PortfolioFavoriteEntity {
    @TableId
    public String mediaId;
    public String accountId;
    public Instant createdAt;
    public String getMediaId() {
        return mediaId;
    }
    public String getAccountId() {
        return accountId;
    }
}
