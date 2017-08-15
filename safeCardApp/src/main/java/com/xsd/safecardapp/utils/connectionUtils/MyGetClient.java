package com.xsd.safecardapp.utils.connectionUtils;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用：HttpClient发送get请求
 */
public class MyGetClient {
	
	/**
     * 发送get请求 
     * @param url 地址
     * @param params 参数
     * @return 响应字符串
     * 
     * //先将参数放入List，再对参数进行URL编码  
	 			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();  
				params.add(new BasicNameValuePair("CD", ORDER_STR));  
				params.add(new BasicNameValuePair("ID", deviceIMEI)); 
     */
    public static String sendGet(String url,List<BasicNameValuePair> params) throws IOException {
    	
        String context = null;
        
        HttpClient client = MyHttpClient.getHttpClient();
        
        //对参数编码  
      	String param = URLEncodedUtils.format(params, "UTF-8"); 
        
        //TargetUrl             
		String targetUrl = url+"?"+param;  
		
		System.out.println("WWWWWW"+targetUrl);
       
        GetMethod method = new GetMethod(targetUrl);

        // 可以更改编码格式
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        try {
            client.executeMethod(method);

            //打印服务器返回的状态
            System.out.println(method.getStatusLine());

            //打印返回的信息
            context = method.getResponseBodyAsString();
            
        } catch (HttpException e) {
            e.printStackTrace();
        }
        finally{
            // 释放连接，放回连接池
            method.releaseConnection();

            // 关闭连接
//			((SimpleHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
        }
        return context;
	}  
}
