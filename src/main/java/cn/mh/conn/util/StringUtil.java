package cn.mh.conn.util;

public class StringUtil {

	/**
	 * 左对齐补空格
	 * @param str 原始字段
	 * @param length 总长
	 * @return 补足后字符串
	 */
	public static String leftAlign(String str, int length) {
		if(str == null) {
			return String.format("%-" + length + "s", "");
		} else if(str.length() < length) {
			return String.format("%-" + length + "s", str);
		} else {
			return str;
		}
	}
	
	/**
	 * 右对齐补空格
	 * @param str 原始字段
	 * @param length 总长
	 * @return 补足后字符串
	 */
	public static String rightAlign(String str, int length) {
		if(str == null) {
			return String.format("%" + length + "s", "");
		} else if(str.length() < length) {
			return String.format("%" + length + "s", str);
		} else {
			return str;
		}
	}
	
	/**
	 * 右对齐补0
	 * @param i 原数字
	 * @param length 总长
	 * @return 补足后字符串
	 */
	public static String rightAlignZero(int i, int length) {
		return String.format("%0" + length + "d", i);
	}
	
}
