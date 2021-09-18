package com.sy.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OSSFile {

	private static final long serialVersionUID = 1L;

	private String fileName;

	private String url;

}
