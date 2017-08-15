package com.xsd.safecardapp.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.SafeAreaLVAdapter;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.fragment.MeFragment;
import com.xsd.safecardapp.javabean.SafeAreaInfoJson;
import com.xsd.safecardapp.javabean.SafeAreaInfoJson.SafeAreaInfoResult;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author OnlyWay
 *
 *         功能：安全区域显示
 *
 *         时间 2015年8月31日
 */
public class SafeAreaActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {

	private String jsonString;
	private Message msg;
	private SafeAreaInfoJson mJson;
	private List<SafeAreaInfoResult> infoResult;
	private SafeAreaLVAdapter adapter;
	
	private MyLoadingDialog dialog;

	private ListView lvSafeArea;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if(dialog!=null && dialog.isShowing())
		        dialog.dismiss();
			
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:

				adapter = new SafeAreaLVAdapter(SafeAreaActivity.this,
						infoResult);

				lvSafeArea.setAdapter(adapter);
				break;

			case Consts.MESSAGE_FAIL:
				Toast.makeText(SafeAreaActivity.this, "服务器出错啦", 0).show();
				break;

			case Consts.MESSAGE_EMPTY:

				break;
			case 10086:
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "删除成功",
						Toast.LENGTH_SHORT).show();
				break;
			case 10010:
				Toast.makeText(getApplicationContext(), "删除失败",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safe_area);
		lvSafeArea = (ListView) findViewById(R.id.lv_safe_area);
		lvSafeArea.setOnItemClickListener(this);
		lvSafeArea.setOnItemLongClickListener(this);
		requestSafeAreaData(MainTabActivity.getmResult()
				.get(HomePageFragment.oldPosition).getImei());
	}

	/**
	 * 请求安全区域信息
	 */
	private void requestSafeAreaData(final String imei) {
		
		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "DR"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("DR")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("fsdfdfhre" + jsonString);

						Gson gson = new Gson();
						mJson = gson.fromJson(jsonString,
								SafeAreaInfoJson.class);
						infoResult = mJson.getResult();

						if (infoResult != null && infoResult.size() > 0) {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						e.printStackTrace();
						msg = Message.obtain();
						msg.what = Consts.MESSAGE_FAIL;
						handler.sendMessage(msg);
						
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

	/**
	 * 添加键按钮触发方法
	 * 
	 * @param v
	 */
	public void addSafeArea(View v) {
		Intent intent = new Intent(this, AddSafeAreaActivity.class);
		
		startActivityForResult(intent, Consts.SAFEAREA_TO_ADDSAFEAREA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Consts.SAFEAREA_TO_ADDSAFEAREA) {
			Bundle bundle = data.getBundleExtra(Consts.INTENT_DATA);
			if (bundle != null) {
				SafeAreaInfoResult returnResult = (SafeAreaInfoResult) bundle
						.getSerializable("safeareainfo");
				if (infoResult == null) {
					infoResult = new ArrayList<SafeAreaInfoJson.SafeAreaInfoResult>();
					adapter = new SafeAreaLVAdapter(this, infoResult);
				}
				infoResult.add(returnResult);
				System.out.println("dasdasdaswf"+infoResult.size());
				lvSafeArea.setAdapter(adapter);
				//adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getApplicationContext(), "bundleweikong",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 返回键按钮触发方法
	 * 
	 * @param v
	 */
	public void finishMyself(View v) {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, EditSafeAreaActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("safeareainfo", infoResult.get(position));
		intent.putExtra(Consts.INTENT_DATA, bundle);
		startActivityForResult(intent, Consts.SAFEAREA_TO_EDITSAFEAREA);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		createDialog(position);
		return true;
	}

	private void createDialog(final int position) {
		AlertDialog.Builder builder = new Builder(SafeAreaActivity.this,R.style.alert_dialog);
		builder.setMessage("安全区域删除后，将不再有效。您是否确定要删除该安全区域？");
		builder.setTitle("提示");
		builder.setPositiveButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setNegativeButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// dialog.dismiss();
				deleteSafeAreaData(
						MainTabActivity.getmResult()
								.get(HomePageFragment.oldPosition).getImei(),
						position);
			}

		});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	/**
	 * 删除安全区域
	 */
	private void deleteSafeAreaData(final String imei, final int position) {
		
		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "DD"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("RG", infoResult.get(
							position).getRegionid()
							+ ""));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("DD")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						JSONObject obj = new JSONObject(jsonString);

						String code = obj.getString("code");

						if ("0".equals(code)) {
							infoResult.remove(position);
							msg = Message.obtain();
							msg.what = 10086;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = 10010;
							handler.sendMessage(msg);
						}

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
}
