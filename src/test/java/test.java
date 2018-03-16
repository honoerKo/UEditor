import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;


public class test {

	@Test
	public void test() {
		String strHtml = "<img style=\"height:1px;width:1px;\" src=\"/fileupload/img/1213234.vmmfd\" />";
		//<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.gif|\\.png|\\.jpeg|)\\b)[^>]*>
		//<img[\s]+src[\s]*=[\s]*((['"](?<src>[^'"]*)[\'"])|(?<src>[^\s]*))
		Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+)[^>]*", Pattern.CASE_INSENSITIVE);//不限制图片后缀 
        Matcher m = p.matcher(strHtml);  
        String quote = null;  
        String src = null;  
        while (m.find()) {  
            quote = m.group(1);  
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("\\s+")[0] : m.group(2); 
            System.out.println(src);
        }
	}

}
