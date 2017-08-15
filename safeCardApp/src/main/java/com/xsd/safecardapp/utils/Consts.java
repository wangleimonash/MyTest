package com.xsd.safecardapp.utils;

import com.hysd.usiapp.BuildConfig;

public class Consts {

    public static String USERNAME;

    /**
     * SharedPrefrences相关
     */
    public static final String CONFIG = "config";
    public static final String IS_REM_PASSWORD = "isRememberPassword";
    public static final String IS_AUTO_LOGIN = "isAutoLogin";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    /**
     * Handler捕获的消息类别
     */
    public static final int MESSAGE_SUCCESS = 0x001;//成功
    public static final int MESSAGE_FAIL = 0x001 << 1;//失败
    public static final int MESSAGE_LOADMORE = 0x001 << 2;//加载更多
    public static final int MESSAGE_REFRESH = 0x001 << 3;//刷新页面
    public static final int MESSAGE_EMPTY = 0x001 << 4;//刷新页面
    public static final String APK_TYPE = BuildConfig.APK_TYPE;
    public static final String URL_PATH = BuildConfig.URL_PATH;//服务器地址
    public static final String URL_PATH_HEART = BuildConfig.URL_PATH_HEART;//服务器地址
    public static final String URL_PATH_SHARE_PIC = BuildConfig.URL_PATH_SHARE_PIC;
    public static final String LOCATION_ORDER = "PQ"; //请求位置信息命令字
    public static final String PATH_ORDER = "TL"; //请求轨迹信息命令字
    public static final String SMS_ORDER = "SMS"; //请求轨迹信息命令字
    public static final String SENDER_CODE = "HYSD";//请求各种信息的发送方码

    /**
     * Activity跳转RequestCode标识
     */
    public static final int RESULT_OK = 110;
    /**
     * Activity跳转RequestCode标识
     */
    public static final int SAFEAREA_TO_ADDSAFEAREA = 0x002;//安全区域Activity跳转到添加安全区域
    public static final int SAFEAREA_TO_EDITSAFEAREA = 0x002 << 1;//安全区域Activity跳转到添加安全区域

    /**
     * Activity跳转ResultCode标识
     */
    public static final int FLASH_TO_ADDDEVICE = 0x003;//二维码扫描 回到添加设备界面
    public static final int INPUT_TO_ADDDEVICE = 0x003 << 1;//手动输入 回到添加设备页面

    public static final String INTENT_DATA = "intent_data";//Intent传递数据的标示
    public static final boolean SHOULD_BIND = BuildConfig.SHOULD_BIND;
    /**
     * 添加设备返回标识
     */
    public static final int ADD_DEV_SUCCESS = 0x004;//二维码扫描 回到添加设备界面
    public static final int ADD_DEV_ALREADY = 0x004 << 1;//二维码扫描 回到添加设备界面
    public static final int ADD_DEV_FAIL = 0x004 << 2;//二维码扫描 回到添加设备界面
}
