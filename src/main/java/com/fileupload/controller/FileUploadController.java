package com.fileupload.controller;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sun.misc.BASE64Decoder;

import com.fileupload.model.FileUpload;
import com.fileupload.service.IFileUploadService;

@Controller
@RequestMapping("/file")
public class FileUploadController {

	@Resource
	private IFileUploadService fuService;
	
	/**
	 * 将前端返回的编辑器数据存储进数据库并返回提示信息
	 * @param reques
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/save")
	public void save(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String msg = null;		
		try {
			FileUpload fileUpload = new FileUpload();
			String strHtml = request.getParameter("detail");
			String strTitle = request.getParameter("title");
			//去除前端编辑时不合法的换行符
			//System.out.println("strHtml1:"+strHtml);
			strHtml = strHtml.replaceAll("\n", " ");
			//System.out.println("strHtml3:"+strHtml);
			if(strTitle == null || strTitle.isEmpty()) strTitle = " ";
			//判断，当有图片时，才调用imgUpload方法
			String str = ";base64,";
			int index = this.findStrIndexOfCount(str, strHtml);
			if(index != -1){
				for (int i = 0; i < index; i++) {
					try {
						String detail = this.imgUpload(strHtml,strTitle);
						strHtml = detail;
					} catch (IOException e) {
						// TODO: handle exception
						strHtml = "IOException";
						break;
					}
				}				
			}
			if (!"IOException".equals(strHtml)) {
				fileUpload.setTitle(strTitle);
				fileUpload.setDetail(strHtml);
				int flag = fuService.save(fileUpload);
				if (flag == 1) {
					msg = "{\"msg\":\"发布成功！\"}";
				} else {
					msg = "{\"msg\":\"ERROR:信息发布失败！  错误原因：上传数据库失败\"}";
				}					
			}else if("IOException".equals(strHtml)){
				msg = "{\"msg\":\"ERROR:图片上传失败！  错误原因：图片写入失败\"}";
			}
		} catch (Exception e) {
			// TODO: handle exception
			msg = "{\"msg\":\"ERROR:信息发布失败！  错误原因：数据上传失败\"}";
		} 
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(msg);		
	}
	
	/**
	 * 查询最后一次发布的一条数据
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/query")
	public void query(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String msg = null;
		try {
			FileUpload fuList = fuService.queryByMaxId();
			if(fuList != null) {
				String detail = fuList.getDetail();
				String title = fuList.getTitle();
				//System.out.println("detail:"+detail);
				//需将数据中的双引号添加转义符才能以json格式传递到前端显示
				detail = detail.replaceAll("\"", "\\\\\"");
				//detail为string类型，需转化为json格式传回页面
				msg = "{\"msg\":\""+detail+"\",\"title\":\""+title+"\",\"value\":1}";
				//System.out.println("msg: "+msg);
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
	}
	
	/**
	 * 图片上传
	 * 截取出base64编码格式的src
	 * 将src数据写入新的文件中，并返回文件路径替换src的内容
	 * @param strHtml
	 * @return
	 * @throws IOException 
	 */
	public String imgUpload(String strHtml,String strTitle) throws IOException{
		//从strHtml中截取第一个base64编码的字符串
		int start = intercept(strHtml, "<img src=\"data:", 0);
		int end = intercept(strHtml, "\"", start+15);
		if(start == -1 || end == -1) return strHtml;
		String imgSrc = strHtml.substring(start+15, end);
		if("".equals(imgSrc) || imgSrc.isEmpty()) return strHtml;
		//System.out.println("imgSrc:"+imgSrc);
		//获取src中图片的后缀和base64编码的数据
		String[] src = imgSrc.split(";");
		if(src.length != 2) return strHtml;
		String[] Type = src[0].split("/");
		if(Type.length != 2) return strHtml;
		String imgType = Type[1];
		String base64Str = src[1].substring(7);
		//System.out.println("imgType:"+imgType+",base64Str:"+base64Str);	
		/**********************
		 * 测试字符串是否截取成功
		 **********************/
		//目标路径
		String Path = "E:\\\\image\\\\fileupload\\\\";
		String FileName = strTitle + "_" + System.currentTimeMillis() + "." + imgType;
		String file = Path + FileName;
		//File f = new File(file);
		//if(!f.exists()) f.mkdirs();
		//System.out.println("realPath:"+file);
		//将base64Str写入realFilePath中
		byte[] bytes = new BASE64Decoder().decodeBuffer(base64Str);
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		byte[] buffer = new byte[1024];
        FileOutputStream out = new FileOutputStream(file);
        @SuppressWarnings("unused")
		int bytesum = 0;
        int byteread = 0;
        while ((byteread = in.read(buffer)) != -1) {
            bytesum += byteread;
            out.write(buffer, 0, byteread); //文件写操作
        }
        if(null!=out){
        	out.flush();
            out.close();
        }
        //将file放入src返回数据
        //截取第一个src=""之前的数据
        String detailStart = strHtml.substring(0,start);
        //截取第一个src=""之后的数据
        String detailEnd = strHtml.substring(end);
        String detailPath = file.substring(2);
        //System.out.println("detailStart:"+detailStart+",detailEnd:"+detailEnd);
        String detail = detailStart + "<img src=\"" + detailPath + detailEnd;
        //System.out.println(detail);
        return detail;
	}
	
	/**
	 * 从索引开始位置获取字符串中子字符串的第一个索引
	 * @param strHtml 字符串
	 * @param childStr 子字符串
	 * @param startIndex 索引开始位置
	 * @return
	 */
	public int intercept(String strHtml,String childStr,int startIndex){
		int index = strHtml.indexOf(childStr, startIndex);
		return index;
	}
	
	/**
	 * 获取子字符串出现的次数
	 * @param selectStr 子字符串
	 * @param tragetStr 目标字符串
	 * @return
	 */
	public int findStrIndexOfCount(String selectStr,String tragetStr){
		int selLen = selectStr.length();
		int traLen = tragetStr.length();
		if(selLen > 0 && selLen < traLen){
			return (traLen - tragetStr.replaceAll(selectStr, "").length())/selLen;
		}
		return -1;
	}
	
	@RequestMapping("/text")
	public String text(){
		return "text";
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
}
