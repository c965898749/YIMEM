package com.sy.tool;

import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
    private String url = null;
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
            //判断远程文件夹是否存在，如果不存在则创建
            if(!smbFile.exists()){
                smbFile.mkdirs();
            }
            System.out.println("连接成功...url：" + this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.print(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print(e);
        }
    }


    public int uploadFile(MultipartFile multipartFilefile,String name) {
        int flag = -1;
        BufferedInputStream bf = null;
        try {
            byte [] byteArr = multipartFilefile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(byteArr);
            bf = new BufferedInputStream(inputStream);
            this.smbOut = new SmbFileOutputStream(this.url + "/"
                    + name, false);
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

    // 4.资源文件到本地
    public void dowloafFile(HttpServletRequest request, HttpServletResponse response) throws IOException {//        filePath=request.getAttribute("filePath").toString();
        // ISO-8859-1 ==> UTF-8 进行编码转换
        String imgPath = extractPathFromPattern(request);
        if(oConvertUtils.isEmpty(imgPath) || imgPath=="null"){
            return;
        }
//        String filePath="smb://yimem:c866971331@192.168.0.117/lishi/ccc.txt";
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
                System.out.println("资源的文件不存在！");
            }
            if (Constants.VV.equals(url)){
                response.setContentLength(Integer.parseInt(file.length()+""));
                response.setHeader("Content-Range", "" + Integer.valueOf(Integer.parseInt(file.length()+"") - 1));
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Etag", "W/\"9767057-1323779115364\"");
//                response.setContentType("video/mp4");
//                response.setHeader("Accept-Ranges", "bytes");
//                response.setHeader("Etag", "W/\"9767057-1323779115364\"");
            }else {
                response.setContentType("application/x-msdownload");
            }
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

    public static void deleteFile(String url,String shareFolderPath) {
        SmbFile SmbFile;
        try {
//             smb://userName:passWord@host/path/shareFolderPath/fileName
            String[] cc=shareFolderPath.split("/common/static");
            if (cc.length>1){
                SmbFile = new SmbFile(url + cc[1]);
                if (SmbFile.exists()) {
                    SmbFile.delete();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 读取共享文件夹下的所有文件(文件夹)的名称
     * @param remoteUrl
     */
    public static List<String> getSharedFileList(String remoteUrl) {
        SmbFile smbFile;
        List<String> list=new ArrayList<>();
        try {
            // smb://userName:passWord@host/path/
            smbFile = new SmbFile(remoteUrl);
            if (!smbFile.exists()) {
//                System.out.println("no such folder");
            } else {
                SmbFile[] files = smbFile.listFiles();
                for (SmbFile f : files) {
                    list.add(f.getName());
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return  list;
    }
}
