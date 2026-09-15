package com.frameforward.portfolio.model.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("portfolio_favorites")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioFavoriteEntity {

	/** 收藏的媒体唯一标识。 */
	@TableId
	public String mediaId;

	/** 所属账户唯一标识。 */
	public String accountId;

	/** 收藏创建时间。 */
	public Instant createdAt;

}
