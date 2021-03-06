package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 白名单编辑
 * @author OnlyWay
 *
 */
public class WhiteListEditActivity extends Activity{
	
	private EditText etName;
	private EditText etPhone;
	
	private String jsonString;
	private Message msg;
	private String no;
	private String nm;
	private String pc;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if(dialog!=null && dialog.isShowing())
		        dialog.dismiss();
			
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				Toast.makeText(WhiteListEditActivity.this, "修改成功",
						Toast.LENGTH_SHORT).show();
//				Bundle bundle = new Bundle();
//				SafeAreaInfoResult result = new SafeAreaInfoResult();
//				result.setName(areaName);
//				result.setRegionid(regionid);
//				Regionpoint point = new Regionpoint();
//				point.setLat(currentLatLng.latitude);
//				point.setLng(currentLatLng.longitude);
//				point.setPoint(radius);
//				result.setRegionpoint(point);
//				bundle.putSerializable("safeareainfo", result);
//				dataIntent.putExtra(Consts.INTENT_DATA, bundle);
//				setResult(Consts.SAFEAREA_TO_ADDSAFEAREA, dataIntent);
				finish();
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(WhiteListEditActivity.this, "修改失败",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
		};
	
	
	private MyLoadingDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_white_edit);
		etName = (EditText) findViewById(R.id.et_name);
		etPhone = (EditText) findViewById(R.id.et_phone);
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra(Consts.INTENT_DATA);
		no = bundle.getString("NO");
		nm = bundle.getString("NM");
		pc = bundle.getString("PC");
		etName.setText(nm);
		etPhone.setText(pc);
	}
	
	public void commit(View v){
		if(TextUtils.isEmpty(etName.getText().toString().trim())||
				TextUtils.isEmpty(etPhone.getText().toString().trim())){
			Toast.makeText(this,"信息不能为空",Toast.LENGTH_SHORT).show();
			return;
		}
		addSafeAreaData(MainTabActivity.getmResult()
				.get(HomePageFragment.oldPosition).getImei(),
				etName.getText().toString().trim(),
				etPhone.getText().toString().trim(),no);
		
	}
	
	public void finishMyself(View v){
		finish();
	}
	
	
	
	private void addSafeAreaData(final String imei,final String name,final String phone,final String no) {

		showLoadingDialog();
		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "UPPD"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("NO", no));
					params.add(new BasicNameValuePair("NM", name));
					params.add(new BasicNameValuePair("PC", phone));					
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("UPPD")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						JSONObject obj = new JSONObject(jsonString);
						String code = obj.getString("code");

						if ("0".equals(code)) {						
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_FAIL;
							handler.sendMessage(msg);
						}

						// if (pathResult.size() > 0) {
						// msg = Message.obtain();
						// msg.what = Consts.MESSAGE_SUCCESS;
						// handler.sendMessage(msg);
						// } else {
						// msg = Message.obtain();
						// msg.what = Consts.MESSAGE_EMPTY;
						// handler.sendMessage(msg);
						// }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {
			msg = Message.obtain();
			msg.what = Consts.MESSAGE_FAIL;
			handler.sendMessage(msg);
		}

		}
	
	private void showLoadingDialog(){	
		dialog = MyLoadingDialog.getMyLoadingDialog(this,R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();				
		}

}
