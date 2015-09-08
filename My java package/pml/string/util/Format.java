package pml.string.util;

import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import msra.nlp.kb.KnowledgeBaseException;

/**
 * This class deals with string format conversiong.
 * Hex2Unicode : 
 * 
 * Hex2UTF8 :
 *  
 * @author wangwenye
 *
 */
public class Format 
{
	
	 /**
	  * convet UTF-8 format string into hex format(i.e \0xe4\0xe3) string
	  * @param str
	  * 		The UTF-8 format string
	  * @return
	  * 		A hex format string
	  * */
	 public static String toHex(String string) throws UnsupportedEncodingException
	 {
		 return toHex(string, "utf-8");
	 }
	  
	 /**
	  * convet given format string into hex format(i.e \0xe4\0xe3) string
	  * @param str
	  * 		The given format string
	  * @param charset
	  * 		The format of the input string(default is UTF-8)
	  * @return
	  * 		A hex format string
	  * @throws UnsupportedEncodingException
	  */
	 public static String toHex(String str,String charset) throws UnsupportedEncodingException 
	 {
	        String hexRaw = String.format("%x", new BigInteger(1, str.getBytes(charset)));
	        char[] hexRawArr = hexRaw.toCharArray();
	        StringBuilder hexFmtStr = new StringBuilder();
	        final String SEP = "\\x";
	        for (int i = 0; i < hexRawArr.length; i++)
	        {
	            hexFmtStr.append(SEP).append(hexRawArr[i]).append(hexRawArr[++i]);
	        }
	        return hexFmtStr.toString();
	 }
	 
	 /**
	  * contert \0xe3\0xe4 format hex string into UTF-8 format string
	  * @param str
	  * 		A hex string
	  * @return
	  * @throws UnsupportedEncodingException
	  */
	 public static String Hex2Str(String str) throws UnsupportedEncodingException
	 {
		 return Hex2Str(str,"utf-8");
	 }
	 
	 /**
	  * convert \0xe3\0xe4 format hex string into stndard format string(i.e UTF-8)
	  * @param str
	  * @param charset
	  * 		The destinated string format
	  * @return
	  * 		A string with given format
	  * @throws UnsupportedEncodingException
	  */
	 public static String Hex2Str(String str, String charset) throws UnsupportedEncodingException {
		 String strArr[] = str.split("\\\\"); // åˆ†å‰²æ‹¿åˆ°å½¢å¦‚ xE9 çš„16è¿›åˆ¶æ•°æ�®
		 byte[] byteArr = new byte[strArr.length - 1];
		 for (int i = 1; i < strArr.length; i++) {
			 Integer hexInt = Integer.decode("0" + strArr[i]);
			 byteArr[i - 1] = hexInt.byteValue();
		 }

		 return new String(byteArr, charset);
	 }
	 
	 
	 public static String UnicodeEscapedSerial2Str(String str) throws UnsupportedEncodingException
	 {
			 return UnicodeEscaped2UTF8.convertUnicodeEscape(str);
	 }
	 
	
	 /**
	 * translate \u1003 format unicode string to uft8 format string
	 * @param unicode
	 * @return
	 */
	public static String Unicode2UTF8(String unicodeStr)
	{
		if(unicodeStr==null)
		{
			return null;
		}
		byte[] utf8;
		try 
		{
			utf8 = unicodeStr.getBytes("UTF-8");
			String string = new String(utf8, "UTF-8");
			return string;
		} 
		catch (UnsupportedEncodingException e) 
		{
			throw new KnowledgeBaseException(e.getCause());
		}
	}
	
	
	/**
	 * transform traditional chinese into simple chinese
	 * @param args
	 */
	 public static String Zhf2Zhj(String input)
	 {
		return ZHConverter.convert(input,1);
	 }
	 
	 public static String Zhj2Zhf(String input)
	 {
		 return ZHConverter.convert(input,0);
	 }

	 public static void main(String args[]) throws UnsupportedEncodingException
	 {	
		 String string = "\\u674E\\u5BDF\\u00B7\\u54C8\\u91CC\\u65AF";
		 //Character.UnicodeBlock.
		 
		System.out.println(UnicodeEscaped2UTF8.convertUnicodeEscape(string));
//		 String string= "畢業並取得圖形藝術學位後";
//		 System.out.println(Format.Zhj2Zhf(string));
//		 Integer test = Integer.decode("0x12");
//		 System.out.println(test);
//		 test = 300;
//		 System.out.println(test.byteValue());
		 
	 }
}
