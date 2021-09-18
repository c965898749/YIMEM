package com.sy.tool;

import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SmbUtil {
    // 1. 声明属性
    private String url = "smb://yimem:c866971331@192.168.5.100/lishi/";
    private SmbFile smbFile = null;
    private SmbFileOutputStream smbOut = null;
    private static SmbUtil smbUtil = null; // 共享文件协议

    private SmbUtil(String url) {
        this.url = url;
        this.init();
    }

    // 2. 得到SmbUtil和连接的方法
    public static synchronized SmbUtil getInstance(String url) {
        if (smbUtil == null)
            return new SmbUtil(url);
        return smbUtil;
    }


    // 3.smbFile连接
    public void init() {
        try {
            System.out.println("开始连接...url：" + this.url);
            smbFile = new SmbFile(this.url);
            smbFile.connect();
            System.out.println("连接成功...url：" + this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.print(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print(e);
        }
    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 4.上传文件到服务器
//    public int uploadFile(File file) {
    public int uploadFile(MultipartFile multipartFilefile) {
        File file = null;
        int flag = -1;
        BufferedInputStream bf = null;
        try {
            if (multipartFilefile.equals("") || multipartFilefile.getSize() <= 0) {
               return 0;
            } else {
                InputStream ins = null;
                ins = multipartFilefile.getInputStream();
                file = new File(multipartFilefile.getOriginalFilename());
                inputStreamToFile(ins, file);
                ins.close();
            }
            this.smbOut = new SmbFileOutputStream(this.url + "/"
                    + file.getName(), false);
            bf = new BufferedInputStream(new FileInputStream(file));
            byte[] bt = new byte[8192];
            int n = bf.read(bt);
            while (n != -1) {
                this.smbOut.write(bt, 0, n);
                this.smbOut.flush();
                n = bf.read(bt);
            }
            flag = 0;
            System.out.println("文件传输结束...");
        } catch (SmbException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("找不到主机...url：" + this.url);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        } finally {
            try {
                if (null != this.smbOut)
                    this.smbOut.close();
                if (null != bf)
                    bf.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return flag;
    }


    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    // 4.下载文件到本地
    public void dowloafFile(HttpServletRequest request, HttpServletResponse response) throws IOException {//        filePath=request.getAttribute("filePath").toString();
        // ISO-8859-1 ==> UTF-8 进行编码转换
        String imgPath = extractPathFromPattern(request);
        if(oConvertUtils.isEmpty(imgPath) || imgPath=="null"){
            return;
        }
//        String filePath="smb://yimem:c866971331@192.168.5.100/lishi/ccc.txt";
//        fileName=request.getAttribute("fileName").toString();
        imgPath = imgPath.replace("..", "");
        if (imgPath.endsWith(",")) {
            imgPath = imgPath.substring(0, imgPath.length() - 1);
        }
        String filePath = url + File.separator + imgPath;
        String realPath = filePath;
        SmbFileInputStream in = null;
        OutputStream os = null;
        try {
            //java.io.File file=new java.io.File(realPath);
            SmbFile file = new SmbFile(realPath);
            if(!file.exists()) {
                System.out.println("下载的文件不存在！");
            }
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition","attachment; filename="+new String(file.getName().getBytes(),"ISO8859_1"));
            in = new SmbFileInputStream(file);
            DataInputStream dis=new DataInputStream(in);
            os = response.getOutputStream();
//            os =  new FileOutputStream("D:\\ccc.txt");
            byte[] buf=new byte[1024];
            int left=(int)file.length();
            int read=0;
            while(left>0) {
                read=dis.read(buf);
                left-=read;
                os.write(buf,0,read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    // 5. 在main方法里面测试
//    public static void main(String[] args) throws IOException {
////        // 服務器地址 格式為 smb://电脑用户名:电脑密码@电脑IP地址/IP共享的文件夹
//        String remoteUrl = "smb://yimem:c866971331@192.168.5.100/lishi/";
////        String localFile = "D:\\ccc.txt"; // 本地要上传的文件
////        File file = new File(localFile);
//        SmbUtil smb = SmbUtil.getInstance(remoteUrl);
////        smb.uploadFile(file);// 上传文件
////        smb.dowloafFile();
//    }
}
