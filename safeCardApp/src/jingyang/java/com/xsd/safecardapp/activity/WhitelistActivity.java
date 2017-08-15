package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.WhileListLVAdapter;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.fragment.MeFragment;
import com.xsd.safecardapp.javabean.SwitchJson;
import com.xsd.safecardapp.javabean.WhiteBean;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 
 * @author OnlyWay
 *
 *         功能：白名单列表
 *
 *         时间 2015年8月31日
 */
public class WhitelistActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener, android.view.View.OnClickListener {

	private String jsonString;
	private Message msg;
	
	
	private MyLoadingDialog dialog;

	private ListView lvWhiteList;
	private List<WhiteBean.WhiteEntity> entity;
	private WhileListLVAdapter adapter;
	
	private SwitchJson switchJson;
	
	private Boolean isOpend = false;
	
	private ImageButton ibToggle;
	private ImageButton ibAdd;
	
	private LinearLayout ll;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if(dialog!=null && dialog.isShowing())
		        dialog.dismiss();
			
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				adapter = new WhileListLVAdapter(WhitelistActivity.this, entity);
				lvWhiteList.setAdapter(adapter);
				
				if(switchJson.getResult().getSt() == 1){
					ibToggle.setImageResource(R.drawable.switch_on);
					isOpend = true;
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					isOpend = false;
				}
				if(isOpend){
					ibToggle.setImageResource(R.drawable.switch_on);
					ll.setVisibility(View.VISIBLE);
					ibAdd.setVisibility(View.VISIBLE);
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					ll.setVisibility(View.INVISIBLE);
					ibAdd.setVisibility(View.INVISIBLE);
				}
				break;

			case Consts.MESSAGE_FAIL:
				Toast.makeText(WhitelistActivity.this, "服务器出错啦", 0).show();
				break;

			case Consts.MESSAGE_EMPTY:

				break;
			case 10086:
				adapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "删除成功",
						Toast.LENGTH_SHORT).show();
				break;
			case 10088:
				adapter = new WhileListLVAdapter(WhitelistActivity.this, entity);
				lvWhiteList.setAdapter(adapter);
				Toast.makeText(getApplicationContext(), "功能开关尚未打开",
						Toast.LENGTH_SHORT).show();
				if(isOpend){
					ibToggle.setImageResource(R.drawable.switch_on);
					ll.setVisibility(View.VISIBLE);
					ibAdd.setVisibility(View.VISIBLE);
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					ll.setVisibility(View.INVISIBLE);
					ibAdd.setVisibility(View.INVISIBLE);
				}
				break;
			case 10089:
				isOpend = !isOpend;
				String str = isOpend?"允许来电开关已开启":"允许开关已关闭";
				Toast.makeText(getApplicationContext(), str,
						Toast.LENGTH_SHORT).show();
				if(isOpend){
					ibToggle.setImageResource(R.drawable.switch_on);
					ll.setVisibility(View.VISIBLE);
					ibAdd.setVisibility(View.VISIBLE);
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					ll.setVisibility(View.INVISIBLE);
					ibAdd.setVisibility(View.INVISIBLE);
				}
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
		setContentView(R.layout.activity_white_list);
		lvWhiteList = (ListView) findViewById(R.id.lv_white_list);
		lvWhiteList.setOnItemClickListener(this);
		lvWhiteList.setOnItemLongClickListener(this);
		
		ibToggle = (ImageButton) findViewById(R.id.ib_toggle);
		ibToggle.setOnClickListener(this);
		
		ibAdd = (ImageButton) findViewById(R.id.ib_add);
		ll = (LinearLayout) findViewById(R.id.ll);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		requestWhiteListData(MainTabActivity.getmResult()
				.get(HomePageFragment.oldPosition).getImei());
	}

	/**
	 * 请求安全区域信息
	 */
	private void requestWhiteListData(final String imei) {
		
		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "PD"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("PD")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("WANAN" + jsonString);

						Gson gson = new Gson();
						WhiteBean bean = gson.fromJson(jsonString, WhiteBean.class);
						entity = bean.getResult();

						if (entity != null && entity.size() > 0) {
							// 先将参数放入List，再对参数进行URL编码
							List<BasicNameValuePair> params2 = new LinkedList<BasicNameValuePair>();
							params2.add(new BasicNameValuePair("CD", "GETST"));
							params2.add(new BasicNameValuePair("ID", imei));
							params2.add(new BasicNameValuePair("TY", "1"));
							params2.add(new BasicNameValuePair("AU", AUUtils.getAU("GETST")));

							
								jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
										Consts.URL_PATH, params2);

								System.out.println("EEEEE" + jsonString);
								
								Gson gson2 = new Gson();
								switchJson = gson2.fromJson(jsonString, SwitchJson.class);
								

								if (switchJson.getCode() == 0) {
									
									msg = Message.obtain();
									msg.what = Consts.MESSAGE_SUCCESS;
									handler.sendMessage(msg);
								}else if(switchJson.getCode() == 12){
									msg = Message.obtain();
									msg.what = 10088;
									handler.sendMessage(msg);
								} 
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
		Intent intent = new Intent(this, WhiteListAddActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

//		if (resultCode == Consts.SAFEAREA_TO_ADDSAFEAREA) {
//			Bundle bundle = data.getBundleExtra(Consts.INTENT_DATA);
//			if (bundle != null) {
//				SafeAreaInfoResult returnResult = (SafeAreaInfoResult) bundle
//						.getSerializable("safeareainfo");
//				if (infoResult == null) {
//					infoResult = new ArrayList<SafeAreaInfoJson.SafeAreaInfoResult>();
//					adapter = new SafeAreaLVAdapter(this, infoResult);
//				}
//				infoResult.add(returnResult);
//				System.out.println("dasdasdaswf"+infoResult.size());
//				lvSafeArea.setAdapter(adapter);
//				//adapter.notifyDataSetChanged();
//			} else {
//				Toast.makeText(getApplicationContext(), "bundleweikong",
//						Toast.LENGTH_SHORT).show();
//			}
//		}
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
//		Intent intent = new Intent(this, EditSafeAreaActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("safeareainfo", infoResult.get(position));
//		intent.putExtra(Consts.INTENT_DATA, bundle);
//		startActivityForResult(intent, Consts.SAFEAREA_TO_EDITSAFEAREA);
		Intent i = new Intent(this,WhiteListEditActivity.class);
		Bundle b = new Bundle();
		b.putString("NO", entity.get(position).getID());
		b.putString("NM", entity.get(position).getNM());
		b.putString("PC", entity.get(position).getPC());
		System.out.println("ACAC"+entity.get(position).getID());
		System.out.println("ACAC"+entity.get(position).getNM());
		i.putExtra(Consts.INTENT_DATA, b);
		startActivity(i);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		createDialog(position);
		return true;
	}

	private void createDialog(final int position) {
		AlertDialog.Builder builder = new Builder(WhitelistActivity.this,R.style.alert_dialog);
		builder.setMessage("是否删除该白名单号码");
		builder.setTitle("提示");
		builder.setPositiveButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setNegativeButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				delete(
						MainTabActivity.getmResult()
								.get(HomePageFragment.oldPosition).getImei(),
						position,entity.get(position).getID());
			}

		});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	/**
	 * 删除安全区域
	 */
	private void delete(final String imei, final int position,final String no) {
		
		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "DEPD"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("NO", entity.get(position).getID()));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("DEPD")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						JSONObject obj = new JSONObject(jsonString);

						String code = obj.getString("code");

						if ("0".equals(code)) {
							entity.remove(position);
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
	
	/**
	 * 设置开关咯
	 */
	private void requestToggle(final String imei, final String state ) {

		showLoadingDialog();
		
		if (NetUtils.isNetworkAvailable(this)) {
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "SETST"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("TY", "1"));
					params.add(new BasicNameValuePair("ST", state));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("SETST")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("EEEEE" + jsonString);

						JSONObject obj;
						
						obj = new JSONObject(jsonString);
						
						String code = obj.getString("code");
						

						if ("0".equals(code)) {
							msg = Message.obtain();
							msg.what = 10089;
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

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.ib_toggle){
			String state = !isOpend?"1":"0";
			requestToggle(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei(),state);
		}
	}
}
