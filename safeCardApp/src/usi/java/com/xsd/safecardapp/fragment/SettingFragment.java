package com.xsd.safecardapp.fragment;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.dtr.zxing.activity.CaptureActivity;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.AlarmRecordActivity;
import com.xsd.safecardapp.activity.CardInfoActivity;
import com.xsd.safecardapp.activity.DailyPerformanceActivity;
import com.xsd.safecardapp.activity.FamilyActivity;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.activity.NoDisturbingActivity;
import com.xsd.safecardapp.activity.SafeAreaActivity;
import com.xsd.safecardapp.activity.SelectModeActivity;
import com.xsd.safecardapp.activity.ShowNoDisturbingActivity;
import com.xsd.safecardapp.activity.ShowPathActivity;
import com.xsd.safecardapp.activity.WhitelistActivity;
import com.xsd.safecardapp.javabean.LoginJson.LoginResult;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 第二个界面，九宫格展示
 */
public class SettingFragment extends Fragment implements
		OnItemClickListener, OnClickListener {
	
	private MyLoadingDialog dialog;
	private Message msg;
	
	private static final int COUNT = 9;
	private String[] names = {"按键设置",
			"免扰时段","电量查询","报警信息","添加设备","定位设置","允许来电","电子围栏","设备信息"};
	private int[] ids = {R.drawable.backgroud_key,R.drawable.backgroud_blacklist,
			R.drawable.backgroud_battery,R.drawable.backgroud_message,
			R.drawable.backgroud_card_add,R.drawable.backgroud_location,
			R.drawable.backgroud_call,R.drawable.backgroud_crawl,
			R.drawable.backgroud_card_info,
	};
//	private String[] names = {"按键设置","亲情倾听",
//			"班主任管理","免打扰时间","学校管理","电量","短信提醒",};
//	private int[] ids = {R.drawable.backgroud_key,R.drawable.backgroud_dear,
//			R.drawable.backgroud_teacher,R.drawable.backgroud_blacklist,R.drawable.backgroud_school,R.drawable.backgroud_battery,R.drawable.backgroud_message};

	private View rootView; // 根页面
	private GridView gvSetting;

	private Context mContext;
	
	private String resultString;
	
	private List<LoginResult> mResult;
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			switch (msg.what) {
			
			case 90016:
				if(TextUtils.isEmpty(resultString)){
					Toast.makeText(mContext, "查询电量出错", 0).show();
				}else{
					//Toast.makeText(mContext, "当前电量"+resultString+"%", 0).show();
					AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());  //先得到构造器  
			        builder.setTitle("电量"); //设置标题  
			        builder.setMessage("设备剩余电量"+resultString+"%"); //设置内容  
			        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置确定按钮  
			            @Override  
			            public void onClick(DialogInterface dialog, int which) {  
			                dialog.dismiss(); //关闭dialog  
			                //Toast.makeText(getActivity(), "确认" + which, Toast.LENGTH_SHORT).show();  
			            }  
			        });  
			        
			        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮  
			            @Override  
			            public void onClick(DialogInterface dialog, int which) {  
			                dialog.dismiss(); //关闭dialog  
			                //Toast.makeText(getActivity(), "确认" + which, Toast.LENGTH_SHORT).show();  
			            }  
			        });  
			        //参数都设置完成了，创建并显示出来  
			        builder.create().show();  
				}
			
				break;
			case 10086:
				Toast.makeText(mContext, "查询电量出错", 0).show();
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		

		mContext = getActivity();

		rootView = inflater.inflate(R.layout.fragment_setting,
				container, false);
		initView();
		mResult = MainTabActivity.getmResult();
		return rootView;
	}

	private void initView() {
		
		gvSetting = (GridView) rootView.findViewById(R.id.gv_setting);
		gvSetting.setFocusable(false);
		
		gvSetting.setAdapter(new MyAdapter());
		gvSetting.setOnItemClickListener(this);
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent i;
		switch (position) {
	
		case 4:
			Intent intent = new Intent(getActivity(), CaptureActivity.class);
			startActivityForResult(intent, 110);
			break;
		case 0:
			i = new Intent(getActivity(),FamilyActivity.class);
			startActivity(i);
			break;
		case 1:
			i = new Intent(getActivity(),ShowNoDisturbingActivity.class);
			startActivity(i);
		
			break;	
		case 2:
			batteryStatus(mResult.get(HomePageFragment.oldPosition).getImei());			
			break;
		case 3:
			Intent recordIntent = new Intent(getActivity(),AlarmRecordActivity.class);
			startActivity(recordIntent);
			break;
		case 8:
			i = new Intent(getActivity(),CardInfoActivity.class);
			startActivityForResult(i, 110);
			break;
		case 6:
			i = new Intent(getActivity(),WhitelistActivity.class);
			startActivity(i);

			break;
		case 7:
			i = new Intent(getActivity(),SafeAreaActivity.class);
			startActivity(i);
			break;
		case 5:
			i = new Intent(getActivity(),SelectModeActivity.class);
			startActivity(i);
			break;

		default:
			break;
		}
		
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 10086) {
			Intent intent = new Intent(getActivity(), MainTabActivity.class);
			startActivity(intent);
			getActivity().finish();
		} else if (resultCode == 9527) {
			getActivity().finish();
		}
	}
	
	
	private void showLoadingDialog() {

		dialog = MyLoadingDialog.getMyLoadingDialog(
				((MainTabActivity) getActivity()), R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();

	}


	private void batteryStatus(final String imei) {

		showLoadingDialog();
		
		if (NetUtils.isNetworkAvailable(mContext)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "PO"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("PO")));
					try {
						String jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);
						
						System.out.println("heheheaaa"+jsonString);

						JSONObject ojb = new JSONObject(jsonString);
						
						String strcode = ojb.getString("code");
						resultString = ojb.getString("result");
						if(strcode.equals("0")){
							msg = Message.obtain();
							msg.what = 90016;
							
							handler.sendMessage(msg);
						}

							
						 else {
							 msg = Message.obtain();
								msg.what = 10086;
								handler.sendMessage(msg);
						}
					} catch (Exception e) {
						e.printStackTrace();

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(mContext, "请检查网络",
										Toast.LENGTH_SHORT).show();
							}
						});

					}
				}

			});

		} else {

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT)
							.show();
				}
			});

		}
	}

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return COUNT;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			View view = View.inflate(mContext, R.layout.item_setting_gv, null);
			ImageView iv = (ImageView) view.findViewById(R.id.iv_setting_item);
			TextView tv = (TextView) view.findViewById(R.id.tv_setting_item);
			iv.setImageResource(ids[position]);
			tv.setText(names[position]);
			return view;
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	

}