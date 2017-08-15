package com.xsd.safecardapp.activity;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.AlarmAdapter;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.javabean.AlarmJson;
import com.xsd.safecardapp.javabean.AlarmJson.AlarmResult;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.RequestDataUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 报警信息界面
 * @author OnlyWay
 *
 */
public class AlarmRecordActivity extends Activity implements OnItemClickListener {

	private String jsonString;
	private Message msg;
	private AlarmJson mJson;
	private List<AlarmResult> mResult;
	
	private ListView lvAlarmRecord;
	private AlarmAdapter adapter;
	private MyLoadingDialog dialog;
	private TextView tvNoInfo;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:

				adapter = new AlarmAdapter(AlarmRecordActivity.this,
						mResult);

				lvAlarmRecord.setAdapter(adapter);
				break;

			case Consts.MESSAGE_FAIL:

				break;

			case Consts.MESSAGE_EMPTY:
				Toast.makeText(getApplicationContext(), "数据为空",
						Toast.LENGTH_SHORT).show();
				tvNoInfo.setVisibility(View.VISIBLE);
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
		setContentView(R.layout.activity_alarm_record);
		tvNoInfo = (TextView) findViewById(R.id.tv_no_info);
		lvAlarmRecord = (ListView) findViewById(R.id.lv_alarm_record);
		lvAlarmRecord.setOnItemClickListener(this);

		requestAlarmRecord(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei()
				,RequestDataUtils.getStartTime(7),RequestDataUtils.getEndTime());
	}

	/**
	 * 请求报警日志信息
	 */
	private void requestAlarmRecord(final String imei, final String starTime,
			final String endTime) {

		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "AL"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("ST", starTime));
					params.add(new BasicNameValuePair("ET", endTime));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("AL")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("fsdfdfhre" + jsonString);

						Gson gson = new Gson();
						mJson = gson.fromJson(jsonString,
								AlarmJson.class);
						mResult = mJson.getResult();

						if (mResult != null && mResult.size() > 0) {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (IOException e) {
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

	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}

	public void finishMyself(View v) {
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i = new Intent(this,AlarmDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("content", mResult.get(position).getDesc());
		i.putExtra("DOTA", bundle);
		startActivity(i);
		
	}
}
