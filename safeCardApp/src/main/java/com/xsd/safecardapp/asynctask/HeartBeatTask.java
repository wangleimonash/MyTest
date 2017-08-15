package com.xsd.safecardapp.asynctask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.javabean.MessageBean;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用： 呵呵，非要加个心跳包
 */
public class HeartBeatTask {

	private Context mContext;

	public HeartBeatTask(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public List<MessageBean> getheartbeatData() {
		Map resultMap = new HashMap();
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("CD", "HB"));
		params.add(new BasicNameValuePair("ID",MainTabActivity.getmResult().get(0).getImei()));
		params.add(new BasicNameValuePair("AU", AUUtils.getAU("HB")));
		try {		
			//对参数编码  
	      	String param = URLEncodedUtils.format(params, "UTF-8"); 
//	        //TargetUrl             
//			String targetUrl = "http://sz.iok100.com/Html/Home.aspx"+"?"+param; 
//			System.out.println("HAHAH"+targetUrl);
			String url = Consts.URL_PATH_HEART;
			if(TextUtils.isEmpty(url)){
				url = "http://sz.iok100.com/Html/Home.aspx";
			}
			String jsonString = MyHttpSend.httpSend(
					MyHttpSend.TYPE_GET, url, params);
			
			
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}		
			JSONObject json = new JSONObject(jsonString);			
			JSONArray messageArr = json.getJSONArray("BD");
			List<MessageBean> homeWorkList = new ArrayList<MessageBean>();			
			for (int index = 0; index < messageArr.length(); index++) {
				JSONObject check = messageArr.getJSONObject(index);
				
				MessageBean mb = new MessageBean();
				JSONObject message = messageArr.getJSONObject(index);
				mb.setContent(message.getString("content"));
				mb.setTitle(message.getString("title"));
				mb.setType(message.getString("type"));				
				mb.setCreateDate(message.getString("date"));
				
				homeWorkList.add(mb);
			}
			
			
//			JSONObject absenceObject = json.getJSONArray("SO").getJSONObject(0);
//			resultMap.put("HOME_WORK", homeWorkList);
//			resultMap.put("ABSENCE",
//					"1".equals(JsonUtils.getString(absenceObject, "code")));
			return homeWorkList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
}
