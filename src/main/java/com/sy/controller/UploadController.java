package com.sy.controller;

import cn.hutool.core.io.FileUtil;
import com.sy.expection.CsdnExpection;
import com.sy.mapper.UploadMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.Power;
import com.sy.model.Upload;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.model.resp.ResultVO;
import com.sy.service.PowerService;
import com.sy.service.UploadService;
import com.sy.service.UserServic;
import com.sy.tool.*;


import com.sy.vo.Result;
import jcifs.smb.SmbFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class UploadController {
    //    权限注入
    @Autowired
    private PowerService powerService;
    @Autowired
    private UploadMapper uploadMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UploadService uploadService;
    @Autowired
    UserServic servic;

    @RequestMapping("/upload/yulang")
    @ResponseBody
    public BaseResp yulang(Integer id) {
        BaseResp baseResp = new BaseResp();
        Upload upload = new Upload();
        try {
            upload = uploadService.findById(id);
            if (upload == null) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("资源不存在！");
            }
        } catch (CsdnExpection csdnExpection) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("资源查询异常");
        }
        baseResp.setSuccess(200);
        baseResp.setData(upload.getSrc());
//        if ("pdf".equals(upload.getLeixin2()) || "flac".equals(upload.getLeixin2()) || "mp3".equals(upload.getLeixin2()) || "mp4".equals(upload.getLeixin2())) {
//            baseResp.setSuccess(200);
//            baseResp.setData(upload.getSrc());
//        } else {
//            baseResp.setSuccess(0);
//            baseResp.setErrorMsg("该文件格式暂不支持预览");
//        }
        return baseResp;
    }

    @RequestMapping("/upload/remove")
    @ResponseBody
    public BaseResp remove(Integer id, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user = servic.getUserByRedis(request);
        if (user != null) {
            Upload upload = new Upload();
            upload.setId(id);
            upload.setUserid(user.getUserId());
            List<Upload> uploads = uploadMapper.selectAll(upload);
            if (Xtool.isNotNull(uploads)) {
                try {
//                    FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
//                    uploads.forEach(x -> {
//                        uploadMapper.delete(user.getUserId(), x.getId());
//                        user.setResourceCount(user.getResourceCount() - 1);
//                        userMapper.updateuser(user);
//                        String url = x.getSrc().replace(Constants.IMAGE_SERVER_URL + "group1/", "");
//                        fastDFSClient.deleteFile("group1", url);
//                    });
//                    SmbFile = new SmbFile(remoteUrl + shareFolderPath + "/" + fileName);
//                    if (SmbFile.exists()) {
//                        SmbFile.delete();
//                    }
                    uploads.forEach(x -> {
                        uploadMapper.delete(user.getUserId(), x.getId());
                        user.setResourceCount(user.getResourceCount() - 1);
                        userMapper.updateuser(user);
                        SmbUtil.deleteFile(Constants.REMOTEURL, x.getSrc());
                    });
                    baseResp.setSuccess(1);
                    baseResp.setErrorMsg("删除成功");
                    return baseResp;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("删除失败");
                return baseResp;
            } else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("你无权删除该资源");
                return baseResp;
            }

        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("未登入");
        return baseResp;
    }

    /**
     * //     * ajax上传
     * //
     */
//    @RequestMapping("interviewUpload")
//    @ResponseBody
    public BaseResp ajaxUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            Power power = powerService.getPowerByUserId(user.getUserId());
            try {
                //接收上传的文件
                //取扩展名
                String originalFilename = file.getOriginalFilename();
                String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                Long loadSize = 209715200L;
                if (power != null) {
                    System.out.println(power.getUploadAccessAlevel());
                    loadSize = AllEnum.SizeEnum(power.getUploadAccessAlevel());
                }
                // 检查文件大小
                if (file.getSize() > loadSize) {
                    baseResp.setSuccess(0);
                    baseResp.setErrorMsg("上传文件超过限制");
                    return baseResp;
                }
                System.out.println("接收文件");
                if (extName.equals("avi") || extName.equals("rm")
                        || extName.equals("rmvb") || extName.equals("wmv")
//                        || extName.equals("mp4") || extName.equals("wmv")
                        || extName.equals("3gp") || extName.equals("mov")
                        || extName.equals("ogg")) {
                    System.out.println("========开始调用视频转码工具类=======");
                    //调用转码机制flv mp4 f4v m3u8 webm ogg放行直接播放，
                    //asx，asf，mpg，wmv，3gp，mov，avi，wmv9，rm，rmvb等进行其他转码为mp4
                    String path = Constants.videoRealPath;
                    File TempFile = new File(path);
                    if (TempFile.exists()) {
                        if (TempFile.isDirectory()) {
                            System.out.println("该文件夹存在。");
                        } else {
                            System.out.println("同名的文件存在，不能创建文件夹。");
                        }
                    } else {
                        System.out.println("文件夹不存在，创建该文件夹。");
                        TempFile.mkdir();
                    }
//                    String randomcode = "";
//                    for (int i = 0; i < 6; i++) {
//                        //52个字母与6个大小写字母间的符号；范围为91~96
//                        int value = (int) (Math.random() * 58 + 65);
//                        while (value >= 91 && value <= 96)
//                            value = (int) (Math.random() * 58 + 65);
//                        randomcode = randomcode + (char) value;
//
//                    }
//                    randomcode = randomcode + "." + extName;
//                    //上传到本地磁盘/服务器
                    try {

                        System.out.println("写入本地磁盘/服务器");
                        InputStream is = file.getInputStream();
                        OutputStream os = new FileOutputStream(new File(path, originalFilename));
                        int len = 0;
                        byte[] buffer = new byte[2048];

                        while ((len = is.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        os.close();
                        os.flush();
                        is.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ConverVideoUtils zout = new ConverVideoUtils(Constants.videoRealPath + originalFilename);  //传入path
//                    //删除源文件
////                    boolean isDelSourseFile = false;
                    boolean isDelSourseFile = true;
//                    System.out.println(Constants.videoRealPath + originalFilename);
                    String targetExtension = ".mp4";                //设置转换的格式
                    zout.beginConver(targetExtension, isDelSourseFile);
//                    if (zout.beginConver(targetExtension, isDelSourseFile)) {
////                        System.out.println("ok----删除临时文件");
////                        File file2 = new File(Constants.videoRealPath+randomcode);
////                        file2.delete();
//                        FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
//                        String url = fastDFSClient.uploadFile(Constants.videoRealPath+originalFilename, "mp4");
//                        File file3 = new File(Constants.videoRealPath+originalFilename);
//                        file3.delete();
//                        url = Constants.IMAGE_SERVER_URL + url;
//                        String fid = "";
//                        System.out.println(url);
//                        baseResp.setSuccess(1);
//                        baseResp.setData(resultMap("SUCCESS", url, file.getSize(), fid, originalFilename, extName));
//                        baseResp.setErrorMsg("文件上传成功");
//                        return baseResp;
//                    } else {
//                        baseResp.setSuccess(0);
//                        baseResp.setErrorMsg("视频转码出错");
//                        return baseResp;
//                    }
                }
                //上传到图片服务器
//                FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
//                System.out.println("FastDFS上传文件");
//                String url = fastDFSClient.uploadFile(file.getBytes(), extName);
//                url = Constants.IMAGE_SERVER_URL + url;
//                String fid = "";
//                System.out.println(url);
//                baseResp.setSuccess(1);
//                baseResp.setData(resultMap("SUCCESS", url, file.getSize(), fid, originalFilename, extName));
                baseResp.setErrorMsg("文件上传成功");
                return baseResp;
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("文件上传出错");
                return baseResp;
            }
        }
    }

//     */

    /**
     * 头像上传
     *
     * @param file
     * @return
     */
    @RequestMapping("headimgUpload")
    @ResponseBody
    public BaseResp headimgUpload(@RequestParam("file") MultipartFile file) throws IOException {
//        return ajaxUpload(file,"headimg");
        return ajaxUpload2(file, "headimg");

    }

    /**
     * 博客资源上传
     *
     * @param file
     * @return
     */
    @RequestMapping("blogUpload")
    @ResponseBody
    public BaseResp blogUpload(@RequestParam("file") MultipartFile file) throws IOException {
//        return ajaxUpload(file,"blog");
        return ajaxUpload2(file, "blog");

    }

    /**
     * 面试资料上传
     *
     * @param file
     * @return
     */
    @RequestMapping("interviewUpload")
    @ResponseBody
    public BaseResp interviewUpload(@RequestParam("file") MultipartFile file) throws IOException {
//        return ajaxUpload(file,"interview");
        return ajaxUpload2(file, "interview");

    }


    /**
     * 资源上传
     *
     * @param file
     * @return
     */
    @RequestMapping("resourceUpload")
    @ResponseBody
    public BaseResp resourceUpload(@RequestParam("file") MultipartFile file) throws IOException {
//        return ajaxUpload(file,"resource");
        return ajaxUpload2(file, "resource");

    }

    //    @RequestMapping("fileUpload")
//    @ResponseBody
    private BaseResp ajaxUpload(MultipartFile file, String type) {
        BaseResp baseResp = new BaseResp();

        Date date = new Date(); //
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String savePath = formatter.format(date);
        SmbUtil smb = SmbUtil.getInstance(Constants.REMOTEURL + "/" + type + "/" + savePath);
        String name = getRandomName(file.getOriginalFilename());
        if (smb.uploadFile(file, name) >= 0) {
            savePath = "/common/static/" + type + "/" + savePath + "/" + name;
            baseResp.setSuccess(1);
            String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            baseResp.setData(resultMap("SUCCESS", savePath, file.getSize(), "", file.getOriginalFilename(), extName));
            baseResp.setErrorMsg("文件上传成功");
            return baseResp;
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("文件上传失败");
            return baseResp;
        }

    }

    public BaseResp ajaxUpload2(MultipartFile file, String type) throws IOException {
        BaseResp baseResp = new BaseResp();

        Date date = new Date(); //
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String savePath = formatter.format(date);

        String originalFilename = file.getOriginalFilename(); // aaa.jpg
//    String mainName = FileUtil.mainName(originalFilename); aaa 文件名
//    String ext = FileUtil.extName(originalFilename); jpg 文件后缀
        String name = getRandomName(file.getOriginalFilename());
        String fullPath = Constants.ROOT_PATH + "/" + type + "/" + savePath;

        if (!FileUtil.exist(fullPath)) {
            FileUtil.mkdir(fullPath); // 父目录不存在则创建
        }
        if (FileUtil.exist(fullPath)) {
            fullPath = fullPath + File.separator + name; // 文件重复则重新指定文件名
        }
        file.transferTo(new File(fullPath)); // 存储文件到本地
        savePath = "/common/static/" + type + "/" + savePath + "/" + name;
        baseResp.setSuccess(1);
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        baseResp.setData(resultMap("SUCCESS", savePath, file.getSize(), "", file.getOriginalFilename(), extName));
        baseResp.setErrorMsg("文件上传成功");
        return baseResp;
    }


    public static String getRandomName(String fileName) {
        int index = fileName.lastIndexOf(".");
        String houzhui = fileName.substring(index);//获取后缀名
        String uuidFileName = UUID.randomUUID().toString().replace("-", "") + houzhui;
        return uuidFileName;
    }

//    @RequestMapping("imgUpload")
//    @ResponseBody
//    public ResultVO ajaximge(@RequestParam("imgFile") MultipartFile file, HttpServletRequest request) {
//        ResultVO result = new ResultVO();
//        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) {
//            result.setError(0);
//            result.setMessage("未登陆");
//            return result;
//        } else {
//            try {
//                //接收上传的文件
//                //取扩展名
//                String originalFilename = file.getOriginalFilename();
//                String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
//                //上传到图片服务器
//                // 定义允许上传的文件扩展名
//                String dirName = request.getParameter("dir");
//                HashMap<String, String> extMap = new HashMap<String, String>();
//                extMap.put("image", "gif,jpg,jpeg,png,bmp");
//                if (!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(extName)) {
//                    result.setError(1);
//                    result.setMessage("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。");
//                    return result;
//                }
//                // 检查文件大小
//                if (file.getSize() > Constants.Max_SIZE) {
//                    result.setError(1);
//                    result.setMessage("上传图片大小超过限制");
//                    return result;
//                }
//                FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
//                String url = fastDFSClient.uploadFile(file.getBytes(), extName);
//                url = Constants.IMAGE_SERVER_URL + url;
//                String fid = "";
//                System.out.println(url);
//                result.setError(0);
//                result.setMessage("上传成功");
//                result.setUrl(url);
//                return result;
//            } catch (Exception e) {
//                e.printStackTrace();
//                result.setError(1);
//                result.setMessage("系统故障，稍后重试！");
//                return result;
//            }
//        }
//
//    }

    private static Map<String, Object> resultMap(String state, String url, long size, String title, String original, String type) {
        Map<String, Object> result = new HashMap();
        result.put("state", state);// 上传成功与否
        result.put("url", url);// 上传后图片 完整的url
        result.put("size", size);// 图片大小
        result.put("title", title);// 上传后 的图片名，即FID（fastdfs处理后的文件名）
        result.put("original", original);// 上传前文件名
        result.put("type", type);// 文件 后缀
        return result;
    }


}
