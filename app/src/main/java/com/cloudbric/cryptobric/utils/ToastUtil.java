package com.cloudbric.cryptobric.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    /**
     * ToastUtil
	 ************************************************************************************************************************************************/
	private static Toast toast;
	
	/**
	 * Short Toast Message
	 ************************************************************************************************************************************************/
	public static void showToastAsShort(Context context, String message) {
		clearToast();
		toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * Long Toast Message
	 ************************************************************************************************************************************************/
	public static void showToastAsLong(Context context, String message) {
		clearToast();
		toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.show();
	}
	
	/**
	 * Toast 객체 초기화
	 ************************************************************************************************************************************************/
	private static void clearToast() {
		if(toast != null) {
			toast.cancel();
			toast = null;
		}
	}
}
