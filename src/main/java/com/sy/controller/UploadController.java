package com.sy.controller;

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
import com.sy.tool.AllEnum;
import com.sy.tool.Constants;
import com.sy.tool.FastDFSClient;

import com.sy.tool.Xtool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @RequestMapping("/upload/yulang")
    @ResponseBody
    public  BaseResp yulang(Integer id){
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
        if ("pdf".equals(upload.getLeixin2())||"flac".equals(upload.getLeixin2())||"mp3".equals(upload.getLeixin2())||"mp4".equals(upload.getLeixin2())){
            baseResp.setSuccess(200);
            baseResp.setData(upload.getSrc());
        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("该文件格式暂不支持预览");
        }
        return baseResp;
    }

    @RequestMapping("/upload/remove")
    @ResponseBody
    public BaseResp remove(Integer id, HttpServletRequest request) {
        BaseResp baseResp = new BaseResp();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未登入");
            return baseResp;
        } else {
            Upload upload = new Upload();
            upload.setId(id);
            upload.setUserid(user.getUserId());
            List<Upload> uploads =uploadMapper.selectAll(upload);
            if (Xtool.isNotNull(uploads)){
                try {
                    FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
                    uploads.forEach(x->{
                        uploadMapper.delete(user.getUserId(),x.getId());
                        user.setResourceCount(user.getResourceCount()-1);
                        userMapper.updateuser(user);
                        String url=x.getSrc().replace(Constants.IMAGE_SERVER_URL+"group1/","");
                        fastDFSClient.deleteFile("group1",url);
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
            }else {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("你无权删除该资源");
                return baseResp;
            }

        }
    }

    /**
     * ajax上传
     */
    @RequestMapping("fileUpload")
    @ResponseBody
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
                //上传到图片服务器
                FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
                String url = fastDFSClient.uploadFile(file.getBytes(), extName);
                url = Constants.IMAGE_SERVER_URL + url;
                String fid = "";
                System.out.println(url);
                baseResp.setSuccess(1);
                baseResp.setData(resultMap("SUCCESS", url, file.getSize(), fid, originalFilename, extName));
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

    @RequestMapping("imgUpload")
    @ResponseBody
    public ResultVO ajaximge(@RequestParam("imgFile") MultipartFile file, HttpServletRequest request) {
        ResultVO result = new ResultVO();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            result.setError(0);
            result.setMessage("未登陆");
            return result;
        } else {
            try {
                //接收上传的文件
                //取扩展名
                String originalFilename = file.getOriginalFilename();
                String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                //上传到图片服务器
                // 定义允许上传的文件扩展名
                String dirName = request.getParameter("dir");
                HashMap<String, String> extMap = new HashMap<String, String>();
                extMap.put("image", "gif,jpg,jpeg,png,bmp");
                if (!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(extName)) {
                    result.setError(1);
                    result.setMessage("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。");
                    return result;
                }
                // 检查文件大小
                if (file.getSize() > Constants.Max_SIZE) {
                    result.setError(1);
                    result.setMessage("上传图片大小超过限制");
                    return result;
                }
                FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
                String url = fastDFSClient.uploadFile(file.getBytes(), extName);
                url = Constants.IMAGE_SERVER_URL + url;
                String fid = "";
                System.out.println(url);
                result.setError(0);
                result.setMessage("上传成功");
                result.setUrl(url);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                result.setError(1);
                result.setMessage("系统故障，稍后重试！");
                return result;
            }
        }

    }

    private Map<String, Object> resultMap(String state, String url, long size, String title, String original, String type) {
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
