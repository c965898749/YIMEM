package com.sy.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sy.tool.Constants;
import com.sy.tool.SmbUtil;
import com.sy.tool.oConvertUtils;
import com.sy.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {





//	/**
//	 * 文件上传统一方法
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@PostMapping(value = "/upload")
//	public Result<?> upload(HttpServletRequest request, HttpServletResponse response) {
//		Result<?> result = new Result<>();
//		String savePath = "";
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		MultipartFile file = multipartRequest.getFile("file");// 获取上传文件对象
//		SmbUtil smb=SmbUtil.getInstance(Constants.REMOTEURL);
//		smb.uploadFile(file);
//		if(oConvertUtils.isNotEmpty(savePath)){
//			result.setMessage(savePath);
//			result.setSuccess(true);
//		}else {
//			result.setMessage("上传失败！");
//			result.setSuccess(false);
//		}
//		return result;
//	}




	/**
	 * 预览图片&资源文件
	 * 请求地址：http://localhost:8080/common/static/{user/20190119/e1fe9925bc315c60addea1b98eb1cb1349547719_1547866868179.jpg}
	 *
	 * @param request
	 * @param response
	 */
//	@GetMapping(value = "/static/**")
//	public void view(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		SmbUtil smb=SmbUtil.getInstance(Constants.REMOTEURL);
//		smb.dowloafFile(request,response);
//	}

	@GetMapping(value = "/static/**")
	public void view(HttpServletRequest request, HttpServletResponse response) throws IOException {
		download(request,response);
	}


	public void download(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String imgPath = extractPathFromPattern(request);
		if(oConvertUtils.isEmpty(imgPath) || imgPath=="null"){
			return;
		}
		imgPath = imgPath.replace("..", "");
		if (imgPath.endsWith(",")) {
			imgPath = imgPath.substring(0, imgPath.length() - 1);
		}
		String fullPath = Constants.ROOT_PATH + File.separator + imgPath;
		OutputStream outputStream = response.getOutputStream(); // 获取输出流
		outputStream = new BufferedOutputStream(outputStream); // 创建缓冲输出流

		if (!FileUtil.exist(fullPath)){
			return;
		}

		byte[] bytes = FileUtil.readBytes(fullPath); // 读取文件

		outputStream.write(bytes); // 输出文件;
		outputStream.flush();
		outputStream.close();
	}

	private static String extractPathFromPattern(final HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
	}
//	@GetMapping(value = "/video/**")
//	public void video(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		SmbUtil smb=SmbUtil.getInstance(Constants.VV);
//		smb.dowloafFile(request,response);
//	}

	@GetMapping(value = "/video/**")
	public void video(HttpServletRequest request, HttpServletResponse response) throws IOException {
		download(request,response);
	}

}
