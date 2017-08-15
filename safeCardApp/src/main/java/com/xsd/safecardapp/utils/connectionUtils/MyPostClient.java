package com.xsd.safecardapp.utils.connectionUtils;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.message.BasicNameValuePair;

public class MyPostClient {
	
	/**
     * Post发送 
     * @param url 地址
     * @param mapParams 参数
     * @return 响应值
     */
	public static String sendPost(String url, List<BasicNameValuePair> params) throws IOException {
    
		String context = null;
		
        HttpClient client = MyHttpClient.getHttpClient();
        
        // 使用 POST 方法 ，如果服务器需要通过 HTTPS 连接，
        // 那只需要将下面 URL 中的 http 换成 https
        PostMethod method = new PostMethod(url);

        // 可以更改编码格式
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        method.setRequestBody((NameValuePair [])params.toArray());

        try {
            client.executeMethod(method);

            //打印服务器返回的状态
            System.out.println("服务器返回的状态: " + method.getStatusLine());

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
