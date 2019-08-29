package com.cloudbric.cryptobric.utils;

import android.util.Log;

/**
 * Log를 간편하게 사용하기 위한 Class
 * @author 유민호
 * @since 2018.04.17
 */
public class LogUtil {

	/**
	 * Timeout
	 ************************************************************************************************************************************************/
	private static final String SETTING_1 = "[";
	private static final String SETTING_2 = " > ";
	private static final String SETTING_3 = "()";
	private static final String SETTING_4 = " > #";
	private static final String SETTING_5 = "] ";

	/**
	 * verbose
	 */
	public static void v(String logString) {
		if(Constant.DEBUG_MODE) {
			Log.v(buildTagName(), logString);
		}
	}
	
	/**
	 * debug
	 */
	public static void d(String logString) {
		if(Constant.DEBUG_MODE) {
			Log.d(buildTagName(), logString);
		}
	}
	
	/**
	 * info
	 */
	public static void i(String logString) {
		if(Constant.DEBUG_MODE) {
			Log.i(buildTagName(), logString);
		}
	}
	
	/**
	 * warn
	 */
	public static void w(String logString) {
		if(Constant.DEBUG_MODE) {
			Log.w(buildTagName(), logString);
		}
	}
	
	/**
	 * error
	 */
	public static void e(String logString) {
		if(Constant.DEBUG_MODE) {
			Log.e(buildTagName(), logString);
		}
	}

	/**
	 * Log TAG String 생성
	 * 파일명, 메소드명, 라인번호 정보를 표시함.
     */
	private static String buildTagName() {
		StackTraceElement ste = new Throwable().getStackTrace()[2];

		return SETTING_1 +
				ste.getFileName() +
				SETTING_2 +
				ste.getMethodName() + SETTING_3 +
				SETTING_4 +
				ste.getLineNumber() +
				SETTING_5;
	}
}