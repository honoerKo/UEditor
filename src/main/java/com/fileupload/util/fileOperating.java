package com.fileupload.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

/**文件上传
 * @author WF
 *
 */
@Component
public class fileOperating {

	/** 
    * 复制单个文件 
    * @param oldPath String 原文件路径 
    * @param newPath String 复制后路径 
    * @return boolean 
    * @throws IOException 
    */ 
    public int copyImage(String oldPath, String newPath) throws IOException { 
    	
    	File oldfile = new File(oldPath);
	    if (oldfile.exists()) { //文件存在时 
			InputStream in = new FileInputStream(oldPath); //读入原文件 
		    FileOutputStream out = new FileOutputStream(newPath); //写入目标路径
		    fileInToOut(in, out);
		    //读写完删除临时文件夹中的图片
		    /*if("image".equals(srcSpl)){ 
		    	oldfile.delete();
		    }*/
		    return 1;
	    }else {
			return 0;
		}
    }
	 
    /**
     * 网络图片上传
     * @param src
     * @param newPath
     * @return
     * @throws Exception
     */
    public int uploadWebImage(String oldPath,String newPath) throws Exception{
    	URL url = new URL(oldPath);
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	InputStream in = conn.getInputStream();
    	FileOutputStream out = new FileOutputStream(newPath);
    	fileInToOut(in, out);
    	return 1;
    }
    
    /**IO流
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private static void fileInToOut(InputStream in, FileOutputStream out) throws IOException {
		byte[] buffer = new byte[1024]; 
		@SuppressWarnings("unused")
		int bytesum = 0; 
		int byteread = 0;
		while ((byteread = in.read(buffer)) != -1) { 
		    bytesum += byteread; //字节数 文件大小 
		    out.write(buffer, 0, byteread); 
		}
		if(null!=out){
			out.flush();
		    out.close();
		    in.close();
		}
	}
	
	/**
     * 判断非当天的文件夹执行删除操作
     * @param path 临时文件夹的上级目录
     */
    public void delBeforedayFolder(String path){
		String folderPath = this.getDate();//保留的文件夹名
		File file = new File(path);
		File[] files = file.listFiles();
		if(files.length > 1){
			for (int i = 0; i < files.length; i++) {
				if(!folderPath.equals(files[i].getName())){
					deleteFolder(files[i]);
				}
			}
		}		
    }
    
    /**
     * 删除文件夹
     * @param file 文件夹路径
     */
    private static void deleteFolder(File file){
    	if(file != null && file.exists()){
    		if(file.isFile()){
    			file.delete();
    		}else if(file.isDirectory()){
    			File[] files = file.listFiles();
    			for (int i = 0; i < files.length; i++) {
    				deleteFolder(files[i]);
				}
    		}
    		file.delete();
    	}
    }
    
    /**
     * 获取当天日期  格式：20180314
     * @return
     */
    public String getDate(){
    	Date date = Calendar.getInstance().getTime();		
		DateFormat df = new SimpleDateFormat("yyyyMMdd"); 
		String str = df.format(date);
		return str;
    }
}
