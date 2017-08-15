package com.xsd.safecardapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 
 * 作者： OnlyWay
 *
 * 作用： 生成获取服务器信息的AU值
 */
public class AUUtils {
    	
	/**
	 * 
	 * @param orderStr  命令字  例如，位置信息：PQ
	 * @return
	 */
	public static String getAU(String orderStr){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String date =format.format(new Date());
		String password = date + orderStr + Consts.SENDER_CODE;
		String authentication = MD5Utils.MD5(password);
		return authentication.toUpperCase();
	}
}
