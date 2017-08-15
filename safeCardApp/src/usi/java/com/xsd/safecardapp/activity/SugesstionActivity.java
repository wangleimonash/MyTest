package com.xsd.safecardapp.activity;



import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.LoginJson;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.SPUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 意见反馈
 * @author OnlyWay
 *
 */
public class SugesstionActivity extends Activity{

	private EditText etContent;
	private MyLoadingDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggesstion);
		etContent = (EditText) findViewById(R.id.tv_content);
	}
	
	public void commit(View v){
		final String text = etContent.getText().toString().trim();
		if(TextUtils.isEmpty(text)){
			Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show();
			return;
		}
		AsyncTask<String, Integer, String> execute = new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showLoadingDialog();
			}

			@Override
			protected String doInBackground(String... params) {
				String content = params[0];
				String jsons = SPUtils.getString(SugesstionActivity.this, "userinfosss");
				LoginJson.LoginResult loginResult = null;
				try{
					LoginJson loginjson = new Gson().fromJson(jsons,LoginJson.class);
					loginResult = loginjson.getResult().get(MainTabActivity.oldPosition);
				}catch (Exception e){

				}
				JsonContentData jsonContentData = new JsonContentData();
				jsonContentData.content = content;
				jsonContentData.userName =loginResult == null?SPUtils.getString(SugesstionActivity.this, "username"):loginResult.getUserName();
				jsonContentData.time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
				jsonContentData.title = "意见反馈";
				jsonContentData.phone =loginResult == null? jsonContentData.userName:loginResult.getPhoneNumber();
				jsonContentData.type = "4";
				Gson gson = new Gson();
				String json = gson.toJson(jsonContentData);
				StringBuilder stringBuilder = new StringBuilder(Consts.URL_PATH);
				stringBuilder.append("?CD=FB").append("&AU=" + AUUtils.getAU("FB"));
				String serversjons = MyHttpSend.postJson(stringBuilder.toString(), json);
				Log.d("WM", serversjons);
				return serversjons;
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				dialog.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(s);
					String code = jsonObject.optString("code");
					if ("0".equals(code)) {
						Toast.makeText(SugesstionActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
						finish();
					} else if ("12".equals(code)) {
						Toast.makeText(SugesstionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(SugesstionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Log.d("wm", e == null ? "" : e.getMessage());
				}
			}
		}.execute(text);
	}

	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}

	public class JsonContentData{
		public String userName;
		public String phone;
		public String title;
		public String type;
		public String content;
		public String time;
	}
}
