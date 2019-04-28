package com.cw.demo.m1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	/**
	 * 函数说明：12位数字或字母组合
	 * @param s
	 * @return
	 */
	public static boolean isCheckPwd(String s) {
		Matcher m = Pattern.compile("^[0-9A-Fa-f]{12}$").matcher(s);
		return m.matches();
	}
	/**
	 * 函数说明：32位数字或字母组合
	 * @param s
	 * @return
	 */
	public static boolean isCheckWriteData(String s) {
		Matcher m = Pattern.compile("^[0-9A-Fa-f]{32}$").matcher(s);
		return m.matches();
	}
}
