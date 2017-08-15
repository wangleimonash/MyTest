package com.xsd.safecardapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.hysd.usiapp.R;
import com.readystatesoftware.viewbadger.BadgeView;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.fragment.InteractionFragment;
import com.xsd.safecardapp.fragment.MeFragment;
import com.xsd.safecardapp.fragment.SettingFragment;
import com.xsd.safecardapp.javabean.CardInfoBean.CardData;
import com.xsd.safecardapp.javabean.LoginJson.LoginResult;
import com.xsd.safecardapp.receiver.CompleteReceiver;
import com.xsd.safecardapp.service.HeartBeatService;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 主界面，用户登录成功后进入此页面，进行检查版本信息，Fragment切换等操作
 * 以及一些用户登录临时信息的缓存。
 */
public class MainTabActivity extends Activity {

    private HomePageFragment homePageFragment;
    private InteractionFragment mfragment6;
    private MeFragment meFragment;
    private SettingFragment mfragment8;
    private FragmentTransaction mTransaction;
    private RadioGroup mGadioGroup;
    private SharedPreferences sp;
    private TextView ivPoint;
    private RelativeLayout rlBadge;

    private String version;
    private String downloadurl;

    private String clientVersionCode;
    private PackageManager packageManager;

    private Message message;

    private BadgeView badgeView;

    private static Context mContext;

    private static CompleteReceiver completeReceiver;
    private boolean fragmentShow = false;
    private int position;
    public int selectFragment = 1;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == Consts.MESSAGE_SUCCESS) {
                // 注册下载完成后广播调用 2014/9/17 20:14
                if (completeReceiver == null)
                    completeReceiver = new CompleteReceiver(); // 添加监听

                registerReceiver(completeReceiver,
                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                // 因为对话框是activity的一部分显示对话框 必须指定activity的环境（令牌）
                AlertDialog.Builder builder = new Builder(MainTabActivity.this);
                builder.setTitle("软件更新");
                builder.setMessage("软件有新的版本了");
                // builder.setCancelable(false);

                builder.setNegativeButton("下次再说", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("立刻更新", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        download(MainTabActivity.this, downloadurl, "safecard/apk/", "safeCardApp", getString(R.string.app_name));
                    }

                });
                builder.show();
            }
        }

        ;
    };

    // 用来判断平安卡列表正在显示哪张卡的信息，即主页侧滑菜单选择的学生，默认是第一个0
    public static int oldPosition = 0;

    //用户登录成功的信息保存在这和application里面
    public static List<CardData> cardsInfo;// 平安卡信息列表
    public static List<LoginResult> mResult;

    public static List<LoginResult> getmResult() {
        return mResult;
    }

    public static void setmResult(List<LoginResult> mResult) {
        MainTabActivity.mResult = mResult;
    }

    public static LatLng currentLatLng;

    public static LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public static void setCurrentLatLng(LatLng currentLatLng) {
        MainTabActivity.currentLatLng = currentLatLng;
    }

    public List<CardData> getCardsInfo() {
        return cardsInfo;
    }

    public void setCardsInfo(List<CardData> cardsInfo) {
        this.cardsInfo = cardsInfo;
    }

    private int backNumber = 0;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivPoint = (TextView) findViewById(R.id.iv_point);
        rlBadge = (RelativeLayout) findViewById(R.id.rl_badge);

        badgeView = new BadgeView(this, rlBadge);

        sp = getSharedPreferences(Consts.CONFIG, MODE_PRIVATE);

        /**
         * 如果打开了手势锁进入解锁页面
         */
        if (sp.getBoolean("isSetGesture", false)) {
            if (sp.getBoolean("isOpenGesture", false)) {
                Intent intent = new Intent(this, UnlockGestureActivity.class);
                startActivityForResult(intent, 110);
            }
        }
        mContext = this;

        if (savedInstanceState == null) {
            if (homePageFragment == null) {
                mTransaction = getFragmentManager().beginTransaction();
                homePageFragment = new HomePageFragment(this);
                mTransaction.replace(R.id.safe_activity_main_frame, homePageFragment);
                mTransaction.commit();
            }
        }
        initView();

        /**
         * 开启后台服务获取家庭作业等信息
         */
        Intent i = new Intent(this, HeartBeatService.class);
        startService(i);

        registerReceiver(changeMessgeRecevier, new IntentFilter("change_message_size"));

        //updates();
        position = getIntent().getIntExtra("position", -1);
        changeTwoEnable(position,!(position == -1),1);
    }

    private void updates() {
        packageManager = getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            clientVersionCode = packInfo.versionName;
            checkVersion();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void changeTwoEnable(int position,boolean enable,int selectItem){
        if(position!=-1&&"2".equals(Consts.APK_TYPE)) {
            getFragment(selectItem);
            findViewById(R.id.safe_acivity_main_rb_record).setEnabled(enable);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        addMessageSize();
    }

    /**
     * 检查更新
     */
    private void checkVersion() {
        if (NetUtils.isNetworkAvailable(this)) {
            ExecutorService executorService = MyExecutorService
                    .getExecutorService();
            executorService.execute(new Runnable() {

                @Override
                public void run() {
                    List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
                    try {
                        // 对参数编码
                        String param = URLEncodedUtils.format(params, "UTF-8");
                        System.out.println("dasdasdag456" + "http://sz.iok100.com:8080/hwts/info.json"
                                + "?" + param);
                        String jsonString = MyHttpSend.httpSend(
                                MyHttpSend.TYPE_GET, "http://sz.iok100.com:8080/hwts/info.json", params);
                        System.out.println("dasdasdag456" + jsonString);
                        JSONObject object = new JSONObject(jsonString);
                        version = object.getString("version");
                        downloadurl = object.getString("downloadurl");

                        System.out.println("dasdasdag456" + version);
                        System.out.println("dasdasdag456" + clientVersionCode);
                        System.out.println("dasdasdag456" + downloadurl);

                        if (!version.equals(clientVersionCode)) {
                            message = Message.obtain();
                            message.what = Consts.MESSAGE_SUCCESS;
                            handler.sendMessage(message);
                        }
//						if (!TextUtils.isEmpty(resultCode)) {
//							message = Message.obtain();
//							message.what = Consts.MESSAGE_SUCCESS;
//							handler.sendMessage(message);
//							
//							
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
//			message = Message.obtain();
//			message.what = Consts.MESSAGE_FAIL;
//			handler.sendMessage(message);
        }
    }

    public synchronized void addMessageSize() {
        int count = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        Log.d("FingerArt", "count:" + count);
        //showPoint(false, count, count + "", count > 0);
        if (count <= 0) {
            if(fragmentShow){
                ivPoint.setVisibility(View.VISIBLE);
            }else {
                ivPoint.setVisibility(View.INVISIBLE);
            }
        } else {
            //ivPoint.setText(count + "");
            ivPoint.setVisibility(View.VISIBLE);
        }
    }
    
    BroadcastReceiver changeMessgeRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addMessageSize();
        }
    };

    /**
     * 初始化布局
     */
    private void initView() {
        mGadioGroup = (RadioGroup) findViewById(R.id.safe_acivity_main_radioGroup);
        mGadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.safe_acivity_main_rb_map:
                        selectFragment = 1;
                        getFragment(1);
                        break;
                    case R.id.safe_acivity_main_rb_information:
                        selectFragment = 2;
                        getFragment(2);
                        break;
                    case R.id.safe_acivity_main_rb_record:
                        selectFragment = 3;
                        getFragment(3);
                        break;
                    case R.id.safe_acivity_main_rb_me:
                        selectFragment = 4;
                        getFragment(4);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Fragment的替换
     */
    private void getFragment(int position) {
        FragmentTransaction mTransaction = getFragmentManager()
                .beginTransaction();
        hideFragments(mTransaction);

        switch (position) {
            case 1:
                if (homePageFragment == null) {
                    homePageFragment = HomePageFragment.getInstatnce(this);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    homePageFragment.setArguments(bundle);
                    mTransaction.add(R.id.safe_activity_main_frame, homePageFragment);
                } else {
                    mTransaction.show(homePageFragment);
                }
                break;
            case 2:
                if (mfragment6 == null) {
                    mfragment6 = new InteractionFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    mfragment6.setArguments(bundle);
                    mfragment6.setOnChangeIvListener(new InteractionFragment.ChangeIvListener() {
                        @Override
                        public void change(boolean show) {
                            fragmentShow = show;
                            addMessageSize();
                        }
                    });
                    mTransaction.add(R.id.safe_activity_main_frame, mfragment6);
                } else {
                    mTransaction.show(mfragment6);
                }
                break;
            case 4:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    meFragment.setArguments(bundle);
                    mTransaction.add(R.id.safe_activity_main_frame, meFragment);
                } else {
                    mTransaction.show(meFragment);
                }
                break;
            case 3:
                if (mfragment8 == null) {
                    mfragment8 = new SettingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position",position);
                    mfragment8.setArguments(bundle);
                    mTransaction.add(R.id.safe_activity_main_frame, mfragment8);
                } else {
                    mTransaction.show(mfragment8);

                }
                break;

            default:
                break;
        }
        mTransaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏Fragment
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homePageFragment != null) {
            transaction.hide(homePageFragment);
        }
        if (mfragment6 != null) {
            transaction.hide(mfragment6);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
        if (mfragment8 != null) {
            transaction.hide(mfragment8);
        }
    }
    long time[] = new long[2];
    @Override
    public void onBackPressed() {
        long current = System.currentTimeMillis();
        System.arraycopy(time, 1, time, 0, time.length - 1);
        time[time.length-1] = current;
        if(time[time.length-1] - time[0]<2000){
            super.onBackPressed();
        }else{
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        }

    }

//
//    /**
//     * 返回键响应事件
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
//            if (backNumber > 0)
//                finish();
//            backNumber++;
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    backNumber = 0;
//                }
//            }).start();
//
//            return false;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == UnlockGestureActivity.RESULT_OK) {

        }
    }

    /**
     * 下载文件 2014/9/17 19:50
     *
     * @param context       上下文环境
     * @param strUrl        下载地址
     * @param strFolderName 存放目录名称
     * @param strApkName    存放的apk名称
     * @param strShowTitle  限制在通知栏的名称
     */
    public static void download(Context context, String strUrl, String strFolderName,
                                String strApkName, String strShowTitle) {

        File folder = new File(Environment.getExternalStorageDirectory(), strFolderName);
        boolean isSuccess = (folder.exists() && folder.isDirectory()) || folder.mkdirs();
        if (isSuccess) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(strUrl));
            request.setDestinationInExternalPublicDir(strFolderName, strApkName + ".apk");

            request.setTitle(strShowTitle); // 设置下载中通知栏提示的标题
            request.setDescription("正在下载" + strShowTitle); // 设置下载中通知栏提示的介绍

            /**
             表示下载进行中和下载完成的通知栏是否显示。默认只显示下载中通知。
             VISIBILITY_VISIBLE_NOTIFY_COMPLETED表示下载完成后显示通知栏提示。
             VISIBILITY_HIDDEN表示不显示任何通知栏提示，
             这个需要在AndroidMainfest中添加权限android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
             */
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            /**
             表示下载允许的网络类型，默认在任何网络下都允许下载。
             有NETWORK_MOBILE、NETWORK_WIFI、NETWORK_BLUETOOTH三种及其组合可供选择。
             如果只允许wifi下载，而当前网络为3g，则下载会等待。
             */
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

//            request.setMimeType("application/vnd.android.package-archive");

            // 执行下载，返回downloadId，downloadId可用于后面查询下载信息。若网络不满足条件、Sdcard挂载中、超过最大并发数等异常会等待下载，正常则直接下载。
            long downloadId = downloadManager.enqueue(request);

            Toast.makeText(context, "开始下载", Toast.LENGTH_LONG).show();  // 通知用户下载信息
        }
    }

    /**
     * 注销广播 2015/3/9 12:16
     */
    public static void unCompleteReceiver() {
        if (completeReceiver != null)
            mContext.unregisterReceiver(completeReceiver);
    }

    @Override
    protected void onDestroy() {
        //EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        super.onDestroy();
        //stopService(i);
        stopService(new Intent(this, HeartBeatService.class));
        unregisterReceiver(changeMessgeRecevier);
        // 把此activity 从foreground activity 列表里移除
        EaseUI.getInstance().popActivity(this);
    }

//    public void showPoint(boolean isFragment,int count,String text,boolean show){
//        if(isFragment){
//            if(show){
//                ivPoint.setVisibility(View.VISIBLE);
//            }else{
//                int visiable = mfragment6.hasRead?View.INVISIBLE:View.VISIBLE;
//                ivPoint.setVisibility(visiable);
//            }
//            ivPoint.setVisibility(View.VISIBLE);
//        }else {
//            if(show){
//                ivPoint.setVisibility(View.VISIBLE);
//            }else{
//                if (count < 0) {
//                    ivPoint.setVisibility(View.INVISIBLE);
//                } else {
//                    ivPoint.setVisibility(View.VISIBLE);
//                }
//            }
//        }
//
//    }
}
