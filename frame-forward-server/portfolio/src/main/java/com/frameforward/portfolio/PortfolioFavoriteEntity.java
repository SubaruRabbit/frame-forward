package com.frameforward.portfolio;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;

@TableName("portfolio_favorites")
public class PortfolioFavoriteEntity {
  @TableId public String mediaId;
  public String accountId;
  public Instant createdAt;
  public String getMediaId() { return mediaId; }
  public String getAccountId() { return accountId; }
}
