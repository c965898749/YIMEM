package com.sy.tool;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FastDFSClient {

	private TrackerClient trackerClient = null;
	private TrackerServer trackerServer = null;
	private StorageServer storageServer = null;
	private StorageClient1 storageClient = null;

	public FastDFSClient(String conf) throws Exception {
		if (conf.contains("classpath:")) {
			conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
		}
		ClientGlobal.init(conf);
		trackerClient = new TrackerClient();
		trackerServer = trackerClient.getConnection();
		storageServer = null;
		storageClient = new StorageClient1(trackerServer, storageServer);
	}

	/**
	 * 上传文件方法
	 * <p>Title: uploadFile</p>
	 * <p>Description: </p>
	 * @param fileName 文件全路径
	 * @param extName 文件扩展名，不包含（.）
	 * @param metas 文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
		String result = storageClient.upload_file1(fileName, extName, metas);
		return result;
	}

	public String uploadFile(String fileName) throws Exception {
		return uploadFile(fileName, null, null);
	}

	public String uploadFile(String fileName, String extName) throws Exception {
		return uploadFile(fileName, extName, null);
	}

	/**
	 * 上传文件方法
	 * <p>Title: uploadFile</p>
	 * <p>Description: </p>
	 * @param fileContent 文件的内容，字节数组
	 * @param extName 文件扩展名
	 * @param metas 文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

		String result = storageClient.upload_file1(fileContent, extName, metas);
		return result;
	}

	public String uploadFile(byte[] fileContent) throws Exception {
		return uploadFile(fileContent, null, null);
	}

	public String uploadFile(byte[] fileContent, String extName) throws Exception {
		return uploadFile(fileContent, extName, null);
	}
//***************以下为自己新增***********************************************************
	/**
	 * 资源文件到本地  张然
	 * @param b
	 * @param path
	 * @param fileName
	 */
	//将字节流写到磁盘生成文件
    private void saveFile(byte[] b, String path, String fileName) {

    	File file = new File(path+fileName);
    	FileOutputStream fileOutputStream = null;
    	try {
			fileOutputStream= new FileOutputStream(file);

			fileOutputStream.write(b);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fileOutputStream!=null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

  	}
	/**
	 * 资源文件 张然
	 * @param group
	 * @param pathAndFilename
	 */
    public void downloadFile(String group,String pathAndFilename) {
        try {
            byte[] b = storageClient.download_file(group,
            		pathAndFilename);
            String ext ="."+ pathAndFilename.split("\\.")[1];
            if(b !=null){
            	 System.out.println(b.length);
                 saveFile(b, "E:/FASTDFS_download/", UUID.randomUUID().toString()+ext);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   /**
    * 删除 文件 张然
    * @param group
    * @param pathAndFilename
    */
    public void deleteFile(String group,String pathAndFilename){
        try {
            int i = storageClient.delete_file(group, pathAndFilename);
            System.out.println( i==0 ? "删除成功" : "删除失败:"+i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
