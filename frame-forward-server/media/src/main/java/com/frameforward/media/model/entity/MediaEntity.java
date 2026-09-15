package com.frameforward.media.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@TableName("media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaEntity {

	/** 媒体唯一标识。 */
	@TableId
	public String id;

	/** 所属账户唯一标识。 */
	public String ownerId;

	/** 媒体内容哈希值。 */
	public String contentHash;

	/** 媒体宽度，单位为像素。 */
	public int width;

	/** 媒体高度，单位为像素。 */
	public int height;

	/** 原始媒体文件路径。 */
	public String originalPath;

	/** 供 AI 处理的媒体副本路径。 */
	public String aiCopyPath;

	/** 媒体 EXIF 元数据。 */
	public String exifJson;

}
