package pml.string.util;

import java.io.*;
/**
 * Reads file with unicode escaped characters and write them out to
 * stdout in UTF-8.
 */
public class UnicodeEscaped2UTF8 
{

	    
	    static enum ParseState {
		NORMAL,
		ESCAPE,
		UNICODE_ESCAPE
	    }

	    // convert unicode escapes back to char
	    public static String convertUnicodeEscape(String s) {
		char[] out = new char[s.length()];

		ParseState state = ParseState.NORMAL;
		int j = 0, k = 0, unicode = 0;
		char c = ' ';
		for (int i = 0; i < s.length(); i++) {
		    c = s.charAt(i);
		    if (state == ParseState.ESCAPE) {
			if (c == 'u') {
			    state = ParseState.UNICODE_ESCAPE;
			    unicode = 0;
			}
			else { // we don't care about other escapes
			    out[j++] = '\\';
			    out[j++] = c;
			    state = ParseState.NORMAL;
			}
		    }
		    else if (state == ParseState.UNICODE_ESCAPE) {
			if ((c >= '0') && (c <= '9')) {
			    unicode = (unicode << 4) + c - '0';
			}
			else if ((c >= 'a') && (c <= 'f')) {
			    unicode = (unicode << 4) + 10 + c - 'a';
			}
			else if ((c >= 'A') && (c <= 'F')) {
			    unicode = (unicode << 4) + 10 + c - 'A';
			}
			else {
			    throw new IllegalArgumentException("Malformed unicode escape");
			}
			k++;

			if (k == 4) {
			    out[j++] = (char) unicode;
			    k = 0;
			    state = ParseState.NORMAL;
			}
		    }
		    else if (c == '\\') {
			state = ParseState.ESCAPE;
		    }
		    else {
			out[j++] = c;
		    }
		}

		if (state == ParseState.ESCAPE) {
		    out[j++] = c;
		}

		return new String(out, 0, j);
	    }
	
	    public static void main(String[] args) throws Exception {
			if (args.length < 1) {
			    System.out.println("Usage: java UnicodeEscape2UTF8 <filename>");
			    return;
			}

			BufferedReader r = new BufferedReader(new FileReader(args[0]));
			String line = r.readLine();
			while (line != null) {
			    line = convertUnicodeEscape(line);
			    byte[] bytes = line.getBytes("UTF-8");
			    System.out.write(bytes, 0, bytes.length);
			    System.out.println();
			    line = r.readLine();
			}
			r.close();
		    }

}
