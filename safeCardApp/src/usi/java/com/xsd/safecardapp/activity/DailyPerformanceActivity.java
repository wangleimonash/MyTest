package com.xsd.safecardapp.activity;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.App;
import com.xsd.safecardapp.adapter.PopupGridViewAdapter;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.javabean.DateBean;
import com.xsd.safecardapp.javabean.EvaluateJson;
import com.xsd.safecardapp.popupwindow.PopupWindowUtils;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.RequestDataUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 学生日常表现
 *
 */
public class DailyPerformanceActivity extends Activity implements OnClickListener, OnItemClickListener{

	private PopupWindow popupWindow;
	private View popupView;

	private List<DateBean> listData = new ArrayList<DateBean>();

	private ImageView iv1;
	private ImageView iv2;
	private ImageView iv3;
	private ImageView iv4;
	private ImageView iv5;

	private Button btn;

	private ImageView[] ivs ;

	private Calendar calendar = Calendar.getInstance();

	private TextView tvTeacher;
	private TextView tvDesc;

	private Message msg;
	private MyLoadingDialog dialog;

	private int todayPositon;

	EvaluateJson json;
	private TextView tv_date;
	private Button bt_pre;
	private Button bt_next;

	private Calendar calendars;
	SimpleDateFormat simpleDateFormat;
	private void showMyCalendar() {

		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(btn);
		}

	}

	/**
	 * 创建显示选择日期的日历PopupWindow
	 */
	private void createMyCalendar() {
		popupView = View.inflate(this, R.layout.popup_show_calendar, null);
		GridView gridView = (GridView) popupView.findViewById(R.id.gridview);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(new PopupGridViewAdapter(this, ""));

		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
		popupWindow.setOutsideTouchable(true);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			bt_next.setEnabled(true);
			bt_pre.setEnabled(true);
			tv_date.setEnabled(true);
			switch (msg.what) {
				case Consts.MESSAGE_SUCCESS:
					tvTeacher.setText(json.getResult().getTechaer());
					tvDesc.setText(json.getResult().getRmark());

					int num = json.getResult().getNum();

					for(int i=0;i<num;i++){
						ivs[i].setImageResource(R.drawable.ic_star_chose);
					}
					break;

				case Consts.MESSAGE_FAIL:

					break;

				case Consts.MESSAGE_EMPTY:
					setUnChecked();
					tvTeacher.setText("老师评价:");
					tvDesc.setText("当天尚无评价");
					Toast.makeText(getApplicationContext(), "当天尚无评价",
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

	public void changeDate(View v){
		showMyCalendar();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily);
		iv1 = (ImageView) findViewById(R.id.iv_1);
		iv2 = (ImageView) findViewById(R.id.iv_2);
		iv3 = (ImageView) findViewById(R.id.iv_3);
		iv4 = (ImageView) findViewById(R.id.iv_4);
		iv5 = (ImageView) findViewById(R.id.iv_5);
		tvTeacher = (TextView) findViewById(R.id.tv_teacher);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		bt_next = (Button)findViewById(R.id.bt_next);
		bt_pre = (Button)findViewById(R.id.bt_pre);
		tv_date = (TextView)findViewById(R.id.tv_date);
		iv1.setOnClickListener(this);
		iv2.setOnClickListener(this);
		iv3.setOnClickListener(this);
		iv4.setOnClickListener(this);
		iv5.setOnClickListener(this);
		bt_next.setOnClickListener(this);
		bt_pre.setOnClickListener(this);
		tv_date.setOnClickListener(this);
		ivs = new ImageView[]{iv1,iv2,iv3,iv4,iv5};

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dt = format.format(new Date());

		simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		calendars = Calendar.getInstance();
		Date date = new Date(System.currentTimeMillis());
		calendars.setTime(date);

		btn = (Button) findViewById(R.id.btn);
		createMyCalendar();
		getData();
		tv_date.setText(simpleDateFormat.format(calendars.getTime()));
		requestAlarmRecord(App.getInstance().getmLoginReult().getResult().get(HomePageFragment.oldPosition).getUserId()+"",dt);
	}

	/**
	 * 请求报警日志信息
	 */
	private void requestAlarmRecord( final String si,final String dt)
	{
		bt_next.setEnabled(false);
		bt_pre.setEnabled(false);
		tv_date.setEnabled(false);
		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {

					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "GETPJ"));
					params.add(new BasicNameValuePair("SI", si));
					params.add(new BasicNameValuePair("DT", dt));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("GETPJ")));

					try {
						String jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("fsdfdfhre" + jsonString);

						Gson gson = new Gson();
						json = gson.fromJson(jsonString,
								EvaluateJson.class);


						if (json.getCode() == 0) {
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

	public void setUnChecked(){
		iv1.setImageResource(R.drawable.ic_star);
		iv2.setImageResource(R.drawable.ic_star);
		iv3.setImageResource(R.drawable.ic_star);
		iv4.setImageResource(R.drawable.ic_star);
		iv5.setImageResource(R.drawable.ic_star);
	}

	public void finishMyself(View v){
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_1:

				break;
			case R.id.tv_2:

				break;
			case R.id.tv_3:

				break;
			case R.id.tv_4:

				break;
			case R.id.tv_5:

				break;
			case R.id.bt_pre:
				calendars.add(Calendar.DAY_OF_MONTH, -1);
				String text = simpleDateFormat.format(calendars.getTime());
				tv_date.setText(text);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String dt = format.format(DailyPerformanceActivity.this.calendars.getTime());
				requestAlarmRecord(
						App.getInstance().getmLoginReult().getResult().get(HomePageFragment.oldPosition).getUserId()+"",
						dt);
				break;
			case R.id.bt_next:
				calendars.add(Calendar.DAY_OF_MONTH,1);
				String text1 = simpleDateFormat.format(calendars.getTime());
				tv_date.setText(text1);
				SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd");
				String dts = formats.format(DailyPerformanceActivity.this.calendars.getTime());
				requestAlarmRecord(
						App.getInstance().getmLoginReult().getResult().get(HomePageFragment.oldPosition).getUserId()+"",
						dts);
				break;
			case R.id.tv_date:
				PopupWindowUtils.show(this, calendars, findViewById(R.id.ll_date), new PopupWindowUtils.OnPoupuItemClickListener() {
					@Override
					public void itemClick(Calendar calendar, PopupWindow popupWindow) {
						DailyPerformanceActivity.this.calendars = calendar;
						String text1 = simpleDateFormat.format(DailyPerformanceActivity.this.calendars.getTime());
						tv_date.setText(text1);
						popupWindow.dismiss();
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						String dt = format.format(DailyPerformanceActivity.this.calendars.getTime());
						requestAlarmRecord(
								App.getInstance().getmLoginReult().getResult().get(HomePageFragment.oldPosition).getUserId()+"",
								dt);

					}
				});
				break;
			default:
				break;
		}
	}

	private void getData() {
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		for (int i = 27 + week; i > 0; i--) {
			Calendar headCalender = Calendar.getInstance();
			DateBean dateBean = new DateBean();
			Calendar test = setStartTime(headCalender, -i);
			dateBean.setYear(test.get(Calendar.YEAR));
			dateBean.setMonth(test.get(Calendar.MONTH) + 1);
			dateBean.setDay(test.get(Calendar.DATE));
			dateBean.setWeek(test.get(Calendar.DAY_OF_WEEK));
			listData.add(dateBean);
		}
		todayPositon = 27 + week;
		// Calendar calendar3 = Calendar.getInstance();
		DateBean datedsaBean = new DateBean();
		datedsaBean.setYear(calendar.get(Calendar.YEAR));
		datedsaBean.setMonth(calendar.get(Calendar.MONTH) + 1);
		datedsaBean.setDay(calendar.get(Calendar.DATE));
		datedsaBean.setWeek(calendar.get(Calendar.DAY_OF_WEEK));
		datedsaBean.setToday(true);
		listData.add(datedsaBean);

		int j = week;
		int k = 1;
		while (7 - j > 0) {
			Calendar footCalendar = Calendar.getInstance();
			DateBean dateBean = new DateBean();
			Calendar test = setStartTime(footCalendar, k);
			dateBean.setYear(test.get(Calendar.YEAR));
			dateBean.setMonth(test.get(Calendar.MONTH) + 1);
			dateBean.setDay(test.get(Calendar.DATE));
			dateBean.setWeek(test.get(Calendar.DAY_OF_WEEK));
			listData.add(dateBean);
			k++;
			j++;
		}
	}

	private static Calendar setStartTime(Calendar calendar, int day) {
		calendar.add(Calendar.DATE, day);
		return calendar;
	}

	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		showMyCalendar();

		requestAlarmRecord(
				App.getInstance().getmLoginReult().getResult().get(HomePageFragment.oldPosition).getUserId()+"",
				RequestDataUtils.getDailyDate(listData.get(position))
		);
	}

}
