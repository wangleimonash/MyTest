package com.xsd.safecardapp.activity;

/**
 * 亲情号码展示
 *
 */
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.FamilyLVAdapter;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.fragment.MeFragment;
import com.xsd.safecardapp.javabean.FamilyJson;
import com.xsd.safecardapp.javabean.FamilyJson.FamilyResult;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.DensityUtil;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FamilyActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private ListView lvFamily;
	private ImageButton ibBack;
	private RelativeLayout llContainer;
	private FamilyLVAdapter adapter;
	private Button btnChange;

	private Message message;
	private String resultCode;
	private FamilyResult mResult;

	private String key1;
	private String key2;
	private String key3;
	private String key4;

	private MyLoadingDialog dialog;

	private PopupWindow settingPopupWindow;
	private View mPopupView;
	private TextView tvPopupTitle;
	private EditText etPopupContent;
	private Button btnOk;
	private Button btnCancel;
	private int clickPositon = -1;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				adapter = new FamilyLVAdapter(FamilyActivity.this, mResult);
				lvFamily.setAdapter(adapter);
				key1 = mResult.getKey1();
				key2 = mResult.getKey2();
				key3 = mResult.getKey3();
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(FamilyActivity.this, "网络异常", Toast.LENGTH_SHORT)
						.show();
				break;
			case 10086:
				if ("0".equals(resultCode)) {
					Toast.makeText(FamilyActivity.this, "修改成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(FamilyActivity.this, "修改失败",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case 110:

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family);

		lvFamily = (ListView) findViewById(R.id.lv_family);
		lvFamily.setOnItemClickListener(this);

		ibBack = (ImageButton) findViewById(R.id.ib_back);
		ibBack.setOnClickListener(this);

		llContainer = (RelativeLayout) findViewById(R.id.ll_container);

		createPopupWindow();

		requestFamilyData(MainTabActivity.getmResult()
				.get(HomePageFragment.oldPosition).getImei());
	}

	public void changeInfo(View v) {
		requestSetFamilyData(MainTabActivity.getmResult()
				.get(HomePageFragment.oldPosition).getImei());
	}

	private void createPopupWindow() {
		mPopupView = View.inflate(this, R.layout.popup_setting, null);
		tvPopupTitle = (TextView) mPopupView
				.findViewById(R.id.tv_setting_title);
		tvPopupTitle.setText("请输入手机号：");
		etPopupContent = (EditText) mPopupView.findViewById(R.id.et_content);
		btnOk = (Button) mPopupView.findViewById(R.id.btn_ok);
		btnCancel = (Button) mPopupView.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				settingPopupWindow.dismiss();
			}
		});
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String number = etPopupContent.getText().toString();
				if ((number.length() == 11) || (TextUtils.isEmpty(number))) {

					switch (clickPositon) {
					case 0:
						key1 = number;
						mResult.setKey1(number);
						break;
					case 1:
						key2 = number;
						mResult.setKey2(number);
						break;
					case 2:
						key3 = number;
						mResult.setKey3(number);
						break;
					

					default:
						break;
					}
					settingPopupWindow.dismiss();
					adapter.notifyDataSetChanged();

					return;
				} else {
					Toast.makeText(FamilyActivity.this, "请输入正确手机号", 0).show();
				}

			}
		});

		settingPopupWindow = new PopupWindow(mPopupView, DensityUtil.dip2px(
				this, 270), DensityUtil.dip2px(this, 180), true);
		settingPopupWindow.setTouchable(true);
		settingPopupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
		settingPopupWindow.setOutsideTouchable(true);
	}

	private void showPopupWindow() {

		if (settingPopupWindow.isShowing()) {
			settingPopupWindow.dismiss();
		} else {
			// popupWindow.showAsDropDown(rlTop);
			settingPopupWindow
					.showAtLocation(llContainer, Gravity.CENTER, 0, 0);
		}

	}

	/**
	 * 请求家庭成员数据
	 */
	private void requestFamilyData(final String imei) {

		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {

			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "DK"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("DK")));

					try {
						// 对参数编码
						String param = URLEncodedUtils.format(params, "UTF-8");
						System.out.println("xxxdsadsxurl" + Consts.URL_PATH
								+ "?" + param);
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						System.out.println("jsonString" + jsonString);

						Gson gson = new Gson();
						FamilyJson mJson = gson.fromJson(jsonString,
								FamilyJson.class);
						mResult = mJson.getResult();
						
						if (mResult != null) {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.ib_back:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			Toast.makeText(this, "管理员号码不可修改", 0).show();
			return;
		}
		clickPositon = position;
		showPopupWindow();
		// requestSetFamilyData();
	}

	/**
	 * 修改成员数据
	 * 
	 * @param imei
	 */
	private void requestSetFamilyData(final String imei) {

		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {

			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "SK"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("K1", mResult.getKey1()));
					params.add(new BasicNameValuePair("K2", mResult.getKey2()));
					params.add(new BasicNameValuePair("K3", mResult.getKey3()));
					params.add(new BasicNameValuePair("K4", mResult.getKey4()));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("SK")));

					try {
						// 对参数编码
						String param = URLEncodedUtils.format(params, "UTF-8");
						System.out.println("xxxdsadsxurl" + Consts.URL_PATH
								+ "?" + param);
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						System.out.println("jsonString" + jsonString);

						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");
						System.out.println("hhsfdsdasd" + jsonString);

						if (!TextUtils.isEmpty(resultCode)) {

							message = Message.obtain();
							message.what = 10086;
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

}
