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

	@TableId
	public String id;

	public String ownerId;

	public String contentHash;

	public int width;

	public int height;

	public String originalPath;

	public String aiCopyPath;

	public String exifJson;

}
