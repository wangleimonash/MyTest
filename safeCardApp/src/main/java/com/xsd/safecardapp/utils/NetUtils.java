package com.xsd.safecardapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用：网络连接工具类
 */
public class NetUtils {

	/**
	 * 判断是否有网络连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			if (cm.getActiveNetworkInfo().isAvailable()) {
				return true;
			}
		} catch (Exception e) {
		}
		
		return false;
	}
}
