package com.sy.tool;

import java.io.File;

public class ConverVideoTest {

    /**
     * @Description:(1.转码和截图功能调用)
     * @param:@param yuanPATH
     * @return:void
     * @author:pxc
     * @version:V1.0
     */

    /*本地测试专用--zoutao*/
//    public static void main(String[] args) {
//        ConverVideoTest c = new ConverVideoTest();
//        String yuanPATH = "D:/shipingzhuanma/22.avi";  //本地源视频
//        c.run(yuanPATH);
//    }
    public static void main(String[] args) {
        File file = new File("D:\\shipingzhuanma");
        delete(file);
    }
    private static void delete(File f) {
        File[] fi = f.listFiles();
        for (File file : fi) {
            if (file.isDirectory()) {
                delete(file);
            } else if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("avi")) {
                System.out.println("成功删除" + file.getName());
                file.delete();

            }
        }
    }

    //web调用
    public void run(String yuanPATH) {
        try {
            // 转码和截图功能开始

            //String filePath = "D:/testfile/video/rmTest.rm";  //本地源视频测试

            String filePath = yuanPATH;				//web传入的源视频
            System.out.println("ConverVideoTest说:传入工具类的源视频为:"+filePath);

            ConverVideoUtils zout = new ConverVideoUtils(filePath);  //传入path
            String targetExtension = ".mp4";  				//设置转换的格式
//            String targetExtension = ".mp4";  				//设置转换的格式
            boolean isDelSourseFile = false;

            //删除源文件
            boolean beginConver = zout.beginConver(targetExtension,isDelSourseFile);
            System.out.println(beginConver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


