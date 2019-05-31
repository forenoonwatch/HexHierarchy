package strat.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	public static final Pattern QUOTE_PATTERN = Pattern.compile("\"(.*?)\"");
	
	public static int longestCommonSubstring(String s, String t) {
		if (t.length() > s.length()) {
			String temp = s;
			s = t;
			t = temp;
		}
		
		int[] topRow = new int[s.length()];
		int[] bottomRow = new int[s.length()];
		
		int z = 0;
		
		for (int i = 0; i < s.length(); ++i) {
			for (int j = 0; j < t.length(); ++j) {
				if (s.charAt(i) == t.charAt(j)) {
					if (i == 0 || j == 0) {
						bottomRow[j] = 1;
					}
					else {
						bottomRow[j] = topRow[j - 1] + 1;
					}
					
					if (bottomRow[j] > z) {
						z = bottomRow[j];
					}
				}
				else {
					bottomRow[j] = 0;
				}
			}
			
			System.arraycopy(bottomRow, 0, topRow, 0, bottomRow.length);
		}
		
		return z;
	}
	
	public static <T> T findBestMatch(T[] data, String match) {
		int maxLen = 0;
		double maxPer = 0;
		int maxI = 0;
		
		for (int i = 0; i < data.length; ++i) {
			String str = data[i].toString();
			int len = longestCommonSubstring(str, match);
			double per = (double)len / (double)str.length();
			
			if (len > maxLen) {
				maxLen = len;
				maxPer = per;
				maxI = i;
			}
			else if (len == maxLen) {
				if (per <= 1.0 && per > maxPer) {
					maxPer = per;
					maxI = i;
				}
			}
		}
		
		return data[maxI];
	}
	
	public static <T> T findBestMatch(Collection<T> data, String match) {
		int maxLen = 0;
		double maxPer = 0;
		T max = null;
		
		for (T t : data) {
			String str = t.toString();
			int len = longestCommonSubstring(str, match);
			double per = (double)len / (double)str.length();
			
			if (len > maxLen) {
				maxLen = len;
				maxPer = per;
				max = t;
			}
			else if (len == maxLen) {
				if (per <= 1.0 && per > maxPer) {
					maxPer = per;
					max = t;
				}
			}
		}
		
		return max;
	}
	
	public static String findTokenInString(String token, String lowerMessage, String rawMessage) {
		for (int i = 0; i < rawMessage.length() - token.length(); ++i) {
			if (lowerMessage.startsWith(token)) {
				return rawMessage.substring(i, i + token.length());
			}
		}
		
		return null;
	}
	
	public static String getQuotedSubstring(String rawMessage) {
		Matcher m = QUOTE_PATTERN.matcher(rawMessage);
		
		if (m.find()) {
			return m.group(1);
		}
		
		return null;
	}
}
