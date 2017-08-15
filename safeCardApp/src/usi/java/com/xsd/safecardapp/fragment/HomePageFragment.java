package com.xsd.safecardapp.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.location.core.CoordinateConvert;
import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.App;
import com.xsd.safecardapp.activity.AddDeviceActivity;
import com.xsd.safecardapp.activity.GuideActivity;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.activity.SafeAreaActivity;
import com.xsd.safecardapp.activity.ShowPathActivity;
import com.xsd.safecardapp.activity.UserInfoOfCardActivity;
import com.xsd.safecardapp.activity.UserLoginActivity;
import com.xsd.safecardapp.adapter.SelectCardAdapter;
import com.xsd.safecardapp.javabean.HXJson;
import com.xsd.safecardapp.javabean.LoginJson;
import com.xsd.safecardapp.javabean.LoginJson.LoginResult;
import com.xsd.safecardapp.javabean.PingAnJson;
import com.xsd.safecardapp.javabean.PingAnJson.Result;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.MD5Utils;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 作者： OnlyWay
 * <p/>
 * 作用：首页位置显示
 */
public class HomePageFragment extends Fragment implements
        OnMarkerClickListener, OnClickListener, AMapLocationListener,
        OnItemClickListener {

    //侧滑学生列表
    private ListView lvLeft;

    private static Context mContext;// 上下文
    private View rootView;// 表示整个Fragment的控件

    private MapView mapView;// 地图显示控件
    private AMap aMap;// 相当于地图的控制器
    private ImageButton ibShowPath;
    private RelativeLayout rlOffline;
    private TextView tvLocation;
    private TextView tvTypeAndTime;
    private ImageButton goThere;// 导航按钮
    private LinearLayout llSelectCard;// 上方可以选择不同的学生卡按钮
    private ImageButton ibMyPosition;
    private ImageButton ibMapMagnify;
    private ImageButton ibMapReduce;
    private ImageButton ibLocate;
    private ImageButton ibFence;
    private ImageButton ibElectircQuantity;
    private TextView tvOffline;
    private PopupWindow popupWindow;
    private View popupView;
    //private ListView lvPopupView;
    private TextView tvSelectCard;

    private boolean isMyLocationChecked = false;
    private static boolean isHomePageChanged;
    public static int oldPosition = 0;
    // 接口相关
    private String jsonString;
    private Message msg;
    private String lat;
    private String lng;
    private String loc;
    private Double currentLat;
    private Double currentLng;
    private Result result;// 平安卡位置信息JAVABEAN
    private String resultString;
    private List<LoginResult> mResult;

    private SharedPreferences sp;
    private Editor editor;

    private LocationManagerProxy mLocationManagerProxy; // 定位相关
    private UiSettings mUiSettings;
    private Marker currentPositonMarker;
    private Marker marker;// 地图Marker位置显示
    private Circle circle;// Marker周围画个圈

    private DrawerLayout dl;
    private View left;
    private ImageButton ibMenu;

    private MyLoadingDialog dialog;

    public HomePageFragment() {

    }

    public HomePageFragment(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public static HomePageFragment getInstatnce(Context mContext) {
        HomePageFragment homePageFragment = new HomePageFragment();
        HomePageFragment.mContext = mContext;
        return homePageFragment;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            switch (msg.what) {
                case Consts.MESSAGE_SUCCESS:
				dialog.dismiss();
                    rlOffline.setVisibility(View.INVISIBLE);
                    tvSelectCard.setText(mResult.get(oldPosition).getUserName());
                    if (mResult.size() == 0) {
                        Toast.makeText(mContext, "位置信息暂时为空", Toast.LENGTH_SHORT).show();
                        rlOffline.setVisibility(View.VISIBLE);
                    } else {
                        initPopupWindow();
                        addMarkersToMap(result);

                        rlOffline.setVisibility(View.INVISIBLE);
                        goThere.setVisibility(View.VISIBLE);
                    }
                    break;
                case Consts.MESSAGE_FAIL:
				dialog.dismiss();
                    tvOffline.setText("获取位置信息失败,请检查网络...");
                    rlOffline.setVisibility(View.VISIBLE);
                    break;
                case 90016:
                    if (TextUtils.isEmpty(resultString)) {
                        Toast.makeText(mContext, "查询电量出错", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "当前电量" + resultString + "%", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 10086:
                    Toast.makeText(mContext, "查询电量出错", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private Handler locationHandler = new Handler() {
        public void handleMessage(Message msg) {

            goThere.setVisibility(View.VISIBLE);
            ibMyPosition.setVisibility(View.VISIBLE);
        }

        ;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_homepage, container,
                false);

        try {

            sp = mContext.getSharedPreferences(Consts.CONFIG, Context.MODE_PRIVATE);
            editor = sp.edit();
            i++;
            /**
             * 初始化界面
             */
            initView(savedInstanceState);

            /**
             * 获取上次缓存的位置信息
             */
            getCacheLatLng();

            /**
             * 请求位置信息
             */
            requestCardsInfo();

            /**
             * 获得经纬度
             */
            getLatLng();

        } catch (Exception e) {
            Intent i = new Intent(getActivity(), MainTabActivity.class);
            getActivity().startActivity(i);
            getActivity().finish();
            e.printStackTrace();
            System.out.println("mainaaaaaa  重新载入");
        } finally {
            return rootView;
        }
    }

    /**
     * 获得上次打开软件获得的位置信息 手机没网的时候显示
     */
    public void getCacheLatLng() {

        lat = sp.getString("lat", null);
        lng = sp.getString("lng", null);
        loc = sp.getString("loc", null);
        if (!(TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng) || TextUtils
                .isEmpty(loc))) {
            // 设置当前地图显示为当前位置
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)),
                    14));
        }
    }

    int i = 0;


    /**
     * 获得当前位置经纬度 用于导航去到平安卡显示的位置
     */
    private void getLatLng() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());

        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用destroy()方法
        // 其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, this);

        mLocationManagerProxy.setGpsEnable(false);
    }

    /**
     * 请求平安卡信息
     */
    private void requestCardsInfo() {

        if (isMyLocationChecked) {
            showCurrentPositionMarker();
        }

        showLoadingDialog();

        goThere.setVisibility(View.INVISIBLE);

        if (NetUtils.isNetworkAvailable(mContext)) {
            ExecutorService executorService = MyExecutorService
                    .getExecutorService();
            executorService.execute(new Runnable() {

                @Override
                public void run() {
                    // 先将参数放入List，再对参数进行URL编码
                    List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("CD", "PL"));
                    params.add(new BasicNameValuePair("PC", sp.getString(
                            "username", "")));
                    params.add(new BasicNameValuePair("PW", MD5Utils.MD5(sp
                            .getString("password", ""))));
                    params.add(new BasicNameValuePair("AU", AUUtils.getAU("PL")));
                    try {
                        jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
                                Consts.URL_PATH, params);

                        Gson gson = new Gson();
                        LoginJson mJson = gson.fromJson(jsonString,
                                LoginJson.class);

                        com.xsd.safecardapp.javabean.LoginResult result = gson.fromJson(jsonString, com.xsd.safecardapp.javabean.LoginResult.class);
                        App.getInstance().setmLoginReult(result);

                        mResult = mJson.getResult();
                        i = 1;
                        System.out.println("dasdsdsaghh" + mResult);
                        // System.out.println("jsonStringfsdfd"
                        // + mResult.get(0).getImei());

                        // 保存获得的平安卡信息
                        MainTabActivity.setmResult(mResult);

                        /**
                         * 请求到平安卡信息后 继续请求第x平安卡的位置信息
                         *
                         */
                        if (!mJson.getCode().equals("0")) {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    dismissDialogs();
                                    Intent intent = new Intent(getActivity(),
                                            UserLoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    return;
                                }
                            });

                        }

                        if (mResult != null && mResult.size() > 0) {
                            requestLocationData(mResult.get(oldPosition));

                            App.getInstance().setUsername(sp.getString(
                                    "username", ""));
                            requestHXID(App.getUsername());
                            msg = Message.obtain();
                            msg.what = Consts.MESSAGE_SUCCESS;
                            handler.sendMessage(msg);
                        } else {
                            if(Consts.SHOULD_BIND) {
                                dismissDialogs();
                                Intent intent = new Intent(getActivity(),
                                        AddDeviceActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                dismissDialogs();
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
                    dismissDialogs();
                    Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT)
                            .show();
                }
            });

        }
    }
    public void dismissDialogs(){
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
    private void requestHXID(final String pc) {
        if (NetUtils.isNetworkAvailable(mContext)) {
            ExecutorService executorService = MyExecutorService.getExecutorService();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // 先将参数放入List，再对参数进行URL编码
                    List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("CD", "GETHX"));
                    params.add(new BasicNameValuePair("PC", pc));
                    params.add(new BasicNameValuePair("TY", 2 + ""));
                    params.add(new BasicNameValuePair("AU", AUUtils.getAU("GETHX")));
                    try {
                        String jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
                                Consts.URL_PATH, params);
                        System.out.println("fsdfdfhre" + jsonString);
                        Gson gson = new Gson();
                        final HXJson mJson = gson.fromJson(jsonString, HXJson.class);
                        if (mJson.getCode() == 0 && mJson.getResult() != null) {
                            App.setHxId(mJson.getResult().getHxid());
                            /**
                             * 登陆环信
                             */
                            EMClient.getInstance().login(mJson.getResult().getHxid() + "", "123456", new EMCallBack() {//回调
                                @Override
                                public void onSuccess() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            EMClient.getInstance().groupManager().loadAllGroups();
                                            EMClient.getInstance().chatManager().loadAllConversations();
                                            EMClient.getInstance().chatManager().addMessageListener(msgListener);
                                            EaseUI.getInstance().pushActivity(getActivity());
                                            Log.d("mainaaaaaa", "登录聊天服务器成功！" + mJson.getResult().getHxid());
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(int progress, String status) {
                                    //Empty
                                }

                                @Override
                                public void onError(int code, String message) {
                                    Log.d("mainaaaaaa", "登录聊天服务器失败！" + message);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    EMMessageListener msgListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            for (EMMessage message : messages) {
                // 声音和震动提示有新消息
                EaseUI.getInstance().getNotifier().viberateAndPlayTone(message);
//                badgeView.show();
            }
            Log.d("FingerArt", "onMessageReceived");
            mContext.sendBroadcast(new Intent("change_message_size"));
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //Empty
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //Empty
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //Empty
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //Empty
        }
    };

    /**
     * 请求电量信息信息
     */
    private void requestElectricQuantity(final String imei) {

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
                        jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
                                Consts.URL_PATH, params);

                        System.out.println("heheheaaa" + jsonString);

                        JSONObject ojb = new JSONObject(jsonString);

                        String strcode = ojb.getString("code");
                        resultString = ojb.getString("result");
                        if (strcode.equals("0")) {
                            msg = Message.obtain();
                            msg.what = 90016;

                            handler.sendMessage(msg);
                        } else {
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


    /**
     * 初始化控件
     *
     * @param savedInstanceState
     */
    private void initView(Bundle savedInstanceState) {
        /**
         * AMapV2地图中介绍如何使用mapview显示地图
         */
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        aMap = mapView.getMap();
        aMap.setOnMarkerClickListener(this);
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        ibShowPath = (ImageButton) rootView.findViewById(R.id.ib_show_path);
        ibShowPath.setOnClickListener(this);
        tvLocation = (TextView) rootView.findViewById(R.id.tv_location);
        tvTypeAndTime = (TextView) rootView.findViewById(R.id.tv_type_and_time);
        goThere = (ImageButton) rootView.findViewById(R.id.ib_go_there);
        goThere.setOnClickListener(this);
        llSelectCard = (LinearLayout) rootView
                .findViewById(R.id.ll_select_card);
        llSelectCard.setOnClickListener(this);
        rlOffline = (RelativeLayout) rootView
                .findViewById(R.id.rl_bottom_offline);
        ibMyPosition = (ImageButton) rootView.findViewById(R.id.ib_my_position);
        ibMyPosition.setOnClickListener(this);
        ibMapMagnify = (ImageButton) rootView.findViewById(R.id.ib_map_magnify);
        ibMapMagnify.setOnClickListener(this);
        ibMapReduce = (ImageButton) rootView.findViewById(R.id.ib_map_reduce);
        ibMapReduce.setOnClickListener(this);
        ibElectircQuantity = (ImageButton) rootView.findViewById(R.id.ib_electric_quantity);
        ibElectircQuantity.setOnClickListener(this);
        ibLocate = (ImageButton) rootView.findViewById(R.id.ib_locate);
        ibLocate.setOnClickListener(this);
        ibFence = (ImageButton) rootView.findViewById(R.id.ib_fence);
        ibFence.setOnClickListener(this);
        tvOffline = (TextView) rootView.findViewById(R.id.tv_offline);
        tvSelectCard = (TextView) rootView.findViewById(R.id.tv_select_card);
        dl = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        lvLeft = (ListView) rootView.findViewById(R.id.lv_left);
        ibMenu = (ImageButton) rootView.findViewById(R.id.ib_menu);
        ibMenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dl.openDrawer(Gravity.LEFT);

            }
        });
    }

    /**
     * 获取平安卡位置信息
     *
     * @param mResult
     */
    private void requestLocationData(LoginResult mResult) {
        // 先将参数放入List，再对参数进行URL编码
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("CD", Consts.LOCATION_ORDER));
        params.add(new BasicNameValuePair("ID", mResult.getImei()));
        params.add(new BasicNameValuePair("AU", AUUtils
                .getAU(Consts.LOCATION_ORDER)));
        try {
            String pathJsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
                    Consts.URL_PATH, params);
            Gson gson = new Gson();
            PingAnJson mJson = gson.fromJson(pathJsonString, PingAnJson.class);
            result = mJson.getResult();
            System.out.println("46574defgr" + pathJsonString);
            if (result != null) {
                lat = String.valueOf(result.getLat());
                lng = String.valueOf(result.getLng());
                loc = result.getDesc();
                editor.putString("lat", String.valueOf(result.getLat()));
                editor.putString("lng", String.valueOf(result.getLng()));
                editor.putString("loc", result.getDesc());
                editor.putString("type", result.getType());
                editor.commit();

//				msg = Message.obtain();
//				msg.what = 566;
//				handler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(Result result) {

        if (result != null) {
            tvLocation.setText(result.getDesc());
            tvTypeAndTime.setText(result.getType() + " " + result.getTime());

            aMap.clear();

//			marker = aMap.addMarker(new MarkerOptions()
//					.icon(BitmapDescriptorFactory
//							.fromResource(R.drawable.head_boy))
//					.anchor(0.5f, 0.5f)
//					.position(
//							LatLngUtils.transformFromWGSToGCJ(new LatLng(result
//									.getLat(), result.getLng()))));
            GeoPoint fromGpsToAMap = CoordinateConvert.fromGpsToAMap(result.getLat(), result.getLng());
            LatLng lastLatlng = new LatLng(fromGpsToAMap.getLatitudeE6() * 1.E-6, fromGpsToAMap.getLongitudeE6() * 1.E-6);
            marker = aMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.head_boy))
                    .anchor(0.5f, 0.5f)
                    .position(lastLatlng));

            marker.setObject(oldPosition);

            // 设置当前地图显示为当前位置
//			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLngUtils
//					.transformFromWGSToGCJ(new LatLng(result.getLat(), result
//							.getLng())), 16));
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                    (lastLatlng, 16));

            // 绘制一个圆形
//			circle = aMap.addCircle(new CircleOptions()
//					.center(LatLngUtils.transformFromWGSToGCJ(new LatLng(result
//							.getLat(), result.getLng()))).radius(200)
//					.strokeColor(Color.TRANSPARENT)
//					.fillColor(getResources().getColor(R.color.map_color)).strokeWidth(3));
            circle = aMap.addCircle(new CircleOptions()
                    .center(lastLatlng).radius(200)
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(getResources().getColor(R.color.map_color)).strokeWidth(3));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker == this.marker) {
            Intent intent = new Intent(mContext, UserInfoOfCardActivity.class);
            mContext.startActivity(intent);
        }
        return false;
    }

    private void initPopupWindow() {
//		popupView = View.inflate(mContext, R.layout.popup_select_card, null);
//		lvPopupView = (ListView) popupView.findViewById(R.id.lv_select_card);
//		lvPopupView.setAdapter(new SelectCardAdapter(mContext, mResult));
//		//lvPopupView.setOnItemClickListener(this);
//
//		popupWindow = new PopupWindow(popupView, DensityUtil.dip2px(
//				getActivity(), 150), LayoutParams.WRAP_CONTENT, true);
//		popupWindow.setTouchable(true);
//		popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
//		popupWindow.setOutsideTouchable(true);


        lvLeft.setAdapter(new SelectCardAdapter(mContext, mResult));
        lvLeft.setOnItemClickListener(this);
    }

    private void showPopupSelectCard() {


        if (popupWindow.isShowing()) {
            popupWindow.dismiss();

        } else {
            popupWindow.showAsDropDown(llSelectCard,
                    -(popupWindow.getWidth() - llSelectCard.getWidth()) / 2, 0);

            // popupWindow.showAsDropDown(llSelectCard);
            // popupWindow.showAtLocation(rootView, Gravity.TOP, 0, 0);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            final int position, long id) {


        updatePosition(position);

    }

    private void updatePosition(int position) {
        goThere.setVisibility(View.INVISIBLE);

        //showPopupSelectCard();
        dl.closeDrawers();
        // if(oldPosition == position){
        // return;
        // }
        oldPosition = position;

        if (isMyLocationChecked) {
            showCurrentPositionMarker();
        }

        ExecutorService executorService = MyExecutorService
                .getExecutorService();
        executorService.execute(new Runnable() {

            @Override
            public void run() {

                requestLocationData(mResult.get(oldPosition));
                msg = Message.obtain();
                msg.what = Consts.MESSAGE_SUCCESS;
                i=1;
                handler.sendMessage(msg);

            }
        });
    }

    private void showLoadingDialog() {

        dialog = MyLoadingDialog.getMyLoadingDialog(
                ((MainTabActivity) getActivity()), R.style.add_dialog);
        dialog.setMessage("数据加载中");
        dialog.show();

    }

    @Override
    public void onClick(View v) {

        Intent intent;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.ib_show_path:
                intent = new Intent(mContext, ShowPathActivity.class);
                bundle = new Bundle();
                if (mResult != null
                        && (!TextUtils.isEmpty(mResult.get(oldPosition).getImei()))) {
                    bundle.putString("IMEI", mResult.get(oldPosition).getImei());
                    intent.putExtra(Consts.INTENT_DATA, bundle);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "请检查网络", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ib_go_there:
                intent = new Intent(mContext, GuideActivity.class);
                bundle = new Bundle();
                GeoPoint fromGpsToAMap = CoordinateConvert.fromGpsToAMap(result.getLat(), result.getLng());
                LatLng lastLatlng = new LatLng(fromGpsToAMap.getLatitudeE6() * 1.E-6, fromGpsToAMap.getLongitudeE6() * 1.E-6);
                //LatLng latlng = LatLngUtils.transformFromWGSToGCJ(new LatLng(result.getLat(), result.getLng()));
                bundle.putDouble("lat", lastLatlng.latitude);
                bundle.putDouble("lng", lastLatlng.longitude);
                bundle.putDouble("currentLat", currentLat);
                bundle.putDouble("currentLng", currentLng);
                intent.putExtra(Consts.INTENT_DATA, bundle);
                mContext.startActivity(intent);
                break;
            case R.id.ll_select_card:
//			if ((!NetUtils.isNetworkAvailable(mContext)) || mResult == null) {
//				requestCardsInfo();
//				return;
//			}
//			showPopupSelectCard();
                break;
            case R.id.ib_my_position:

                showCurrentPositionMarker();

                break;
            case R.id.ib_map_magnify:

                //requestElectricQuantity(mResult.get(oldPosition).getImei());

                aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom + 1));

                break;

            case R.id.ib_map_reduce:

                aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom - 1));

                break;

            case R.id.ib_locate:

                getLatLng();
                requestCardsInfo();

                break;

            case R.id.ib_electric_quantity:
                requestElectricQuantity(mResult.get(oldPosition).getImei());
                break;

            case R.id.ib_fence:

                Intent i = new Intent(getActivity(), SafeAreaActivity.class);
                startActivity(i);

                break;

            default:
                break;
        }

    }

    /**
     * 用户自己当前位置信息
     */
    public void showCurrentPositionMarker() {
        isMyLocationChecked = !isMyLocationChecked;
        if (isMyLocationChecked) {
            // 在手机上显示用户当前位置信息
            ibMyPosition.setImageResource(R.drawable.btn_phone);
            currentPositonMarker = aMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.head_girl))
                    .anchor(0.5f, 0.5f)
                    .position(new LatLng(currentLat, currentLng)));
        } else {
            // 取消当前位置信息显示
            //ibMyPosition.setImageResource(R.drawable.ic_phone_location_closed);
            if (currentPositonMarker != null) {
                currentPositonMarker.destroy();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null
                && amapLocation.getAMapException().getErrorCode() == 0) {
            // 获取位置信息
            currentLat = amapLocation.getLatitude();
            currentLng = amapLocation.getLongitude();
            MainTabActivity
                    .setCurrentLatLng(new LatLng(currentLat, currentLng));
            Message msg = Message.obtain();
            locationHandler.sendMessage(msg);
        }

    }

    ;

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {

        super.onResume();
        mapView.onResume();
        if(lvLeft.getChildAt(oldPosition)!=null)
        updatePosition(oldPosition);
    }

    /**i = 0;
     * 方法必须重写
     */
    @Override
    public void onPause() {

        try {
            super.onPause();
            mapView.onPause();
        } catch (Exception e) {

            getActivity().finish();
            e.printStackTrace();
        }
    }

    /**
     * 用户有机会保存某些非永久性的数据
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            mapView.onDestroy();
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
