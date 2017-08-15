package com.xsd.safecardapp;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.xsd.safecardapp.javabean.LoginResult;
import com.xsd.safecardapp.utils.LockPatternUtils;

import android.app.Application;

/**
 * App:作为项目的application，主要用来保存一些临时数据，如：imei,sid等；
 * 进行一些第三方应用的初始化操作，如：环信，手势解锁。获得学生数据使用App.getInstance.getmLoginReult();

 */
public class App extends Application{
	
	public static App mInstance = null;
	
	private static String hxId;
	
	private LoginResult mLoginReult;
	
	/**
	 * 获得缓存的用户登录信息
	 * @return
	 */
	public LoginResult getmLoginReult() {
		return mLoginReult;
	}

	public void setmLoginReult(LoginResult mLoginReult) {
		this.mLoginReult = mLoginReult;
	}

	/**
	 * 获得缓存的环信id
	 * @return
	 */
	public static String getHxId() {
		return hxId;
	}

	public static void setHxId(String hxId) {
		App.hxId = hxId;
	}

	private LockPatternUtils mLockPatternUtils;
	
	/**
	 * 获得Application实例
	 * @return
	 */
	public static App getInstance() {
		return mInstance;
	}
	
	public static String username;
		
	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		App.username = username;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		
		/**
		 * 环信easeui初始化
		 * @return
		 */
		EMOptions options = new EMOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		//初始化
//		EMClient.getInstance().init(this, options);
//		//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//		EMClient.getInstance().setDebugMode(true);
		
		EaseUI.getInstance().init(this, options);

	}
	
	/**
	 * 手势解锁工具类
	 * @return
	 */
	public LockPatternUtils getLockPatternUtils() {
        if (mLockPatternUtils == null)
            mLockPatternUtils = new LockPatternUtils(this);

		return mLockPatternUtils;
	}

    public void clearLockPatternUtils() {
        if (mLockPatternUtils != null) mLockPatternUtils.clear();

        mLockPatternUtils = null;
    }

}
