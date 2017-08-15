package com.xsd.safecardapp.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.*;
import cn.sharesdk.onekeyshare.OnekeyShare;
import com.dtr.zxing.activity.CaptureActivity;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.AlarmRecordActivity;
import com.xsd.safecardapp.activity.BabyInfoActivity;
import com.xsd.safecardapp.activity.CardInfoActivity;
import com.xsd.safecardapp.activity.CreateGesturePasswordActivity;
import com.xsd.safecardapp.activity.FamilyActivity;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.activity.ResetPassWordActivity;
import com.xsd.safecardapp.activity.SafeAreaActivity;
import com.xsd.safecardapp.activity.ScheduleActivity;
import com.xsd.safecardapp.activity.SelectModeActivity;
import com.xsd.safecardapp.activity.SugesstionActivity;
import com.xsd.safecardapp.activity.UserLoginActivity;
import com.xsd.safecardapp.activity.VersionInfoActivity;
import com.xsd.safecardapp.adapter.MeFragmentGVAdapter;
import com.xsd.safecardapp.popupwindow.SelectCardPopupWindow;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.views.MyGridView;

public class MeFragment extends Fragment implements OnClickListener,
		OnItemClickListener {

	private View rootView;
	private LinearLayout llSelectCard;
//	private TextView tvSelectCard;
	private PopupWindow popupWindow;
	private RelativeLayout rlBabyInfo;
	//private MyGridView gridView;
	private TextView tvLogout;
	private TextView tvLogoff;
	private RelativeLayout rlAddSafeCard;
	private RelativeLayout rlGestureLock;
	private RelativeLayout rlAlarmMode;
	private RelativeLayout rlRest;
	private RelativeLayout rlAlarmRecord;
	private RelativeLayout rlVersionDesc;
	private RelativeLayout rlFamily;
	private RelativeLayout rlCardInfo;
	private RelativeLayout rlSugesstion;
	private RelativeLayout rlOfflineMap;
	private RelativeLayout rlTel;
	private RelativeLayout rlShare;
	
	
	private TextView tvBabyInfo;
	private TextView tvLock;

	public static int oldPosition;

	private SharedPreferences sp;
	private Editor editor;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		

		sp = getActivity().getSharedPreferences(Consts.CONFIG, Context.MODE_PRIVATE);
		editor = sp.edit();
		
		rootView = inflater.inflate(R.layout.fragment_me, container,
				false);

		initView();
		
		

		try {
			createPopWindow();
		} catch (Exception e) {
			getActivity().finish();
			e.printStackTrace();
			System.out.println("MeFragment 出错重新载入");
		}

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	public void initView() {
		llSelectCard = (LinearLayout) rootView
				.findViewById(R.id.ll_select_card);
		llSelectCard.setOnClickListener(this);
		//tvSelectCard = (TextView) rootView.findViewById(R.id.tv_select_card);

		rlBabyInfo = (RelativeLayout) rootView.findViewById(R.id.rl_babyinfo);
		rlBabyInfo.setOnClickListener(this);
//		rlAlarmMode = (RelativeLayout) rootView.findViewById(R.id.rl_alarm_mode);
//		rlAlarmMode.setOnClickListener(this);
//		gridView = (MyGridView) rootView.findViewById(R.id.gridview);
//		gridView.setAdapter(new MeFragmentGVAdapter(getActivity()));
//		gridView.setOnItemClickListener(this);
		tvLogout = (TextView) rootView.findViewById(R.id.tv_logout);
		tvLogout.setOnClickListener(this);
		
		tvLogoff = (TextView) rootView.findViewById(R.id.tv_logoff);
		tvLogoff.setOnClickListener(this);
//		rlGestureLock = (RelativeLayout) rootView
//				.findViewById(R.id.rl_set_pic_lock);
//		rlGestureLock.setOnClickListener(this);
//		rlAddSafeCard = (RelativeLayout) rootView
//				.findViewById(R.id.rl_add_safecard);
		rlRest = (RelativeLayout) rootView
				.findViewById(R.id.rl_rest_pwd);
//		rlAddSafeCard.setOnClickListener(this);
		rlRest.setOnClickListener(this);
//		rlAlarmRecord = (RelativeLayout) rootView
//				.findViewById(R.id.rl_alarm_record);
//		rlAlarmRecord.setOnClickListener(this);
		rlVersionDesc = (RelativeLayout) rootView
				.findViewById(R.id.rl_version_desc);
		rlVersionDesc.setOnClickListener(this);
		
		tvBabyInfo = (TextView) rootView.findViewById(R.id.tv_babyinfo);
		
//		tvLock = (TextView) rootView.findViewById(R.id.tv_lock);
//		
//		if(sp.getBoolean("isOpenGesture", false)){
//			tvLock.setVisibility(View.VISIBLE);
//		}else{
//			tvLock.setVisibility(View.INVISIBLE);
//		}
		
		rootView.findViewById(R.id.rl_sugesstion).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(),SugesstionActivity.class);
				startActivity(i);
			}
		});
		
		
		
		
		rootView.findViewById(R.id.rl_tel).setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View view) {
		Toast.makeText(getActivity(), "功能尚在研发中", 0).show();
	}
});
		rootView.findViewById(R.id.rl_share).setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		showShare();
		
	}
});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_select_card:

			showPopupWindow();

			break;
		case R.id.rl_babyinfo:
			Intent toBabyInfoIntent = new Intent(getActivity(),
					BabyInfoActivity.class);
			startActivity(toBabyInfoIntent);
			break;
		case R.id.tv_logout:

			editor.putBoolean(Consts.IS_AUTO_LOGIN, false);
			editor.putBoolean(Consts.IS_REM_PASSWORD, false);
			editor.putString("username", "");
			editor.putString("password", "");
			editor.commit();
			startActivity(new Intent(getActivity(),UserLoginActivity.class));
			getActivity().finish();
			break;
		case R.id.tv_logoff:

			
			getActivity().finish();
			break;
		case R.id.rl_add_safecard:
			Intent intent = new Intent(getActivity(), CaptureActivity.class);
			startActivityForResult(intent, 110);
			break;
		case R.id.rl_set_pic_lock:
			if (getActivity().getSharedPreferences("config",
					Context.MODE_PRIVATE).getBoolean("isSetGesture", false)) {

				if (getActivity().getSharedPreferences("config",
						Context.MODE_PRIVATE).getBoolean("isOpenGesture",
						false)) {
                        tvLock.setVisibility(View.INVISIBLE);
                        editor.putBoolean("isOpenGesture", false);
                        editor.commit();
				}else{
					tvLock.setVisibility(View.VISIBLE);
                    editor.putBoolean("isOpenGesture", true);
                    editor.commit();
				}
			} else {
				Intent intent2 = new Intent(getActivity(),
						CreateGesturePasswordActivity.class);
				startActivity(intent2);
			}
			break;
		case R.id.rl_alarm_mode:
			Intent modeIntent = new Intent(getActivity(),SelectModeActivity.class);
			startActivity(modeIntent);
			break;
		case R.id.rl_rest_pwd:
			Intent restIntent = new Intent(getActivity(),ResetPassWordActivity.class);
			startActivity(restIntent);
			break;	
		case R.id.rl_alarm_record:
			Intent recordIntent = new Intent(getActivity(),AlarmRecordActivity.class);
			startActivity(recordIntent);
			break;
		case R.id.rl_version_desc:
			Intent versionIntent = new Intent(getActivity(),VersionInfoActivity.class);
			startActivity(versionIntent);
			break;
		
		default:
			break;
		}

	}

	private void createPopWindow() {
		popupWindow = SelectCardPopupWindow.getPopupWindow(getActivity(),
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						if (oldPosition != position) {

							oldPosition = position;

//							tvSelectCard.setText(MainTabActivity.getmResult()
//									.get(oldPosition).getUserName());

//							tvBabyInfo.setText(MainTabActivity.getmResult()
//									.get(oldPosition).getUserName());
						}

						showPopupWindow();

					}

				});

	}

	@Override
	public void onResume() {
//		tvBabyInfo.setText(MainTabActivity.getmResult().get(oldPosition)
//				.getUserName());
//		tvSelectCard.setText(MainTabActivity.getmResult().get(oldPosition)
//				.getUserName());
		
//		if (getActivity().getSharedPreferences("config",
//				Context.MODE_PRIVATE).getBoolean("isOpenGesture",
//				false)) {
//                tvLock.setVisibility(View.VISIBLE);               
//		}else{
////			tvLock.setVisibility(View.INVISIBLE);           
//		}
		super.onResume();
	}

	private void showPopupWindow() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(llSelectCard,
					-(popupWindow.getWidth() - llSelectCard.getWidth()) / 2, 0);
		}
	}

	// private void ShowLogoutDialog() {
	// AlertDialog.Builder builder = new Builder(getActivity());
	// View view = View.inflate(getActivity(), R.layout.dialog_logout, null);
	// et_notpsd1 = (EditText) view.findViewById(R.id.et_notpsd1);
	// et_notpsd2 = (EditText) view.findViewById(R.id.et_notpsd2);
	// btn_ok = (Button) view.findViewById(R.id.btn_ok);
	// btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
	// btn_ok.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// String password1 = et_notpsd1.getText().toString().trim();
	// String password2 = et_notpsd2.getText().toString().trim();
	// if(TextUtils.isEmpty(password1)||TextUtils.isEmpty(password2)){
	// Toast.makeText(Home.this, "不能为空", 0).show();
	// }else if(!(password1.equals(password2))){
	// Toast.makeText(Home.this, "不匹配", 0).show();
	// }else{
	// Editor editor = sp.edit();
	// editor.putString("password", password1);
	// editor.commit();
	// }
	// dialog.dismiss();
	// }
	// });
	// btn_cancel.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// dialog.dismiss();
	//
	// }
	// });
	// builder.setView(view);
	// dialog = builder.show();
	//
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:// 家庭成员按钮
			Intent toFamilyIntent = new Intent(getActivity(),
					FamilyActivity.class);
			startActivity(toFamilyIntent);
			break;
		case 1:// 平安卡信息按钮
			Intent toCardInfo = new Intent(getActivity(),
					CardInfoActivity.class);
			startActivityForResult(toCardInfo, 110);
			break;
		case 2:// 安全区域按钮
			Intent toSafeAreaIntent = new Intent(getActivity(),
					SafeAreaActivity.class);
			startActivity(toSafeAreaIntent);
			break;
		case 3:
			//Toast.makeText(getActivity(), "功能研发中，敬请期待", Toast.LENGTH_SHORT).show();
			showShare();
			break;
		case 4:
			Toast.makeText(getActivity(), "功能研发中，敬请期待", Toast.LENGTH_SHORT).show();
			//showShare();
			break;
		case 5:
			Toast.makeText(getActivity(), "功能研发中，敬请期待", Toast.LENGTH_SHORT).show();
			//showShare();
			break;
		default:
			break;
		}
	}

	private void showShare() {
		ShareSDK.initSDK(getActivity());
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.xsd.safecardapp");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("监护孩子的校内外安全，实时了解孩子的学习情况。");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.png");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setImageUrl(Consts.URL_PATH_SHARE_PIC);
		
		oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.xsd.safecardapp");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("都来试试吧");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.xsd.safecardapp");

		// 启动分享GUI
		oks.show(getActivity());
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
	

}
