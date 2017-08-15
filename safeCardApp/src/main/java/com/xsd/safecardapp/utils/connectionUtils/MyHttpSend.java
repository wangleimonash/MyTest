package com.xsd.safecardapp.utils.connectionUtils;

import android.os.StrictMode;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

public class MyHttpSend {

	//HttpClient的连接类型
    public final static String TYPE_POST = "post";
    public final static String TYPE_GET = "get";

    /**
     * Http请求发送
     * @param type 选择发送的类型 HttpSend.TYPE_POST  HttpSend.TYPE_GET
     * @param url 发送的地址
     * @return 响应的字符串
     */
    public static String httpSend(String type, String url,List<BasicNameValuePair> params) throws IOException {
              
        String data = null;
        if (type.equals(MyHttpSend.TYPE_GET)){ // Get请求发送
            data = MyGetClient.sendGet(url, params);
        } else if (type.equals(MyHttpSend.TYPE_POST)){ // Post请求发送
            data = MyPostClient.sendPost(url, params);
        }

        return data;
    }

    public static String postJson(String path, String json) {
        networkDetect();
        String result = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 6 * 1000);
            client.getParams().setParameter("http.protocol.content-charset",
                    HTTP.UTF_8);
            HttpPost post = new HttpPost(path);
            post.addHeader("charset", HTTP.UTF_8);
//			post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//			post.setHeader("Content-Type", "text/plain; charset=utf-8");
            StringEntity formEntity = null;
            formEntity = new StringEntity(json,HTTP.UTF_8);
            formEntity.setContentEncoding("UTF-8");
            formEntity.setContentType("application/json");
            //formEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(formEntity);
            System.out.println("HUASHUIBA entity"+formEntity.toString());
            HttpResponse response = client.execute(post);
            Log.d("postMethod", "postMethod end");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                result = inStream2String(is);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("e", "======UnsupportedEncodingException======"+e.getMessage());
        } catch (ClientProtocolException e) {
            Log.e("e",  "======ClientProtocolException======"+e.getMessage());
        } catch (IOException e) {
            Log.e("e", "=========IOException==========="+e.getMessage());
        }
        return result;
    }

    public static void networkDetect() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork() // or
                        // .detectAll()
                .penaltyLog().build());
        // 设置虚拟机的策略
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                        // .detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }

    private static String inStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        baos.close();
        is.close();
        return new String(baos.toByteArray());
    }
}
