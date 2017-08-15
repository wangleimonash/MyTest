package com.xsd.safecardapp.fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.amap.api.maps.model.Text;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.AttendenceActivity;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.activity.MessageItemActivity;
import com.xsd.safecardapp.adapter.HomeworkLVAdapter;
import com.xsd.safecardapp.db.dao.MessageDAO;
import com.xsd.safecardapp.javabean.HomeworkResult;
import com.xsd.safecardapp.javabean.MessageBean;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InformationFragment extends Fragment implements OnRefreshListener<ListView>, OnItemClickListener {

	private View rootView;
	private PullToRefreshListView mListView;
	//private MyLoadingDialog dialog;
	
	private TextView tvInfo;
	
	private Message message;
//	private HomeworkLVAdapter adapter;
	private List<HomeworkResult> mResult;
	private MessageDAO dao;
	private List<MessageBean> msgList;
	private String username;
	private TextView tvNoInfo;
	
	private SharedPreferences sp;
	
	private RelativeLayout rlNaocan;
	
	private TextView tvName;

	private Handler handler = new Handler() {
		private HomeworkLVAdapter adapter;

		public void handleMessage(Message msg) {

			//if (dialog != null && dialog.isShowing())
				//dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				
				if(msgList.size() ==0){
					tvNoInfo.setVisibility(View.VISIBLE);
				}else{
					tvNoInfo.setVisibility(View.GONE);
				}
				
				adapter = new HomeworkLVAdapter(getActivity(), msgList);
				mListView.setAdapter(adapter);			
				mListView.onRefreshComplete();		
				//MainTabActivity.setImagePoint(false);
//				}else{
//					adapter.notifyDataSetChanged();
//					mListView.onRefreshComplete();
//				}
				break;
			case Consts.MESSAGE_FAIL:
				tvNoInfo.setVisibility(View.VISIBLE);
				break;
			case 9358:
				
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}else{
					adapter = new HomeworkLVAdapter(getActivity(), msgList);
					mListView.setAdapter(adapter);			
					mListView.onRefreshComplete();		
					//MainTabActivity.setImagePoint(false);
				}

				break;

			default:
				break;
			}
		};
	};
	
	public void back(View v){
		getActivity().finish();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.safe_fragment_main_information,
				container, false);
		
		rootView.findViewById(R.id.ib_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getActivity().finish();
				
			}
		});
		
		rlNaocan = (RelativeLayout) rootView.findViewById(R.id.rl_naocan);
		tvNoInfo = (TextView) rootView.findViewById(R.id.tv_no_info);
		rlNaocan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(),AttendenceActivity.class);
				startActivity(i);
			}
		});

		sp = getActivity().getSharedPreferences(Consts.CONFIG, Context.MODE_PRIVATE);
		username = sp.getString("username", "");
		mListView = (PullToRefreshListView) rootView
				.findViewById(R.id.pull_refresh_list);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		
		tvInfo = (TextView) rootView.findViewById(R.id.tv_info);
		tvInfo.setVisibility(View.GONE);
		
		tvName = (TextView) rootView.findViewById(R.id.tv_name);
//		tvInfo.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				
//				Intent i = new Intent(getActivity(),AttendenceActivity.class);
//				startActivity(i);
//				
//			}
//		});
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("ILOVEDOTA");
		getActivity().registerReceiver(myReceiver, filter);
		dao = new MessageDAO(getActivity());			
		
		
		return rootView;
	}
	
	
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		
		getHomeworkFromDataBase(getActivity());
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getHomeworkFromDataBase(getActivity());
		if(MainTabActivity.getmResult()!=null&&MainTabActivity.getmResult().size()>0)
		tvName.setText(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getUserName());
	}
	
	private void getHomeworkFromDataBase(final Context context){
		ExecutorService executorService = MyExecutorService
				.getExecutorService();
		executorService.execute(new Runnable() {
			
			

			@Override
			public void run() {
				
				msgList = dao.getAll(username);
				if(msgList!=null){
					message = Message.obtain();
					message.what = Consts.MESSAGE_SUCCESS;
					handler.sendMessage(message);
				}else{
					message = Message.obtain();
					message.what = Consts.MESSAGE_FAIL;
					handler.sendMessage(message);
				}
			}
			
		});
		
	}
	
	
	
	
	private void updateHomeworkFromDataBase(){
		
		ExecutorService executorService = MyExecutorService
				.getExecutorService();
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				
				msgList = dao.getAll(username);
				if(msgList!=null){
					message = Message.obtain();
					message.what = 9358;
					handler.sendMessage(message);
				}else{
					message = Message.obtain();
					message.what = Consts.MESSAGE_FAIL;
					handler.sendMessage(message);
				}
			}
			
		});
		
	}
	
	
	
	

	private void requestHomeworkData() {

		if (NetUtils.isNetworkAvailable(getActivity())) {

			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					try {
						params.add(new BasicNameValuePair("CD", "BD"));
						params.add(new BasicNameValuePair("PC", sp.getString(
								"username", "")));
						params.add(new BasicNameValuePair("ID", MainTabActivity
								.getmResult().get(MainTabActivity.oldPosition)
								.getImei()));
						params.add(new BasicNameValuePair("AU", AUUtils.getAU("BD")));
						params.add(new BasicNameValuePair("SI", "13523"));
					} catch (Exception e1) {
//						Intent i = new Intent(getActivity(),MainTabActivity.class);
//						getActivity().startActivity(i);
						getActivity().finish();			
						System.out.println("Information  出错重新载入");
					}

					try {

						//对参数编码  
				      	String param = URLEncodedUtils.format(params, "UTF-8"); 
				        
				        //TargetUrl             
						String targetUrl = Consts.URL_PATH+"?"+param; 
						
						System.out.println("dasdsdsjsonString"+targetUrl);
						
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, "http://sz.iok100.com/Html/Home.aspx", params);
						System.out.println("dasdsdsjsonString" + jsonString);

						JSONArray jsonArray = new JSONArray(jsonString);
																														
						mResult = new ArrayList<HomeworkResult>();
						HomeworkResult homeworkResult;
						JSONObject obj;
						for(int i = 0;i<jsonArray.length();i++){
							homeworkResult = new HomeworkResult();
							obj = jsonArray.getJSONObject(i);
							homeworkResult.setContent(obj.getString("content"));
							homeworkResult.setDate(obj.getString("date"));
							homeworkResult.setTitle(obj.getString("title"));
							homeworkResult.setType(obj.getString("type"));
							mResult.add(homeworkResult);
						}
						//String mJsonString =   gson.
						//System.out.println("fsdfsdfsdfdfdf" + mJson.getHomeworkResults()[0].getContent());
//						mResult = mJson.getHomewordResult();
//						
						if(mResult!=null){
							message = Message.obtain();
							message.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(message);
						}else{
							message = Message.obtain();
							message.what = Consts.MESSAGE_FAIL;
							handler.sendMessage(message);
						}
//						resultCode = mJson.getCode();
//
//						if (!TextUtils.isEmpty(resultCode)) {
//							message = Message.obtain();
//							message.what = Consts.MESSAGE_SUCCESS;
//							handler.sendMessage(message);
//						} else {
//							message = Message.obtain();
//							message.what = Consts.MESSAGE_FAIL;
//							handler.sendMessage(message);
//						}

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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = new Intent(getActivity(),MessageItemActivity.class);
		Bundle bundle = new Bundle();
		System.out.println("WOHAOLIHAI"+msgList.get(position-1).getId());
		dao.setRead(Integer.parseInt(msgList.get(position-1).getId()));
		bundle.putString("content", msgList.get(position-1).getContent());
		bundle.putString("title", msgList.get(position-1).getTitle());
		bundle.putString("time", msgList.get(position-1).getCreateDate());
		intent.putExtra("DOTA", bundle);
		startActivity(intent);
	}
	
	
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {  
        
        @Override  
        public void onReceive(Context context, Intent intent) {  
                          System.out.println("HB我来了！！！！！");
                          getHomeworkFromDataBase(getActivity());
        }  
    };  
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	getActivity().unregisterReceiver(myReceiver);
    	myReceiver = null;
    };
   
}
