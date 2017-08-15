package com.xsd.safecardapp.utils.connectionUtils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用： 得到网络连接的HttpClient
 */
public class MyHttpClient {
	
    private static HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
	private static HttpConnectionManagerParams params = httpConnectionManager.getParams();
	private static HttpClient httpClient = null;
		
	public static HttpClient getHttpClient(){       
		if (httpClient == null){
			params.setConnectionTimeout(5000); // 连接超时时间
		    params.setSoTimeout(15000); // 把等待客户发送数据的超时时间设为15秒		    
		    // 设置于对应请求的目标主机线程数最多为32条 very important!!
		    params.setDefaultMaxConnectionsPerHost(32); 		    
		    // 总的的线程数最大连接数为256条 very important!! 
		    params.setMaxTotalConnections(256); 		    
		    // 将连接管理器设置到HttpClient中
		    httpClient = new HttpClient(httpConnectionManager);
		}
		
		return httpClient;
	}

}
