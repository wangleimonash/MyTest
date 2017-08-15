package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.CardInfoLVAdapter;
import com.xsd.safecardapp.javabean.LoginJson;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.MD5Utils;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 学生信息列表
 *
 */
public class CardInfoActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {

	private ListView lvCardInfo;
	private MyLoadingDialog dialog;
	private Message message;
	private String resultCode;
	
	private SharedPreferences sp;
	
	private CardInfoLVAdapter adapter;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				
				if("0".equals(resultCode)){
					Toast.makeText(CardInfoActivity.this, "成功", 0).show();
					setResult(10086);
					finish();
				}else{
					Toast.makeText(CardInfoActivity.this, "失败", 0).show();
					System.out.println("dasdsd"+resultCode);
				}				
				break;
			case Consts.MESSAGE_FAIL:

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_info);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		lvCardInfo = (ListView) findViewById(R.id.lv_card_info);
		adapter = new CardInfoLVAdapter(this);
		lvCardInfo.setAdapter(adapter);
		lvCardInfo.setOnItemClickListener(this);
		lvCardInfo.setOnItemLongClickListener(this);
	}
	
	public void finishSelf(View v){
		finish();
	}

	private void createDialog(final int position) {
		AlertDialog.Builder builder = new Builder(CardInfoActivity.this,R.style.alert_dialog);
		builder.setMessage("平安卡解绑后，将不再有效。您是否确定解绑平安卡？");
		builder.setTitle("解绑平安卡");
		builder.setPositiveButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setNegativeButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				requestDeleteDevice(position);
			}

		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.getCurrentFocus();
		dialog.show();

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//requestDeleteDevice(position);
		Intent intent = new Intent(this,BabyInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		intent.putExtra("DOTA", bundle);
		startActivityForResult(intent, 110);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		adapter.notifyDataSetChanged();
	}
	
	private void requestDeleteDevice(final int position) {

		if (NetUtils.isNetworkAvailable(this)) {

			showLoadingDialog();// 显示加载中对话框

			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "DEL"));
					params.add(new BasicNameValuePair("ID", MainTabActivity
							.getmResult().get(position).getImei()));
					params.add(new BasicNameValuePair("PC",sp.getString("username", "")));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("DEL")));

					try {
						
						//对参数编码  
				      	String param = URLEncodedUtils.format(params, "UTF-8"); 
				        
				        //TargetUrl             
						String targetUrl = Consts.URL_PATH+"?"+param; 
						
						System.out.println("fsafsdfdfsght"+targetUrl);

						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						System.out.println("jsonStringaaa" + jsonString);

						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");

						if (!TextUtils.isEmpty(resultCode)) {
							message = Message.obtain();
							message.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(message);
						} else {
							message = Message.obtain();
							message.what = Consts.MESSAGE_FAIL;
							handler.sendMessage(message);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {
			message = Message.obtain();
			message.what = Consts.MESSAGE_FAIL;
			handler.sendMessage(message);
		}

	}

	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		createDialog(position);
		return true;
	}
}
