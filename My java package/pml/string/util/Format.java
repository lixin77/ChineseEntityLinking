package pml.string.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
	  * contert \0xe3\0xe4 format hex string into stndard format string(i.e UTF-8)
	  * @param str
	  * @param charset
	  * 		The destinated string format
	  * @return
	  * 		A string with given format
	  * @throws UnsupportedEncodingException
	  */
	 public static String Hex2Str(String str, String charset) throws UnsupportedEncodingException {
		 String strArr[] = str.split("\\\\"); // 分割拿到形如 xE9 的16进制数据
		 byte[] byteArr = new byte[strArr.length - 1];
		 for (int i = 1; i < strArr.length; i++) {
			 Integer hexInt = Integer.decode("0" + strArr[i]);
			 byteArr[i - 1] = hexInt.byteValue();
		 }

		 return new String(byteArr, charset);
	 }
	 
	 

	 public static void main(String args[])
	 {	 
		 Integer test = Integer.decode("0x12");
		 System.out.println(test);
		 test = 300;
		 System.out.println(test.byteValue());
	 }
}
