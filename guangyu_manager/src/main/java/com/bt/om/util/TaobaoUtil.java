package com.bt.om.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaobaoUtil {
	/**
	 * 
	 * @param conetnt
	 *            搜索的字符串
	 * @param tklSymbolsStr
	 *            淘口令符号“€;￥;《”
	 * @return
	 */
	public static boolean keyParser(String conetnt, String tklSymbolsStr) {
		if (conetnt.contains("jd.com") || conetnt.contains("taobao.com") || conetnt.contains("tmall.com")) {
			return true;
		} else {
			String[] tklSymbols = tklSymbolsStr.split(";");
			for (String symbol : tklSymbols) {
				String reg = symbol + ".*" + symbol;
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(conetnt);
				if (matcher.find()) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 
	 * @param conetnt
	 * @param tklSymbolsStr
	 * @return
	 */
	public static boolean ifTkl(String conetnt, String tklSymbolsStr) {
		String[] tklSymbols = tklSymbolsStr.split(";");
		for (String symbol : tklSymbols) {
			String reg = symbol + ".*" + symbol;
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(conetnt);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
}
