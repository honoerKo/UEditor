package com.fileupload.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * 字符串操作
 * @author WF
 *
 */
@Component
public class StringOperating {
	
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
	
	/** 
     * 从HTML源码中提取图片路径，最后以 List 返回，如果不包含任何图片，则返回一个 size=0　的List 
     * 不限制图片后缀,截取img标签中的src内容 
     * @param strHtml HTML源码 
     * @return <img>标签 src 属性指向的图片地址的List集合  
     */  
    public List<String> getImageSrc(String strHtml) {  
        List<String> imageSrcList = new ArrayList<String>();  
        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+)[^>]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(strHtml);  
        String quote = null;  
        String src = null;  
        while (m.find()) {  
            quote = m.group(1);  
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("\\s+")[0] : m.group(2);  
            imageSrcList.add(src);  
        }  
        return imageSrcList;  
    }
}
