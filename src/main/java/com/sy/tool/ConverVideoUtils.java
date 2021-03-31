package com.sy.tool;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title: ConverVideoUtils.java
 * @Package:com.resource.mytools
 * @Description:(2.转码和截图功能)
 * @see:接收Contants实体的路径
 * @author:Zoutao
 * @date:2018-7-15
 * @version:V1.0
 */

/***
 * 注意：而video标签支持的mp4文件只能是：H.264 视频编码和 AAC 音频编码的 MPEG 4 文件 (并非全部浏览器)
 * 这个文件就有些复杂了，其中涉及到视频的转码、截图、转码是需要判断视频类型的，
 *
 * 如果是ffmpeg能解析的格式:(asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等)就用ffmpeg转码成mp4。
 *
 * 如果是ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 就先用别的工具（mencoder）转换为avi，然后在采用ffmpeg来转码成mp4。
 *
 * 每个视频还会自动生成一张相关的图片。用于前台展示。
 * 其中视频是转码成h.264的编码格式，很多播放器对这个有要求。
 *
 * 这样以后，等web上传一个视频，就会上传，然后转码成mp4，然后数据记录存入sql中
 *  ————————————————
 * 版权声明：本文为CSDN博主「csdnzoutao」的原创文章，遵循CC 4.0 by-sa版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/ITBigGod/article/details/81747046
 */
public class ConverVideoUtils {
    private String sourceVideoPath;                            //源视频路径
    private String filerealname;                            //文件名不包括后缀名
    private String filename;                                //包括后缀名
    private String videofolder = Constants.videofolder;        // 别的格式视频的目录
    private String targetfolder = Constants.targetfolder;    // flv视频的目录
    private String ffmpegpath = Constants.ffmpegpath;         // ffmpeg.exe的目录
    private String mencoderpath = Constants.mencoderpath;    // mencoder的目录
    private String imageRealPath = Constants.imageRealPath;   // 截图的存放目录

    public ConverVideoUtils() {
    }

    //重构构造方法，传入源视频
    public ConverVideoUtils(String path) {
        sourceVideoPath = path;
    }

    //set和get方法传递path
    public String getPATH() {
        return sourceVideoPath;
    }

    public void setPATH(String path) {
        sourceVideoPath = path;
    }

    /**
     * 转换视频格式
     * @param  targetExtension 目标视频后缀名 .xxx
     * @param  isDelSourseFile 转换完成后是否删除源文件
     * @return
     */
    public boolean beginConver(String targetExtension, boolean isDelSourseFile) {
        File fi = new File(sourceVideoPath);

        filename = fi.getName();             //获取文件名+后缀名

        filerealname = filename.substring(0, filename.lastIndexOf(".")); //获取不带后缀的文件名-后面加.toLowerCase()小写

        System.out.println("----接收到文件(" + sourceVideoPath + ")需要转换-------");

        //检测本地是否存在
		/*if (checkfile(sourceVideoPath)) {
			System.out.println(sourceVideoPath + "========该文件存在哟 ");
			return false;
		}*/

        System.out.println("----开始转文件(" + sourceVideoPath + ")-------------------------- ");

        //执行转码机制
        if (process(targetExtension, isDelSourseFile)) {

//            System.out.println("视频转码结束，开始截图================= ");
//
//            //视频转码完成，调用截图功能--zoutao
//            if (processImg(sourceVideoPath)) {
//                System.out.println("截图成功！ ");
//            } else {
//                System.out.println("截图失败！ ");
//            }


            //删除原视频+临时视频
			/*if (isDelSourseFile) {
				deleteFile(sourceVideoPath);
			}*/

			/*File file1 = new File(sourceVideoPath);
	         if (file1.exists()){
	        	 System.out.println("删除原文件-可用："+sourceVideoPath);
	             file1.delete();
	          }*/

            String temppath = videofolder + filerealname + ".avi";
            File file2 = new File(temppath);
            if (file2.exists()) {
                System.out.println("删除临时文件：" + temppath);
                file2.delete();
            }

            sourceVideoPath = null;
            return true;
        } else {
            sourceVideoPath = null;
            return false;
        }
    }


    /**
     * 检查文件是否存在-多处都有判断
     * @param path
     * @return
     */

	/*private boolean checkfile(String path) {
		path = sourceVideoPath;
		File file = new File(path);
		try {
			if (file.exists()) {
				System.out.println("视频文件不存在============="+path);
				return true;
			} else {
				System.out.println("视频文件存在"+path);
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("拒绝对文件进行读访问");
		}
		return false;
	}*/


    /**
     * 视频截图功能
     * @param sourceVideoPath 需要被截图的视频路径（包含文件名和后缀名）
     * @return
     */
    public boolean processImg(String sourceVideoPath) {

        //先确保保存截图的文件夹存在
        File TempFile = new File(imageRealPath);
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

        File fi = new File(sourceVideoPath);
        filename = fi.getName();            //获取视频文件的名称。
        filerealname = filename.substring(0, filename.lastIndexOf("."));    //获取视频名+不加后缀名 后面加.toLowerCase()转为小写

        List<String> commend = new ArrayList<String>();
        //第一帧： 00:00:01
        //截图命令：time ffmpeg -ss 00:00:01 -i test1.flv -f image2 -y test1.jpg

        commend.add(ffmpegpath);            //指定ffmpeg工具的路径
        commend.add("-ss");
        commend.add("00:00:01");            //1是代表第1秒的时候截图
        commend.add("-i");
        commend.add(sourceVideoPath);        //截图的视频路径
        commend.add("-f");
        commend.add("image2");
        commend.add("-y");
        commend.add(imageRealPath + filerealname + ".jpg");        //生成截图xxx.jpg

        //打印截图命令--zoutao
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++) {
            test.append(commend.get(i) + " ");
        }
        System.out.println("截图命令:" + test);

        //转码后完成截图功能-还是得用线程来解决--zoutao
        try {
			/*ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			Process p =builder.start();*/
            //调用线程处理命令
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();

            //获取进程的标准输入流
            final InputStream is1 = p.getInputStream();
            //获取进程的错误流
            final InputStream is2 = p.getErrorStream();
            //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
            new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is1));
                    try {
                        String lineB = null;
                        while ((lineB = br.readLine()) != null) {
                            if (lineB != null) {
                                //System.out.println(lineB);    //必须取走线程信息避免堵塞
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //关闭流
                    finally {
                        try {
                            is1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            new Thread() {
                public void run() {
                    BufferedReader br2 = new BufferedReader(
                            new InputStreamReader(is2));
                    try {
                        String lineC = null;
                        while ((lineC = br2.readLine()) != null) {
                            if (lineC != null) {
                                //System.out.println(lineC);   //必须取走线程信息避免堵塞
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //关闭流
                    finally {
                        try {
                            is2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }.start();
            // 等Mencoder进程转换结束，再调用ffmepg进程非常重要！！！
            p.waitFor();
            System.out.println("截图进程结束");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 实际转换视频格式的方法
     * @param targetExtension 目标视频后缀名
     * @param isDelSourseFile 转换完成后是否删除源文件
     * @return
     */
    public boolean process(String targetExtension, boolean isDelSourseFile) {

        //先判断视频的类型-返回状态码
        int type = checkContentType();
        boolean status = false;

        //根据状态码处理
        if (type == 0) {
            System.out.println("ffmpeg可以转换,统一转为mp4文件");

            status = processVideoFormat(sourceVideoPath, targetExtension, isDelSourseFile);//可以指定转换为什么格式的视频

        } else if (type == 1) {
            //如果type为1，将其他文件先转换为avi，然后在用ffmpeg转换为指定格式
            System.out.println("ffmpeg不可以转换,先调用mencoder转码avi");
            String avifilepath = processAVI(type);

            if (avifilepath == null) {
                // 转码失败--avi文件没有得到
                System.out.println("mencoder转码失败,未生成AVI文件");
                return false;
            } else {
                System.out.println("生成AVI文件成功,ffmpeg开始转码:");
                status = processVideoFormat(avifilepath, targetExtension, isDelSourseFile);
            }
        }
        return status;   //执行完成返回布尔类型true
    }

    /**
     * 检查文件类型
     * @return
     */
    private int checkContentType() {

        //取得视频后缀-
        String type = sourceVideoPath.substring(sourceVideoPath.lastIndexOf(".") + 1, sourceVideoPath.length()).toLowerCase();
        System.out.println("源视频类型为:" + type);

        // 如果是ffmpeg能解析的格式:(asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等)
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 0;
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 0;
        } else if (type.equals("mkv")) {
            return 0;
        }


        // 如果是ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        // 就先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        System.out.println("上传视频格式异常");
        return 9;
    }


    /**
     *  对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
     *  可以先用（mencoder）转换为avi(ffmpeg能解析的)格式.再用ffmpeg解析为指定格式
     * @param type
     * @return
     */
    private String processAVI(int type) {

        System.out.println("调用了mencoder.exe工具");
        List<String> commend = new ArrayList<String>();

        commend.add(mencoderpath);                //指定mencoder.exe工具的位置
        commend.add(sourceVideoPath);             //指定源视频的位置
        commend.add("-oac");
        commend.add("mp3lame");            //lavc 原mp3lame
        commend.add("-lameopts");
        commend.add("preset=64");
        commend.add("-ovc");
        commend.add("xvid");        //mpg4(xvid),AVC(h.264/x264),只有h264才是公认的MP4标准编码，如果ck播放不了，就来调整这里
        commend.add("-xvidencopts");  //xvidencopts或x264encopts
        commend.add("bitrate=600");        //600或440
        commend.add("-of");
        commend.add("avi");
        commend.add("-o");

        commend.add(videofolder + filerealname + ".avi");   //存放路径+名称，生成.avi视频

        //打印出转换命令-zoutao
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++) {
            test.append(commend.get(i) + " ");
        }
        System.out.println("mencoder输入的命令:" + test);
        // cmd命令：mencoder 1.rmvb -oac mp3lame -lameopts preset=64 -ovc xvid
        // -xvidencopts bitrate=600 -of avi -o rmvb.avi

        try {
            //调用线程命令启动转码
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();   //多线程处理加快速度-解决数据丢失
            //doWaitFor(p);

            //获取进程的标准输入流
            final InputStream is1 = p.getInputStream();
            //获取进程的错误流
            final InputStream is2 = p.getErrorStream();
            //启动两个线程，一个线程负责读标准输出流，另一个负责读标准错误流
            new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is1));
                    try {
                        String lineB = null;
                        while ((lineB = br.readLine()) != null) {
                            if (lineB != null) {
                                System.out.println(lineB);    //打印mencoder转换过程代码-可注释
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //关闭流
                   /* finally{
                        try {
                          is1.close();
                        } catch (IOException e) {
                           e.printStackTrace();
                       }
                     }  */

                }
            }.start();
            new Thread() {
                public void run() {
                    BufferedReader br2 = new BufferedReader(
                            new InputStreamReader(is2));
                    try {
                        String lineC = null;
                        while ((lineC = br2.readLine()) != null) {
                            if (lineC != null) {
                                System.out.println(lineC);    //打印mencoder转换过程代码
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //关闭
                   /* finally{
                        try {
                            is2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                      } */

                }
            }.start();

            // 等Mencoder进程转换结束，再调用ffmepg进程非常重要！！！
            p.waitFor();
            System.out.println("Mencoder进程结束");
            return videofolder + filerealname + ".avi";        //返回转为AVI以后的视频地址

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 转换为指定格式--zoutao
     * ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
     * @param oldfilepath
     * @param targetExtension 目标格式后缀名 .xxx
     * @param isDelSourceFile 转换完成后是否删除源文件
     * @return
     */
    private boolean processVideoFormat(String oldfilepath, String targetExtension, boolean isDelSourceFile) {

        System.out.println("调用了ffmpeg.exe工具");

        //先确保保存转码后的视频的文件夹存在
        File TempFile = new File(targetfolder);
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

        List<String> commend = new ArrayList<String>();

        commend.add(ffmpegpath);         //ffmpeg.exe工具地址
        commend.add("-i");
        commend.add(oldfilepath);            //源视频路径
        commend.add("-i");
        commend.add(oldfilepath);
        commend.add("-acodec");
        commend.add("copy");
        commend.add("-vcodec");
        commend.add("libx264");
        commend.add("-preset");
        commend.add("superfast");
        commend.add("-y");

        commend.add(targetfolder + filerealname + targetExtension);  // //转码后的路径+名称，是指定后缀的视频

        //打印命令--zoutao
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++) {
            test.append(commend.get(i) + " ");
        }
        System.out.println("ffmpeg输入的命令:" + test);

        try {
            //多线程处理加快速度-解决rmvb数据丢失builder名称要相同
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();   //多线程处理加快速度-解决数据丢失

            final InputStream is1 = p.getInputStream();
            final InputStream is2 = p.getErrorStream();
            new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is1));
                    try {
                        String lineB = null;
                        while ((lineB = br.readLine()) != null) {
                            if (lineB != null)
                                System.out.println(lineB);    //打印mencoder转换过程代码
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            new Thread() {
                public void run() {
                    BufferedReader br2 = new BufferedReader(
                            new InputStreamReader(is2));
                    try {
                        String lineC = null;
                        while ((lineC = br2.readLine()) != null) {
                            if (lineC != null)
                                System.out.println(lineC);    //打印mencoder转换过程代码
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            p.waitFor();        //进程等待机制，必须要有，否则不生成mp4！！！
            System.out.println("生成mp4视频为:" + targetfolder + filerealname + ".mp4");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
