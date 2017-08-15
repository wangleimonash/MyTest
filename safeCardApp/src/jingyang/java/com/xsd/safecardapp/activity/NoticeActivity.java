package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.AlarmAdapter;
import com.xsd.safecardapp.fragment.MeFragment;
import com.xsd.safecardapp.javabean.AlarmJson;
import com.xsd.safecardapp.javabean.AlarmJson.AlarmResult;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 设备省电模式
 * @author OnlyWay
 *
 */
public class NoticeActivity extends Activity implements OnClickListener {

	private SharedPreferences sp;
	private Editor editor;

	private RelativeLayout rlPowerSavingMode;
	private ImageView ivSacingMode;
	private TextView tvMode;
	private TextView tvDesc;

	private String jsonString;
	private Message msg;
	private AlarmJson mJson;
	private List<AlarmResult> mResult;

	private ListView lvAlarmRecord;
	private AlarmAdapter adapter;
	private MyLoadingDialog dialog;
	
	private String mCode;
	
	private boolean isClose;

	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:

				if("0".equals(mCode)){
					editor.putBoolean("isPowerSaving", !sp.getBoolean("isPowerSaving", false)).commit();
				}else{
					
				}
				break;

			case Consts.MESSAGE_FAIL:

				break;

			case Consts.MESSAGE_EMPTY:
				Toast.makeText(getApplicationContext(), "数据为空",
						Toast.LENGTH_SHORT).show();
				break;
			case 10086:

				break;
			case 10010:

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		editor = sp.edit();

		setContentView(R.layout.activity_notice);
		tvMode = (TextView) findViewById(R.id.tv_mode);
		ivSacingMode = (ImageView) findViewById(R.id.iv_power_saving_mode);
		rlPowerSavingMode = (RelativeLayout) findViewById(R.id.rl_power_saving_mode);
		rlPowerSavingMode.setOnClickListener(this);
		
		isClose = sp.getBoolean("isCloseNotice", false);

		if (sp.getBoolean("isCloseNotice", false)) {
			ivSacingMode.setImageResource(R.drawable.button_off);
		} else {
			
			ivSacingMode.setImageResource(R.drawable.button_on);
		}
	}

	public void back(View v) {
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.rl_power_saving_mode) {
			if (isClose) {
				ivSacingMode.setImageResource(R.drawable.button_off);
//				requestModeData(MainTabActivity.getmResult().get(MeFragment.oldPosition).getImei(),
//						"0000","2359","2","2");
//				finish();
			} else {
				ivSacingMode.setImageResource(R.drawable.button_on);
//				requestModeData(MainTabActivity.getmResult().get(MeFragment.oldPosition).getImei(),
//						"0000","2359","5","5");
//				finish();
			}
			isClose = !isClose;
		}
	}

	public void requestModeData(final String imei,final String startTime,final String endTime,final String lt,final String rp ) {

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "SL"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("ST", startTime));
					params.add(new BasicNameValuePair("ET", endTime));
					params.add(new BasicNameValuePair("LT", lt));
					params.add(new BasicNameValuePair("RP", rp));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("SL")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						

						JSONObject ojb = new JSONObject(jsonString);
						mCode = ojb.getString("code");
						
						System.out.println("fsdfdfhre" + mCode);

						if (!TextUtils.isEmpty(mCode)) {
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
