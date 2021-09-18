package com.sy.service;

import com.sy.entity.OSSFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IOSSFileService  {

	void upload(MultipartFile multipartFile) throws IOException;

//	boolean delete(OSSFile ossFile);

}
