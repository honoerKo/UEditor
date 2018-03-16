package com.fileupload.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fileupload.model.FileUpload;
import com.fileupload.service.IFileUploadService;
import com.fileupload.util.fileOperating;
import com.fileupload.util.stringOperating;

/**
 * 建立用户系统，以用户名单独创建临时文件夹
 * @author WF
 *
 */

@Controller
@RequestMapping("/ueditor")
public class UEditorController {

	@Resource
	IFileUploadService fuService;
	@Autowired
	stringOperating strOpe;
	@Autowired
	fileOperating fileOpe;
	
	@RequestMapping("/save")
	public void save(HttpServletRequest request,HttpServletResponse response){
		try {
			String msg = null;
			//获取项目的根路径
			String requestPath = request.getSession().getServletContext().getRealPath("/");
			//System.out.println("requestPath:"+requestPath);
			//接收前端数据
			FileUpload fileUpload = new FileUpload();
			String strHtml = request.getParameter("detail");
			String strTitle = request.getParameter("title");
			//System.out.println("strHtml:"+strHtml);
			//System.out.println("strTitle:"+strTitle);
			//获取数据内全部img标签的src地址
			List<String> imageSrcList = strOpe.getImageSrc(strHtml);
			if(imageSrcList != null && imageSrcList.size() != 0){
				//创建目标文件夹
				String newSrcPath = "E:/image/fileupload/img/";
				String folderPath = fileOpe.getDate();
				String filePath = newSrcPath + folderPath;
				File fi = new File(filePath);
				if(!fi.exists()) fi.mkdirs();//文件不存在创建中间文件夹
				for (String src : imageSrcList) {
					String[] srcSpl = src.split("/");
					/*for (String string : srcSpl) {
						System.out.println(string);
					}*/
					String oldSrcPath = null;
					if("http:".equals(srcSpl[0])){
						//网络图片											
						oldSrcPath = src;
						if("api.map.baidu.com".equals(srcSpl[2])){
							//给地图图片命名
							String mapsrc = srcSpl[srcSpl.length-1];
							String[] staticimage = mapsrc.split("&");
							String[] centers = staticimage[0].split("=");
							String[] zooms = staticimage[1].split("=");
							String name = centers[1]+"."+zooms[1]+".jpg";
							newSrcPath = newSrcPath + folderPath + "/" + name;
						}else{
							newSrcPath = newSrcPath + folderPath + "/" + srcSpl[srcSpl.length-1];
						}						
						try {
							int i = fileOpe.uploadWebImage(oldSrcPath, newSrcPath);
							if(i == 1){
								strHtml = strHtml.replace(src, newSrcPath.substring(2));
							}else{
								msg = "error0";
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							msg = "error1";
						}
					}else{
						//本地图片&&UEditor编辑器图片
						oldSrcPath = requestPath + srcSpl[2] + "\\" + srcSpl[3] + "\\" + srcSpl[4] + "\\" + srcSpl[5] + "\\" + srcSpl[6];					
						newSrcPath = filePath + "/" + srcSpl[6];
						//System.out.println("newSrcPath1:"+newSrcPath);
						//将oldSrcPath写入newSrcPath
						try {
							//将临时文件夹中发布的文件转存入固定文件夹
							int i = fileOpe.copyImage(oldSrcPath, newSrcPath);
							if(i == 1){
								strHtml = strHtml.replace(src, newSrcPath.substring(2));
								//System.out.println("strHtml:"+strHtml);
							}else {
								msg = "error0";
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							msg = "error1";
						}	
					}
				}
			}
			//删除非当天的临时文件夹
			String path = requestPath+"image\\fileupload\\img\\";
			fileOpe.delBeforedayFolder(path);
			//将数据存入数据库
			if("error0".equals(msg)){
				msg = "{\"msg\":\"文件不存在！\"}";
			}else if ("error1".equals(msg)) {
				msg = "{\"msg\":\"图片写入失败！\"}";
			}else if (msg == null) {
				fileUpload.setTitle(strTitle);
				fileUpload.setDetail(strHtml);
				int flag = fuService.save(fileUpload);
				if (flag == 1) {
					msg = "{\"msg\":\"发布成功！\"}";
				} else {
					msg = "{\"msg\":\"ERROR:信息发布失败！  错误原因：上传数据库失败\"}";
				}
			}
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/query")
	public void query(HttpServletRequest request,HttpServletResponse response){
		try {
			String msg = null;
			try {
				FileUpload fuList = fuService.queryByMaxId();
				if (fuList != null) {
					String detail = fuList.getDetail();
					String title = fuList.getTitle();
					//需将数据中的双引号添加转义符才能以json格式传递到前端显示
					if(detail != null && !detail.isEmpty()){
						detail = detail.replaceAll("\"", "\\\\\"");
					}
					msg = "{\"msg\":\""+detail+"\",\"title\":\""+title+"\",\"value\":1}";
				}else {
					msg = "{\"msg\":\"查询结果为空\"}";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msg = "{\"msg\":\"获取失败！\",\"value\":0}";
			}
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/edit")
	public static String ueditor(){
		return "ueditor";
	}
	
	/*@RequestMapping("/myjsp")
	public static String myjsp(){
		return "MyJsp";
	}*/
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
