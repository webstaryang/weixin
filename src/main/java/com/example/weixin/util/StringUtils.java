package com.example.weixin.util;
/**
 * StringUtils工具类
 * 
 * @author zhaoyanan
 */
public class StringUtils {
	/**
	 * 判断不为NULL并且非Trim Blank
	 * @param str
	 * @return
	 */
	public static boolean isNotNullAndTrimBlank(String str) {
		if (str == null || str.trim().length() <= 0)
			return false;
		return true;
	}

	/**
	 * 判断不为NULL并且Blank
	 * @param str
	 * @return
	 */
	public static boolean isNotNullAndBlank(String str) {
		if (str == null || str.length() <= 0)
			return false;
		return true;
	}

	/**
	 * 判断是否为Blank
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (null == str || str.trim().length() <= 0)
			return true;
		return false;
	}

	/**
	 * 判断是否Not blank
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	
	/**
	 * 判断是否Not Null
	 * @param str
	 * @return
	 */
	public static boolean isNotNull(String str) {
		return str != null;
	}

	/**
	 * 判断是否为Null 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrBlank(String str) {
		if (str == null || str.trim().length() <= 0)
			return true;
		return false;
	}
}