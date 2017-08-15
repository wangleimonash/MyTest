package com.xsd.safecardapp.activity;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.LoginJson;
import com.xsd.safecardapp.javabean.LoginJson.LoginResult;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.MD5Utils;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.SPUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 用户登录信息类，用sharedprefrences保存用户登录信息；
 */
public class UserLoginActivity extends Activity {

    private EditText etUserName;
    private EditText etPassword;
    private static boolean isRemPassword;
    private static boolean isAutoLogin;
    private CheckBox cbRememberPassword;
    private CheckBox cbAutoLogin;
    private static String username;
    private static String password;
    private static MyLoadingDialog dialog; // 加载中对话框

    private ExecutorService executorService;
    private Message message;
    private static String resultCode;
    private static List<LoginResult> mResult;
    private MyHandler handler = new MyHandler(this);

    private SharedPreferences sp;
    private static Editor editor;

    static class MyHandler extends Handler {

        private final WeakReference<UserLoginActivity> mActivity;

        public MyHandler(UserLoginActivity activity) {
            mActivity = new WeakReference<UserLoginActivity>(activity);
        }

        public void handleMessage(Message msg) {

            UserLoginActivity activity = mActivity.get();

            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

            switch (msg.what) {
                case Consts.MESSAGE_SUCCESS:

                    if ("0".equals(resultCode)) {
                        /**
                         * 登录成功后获得用户信息
                         * @return
                         */
                        editor.putString(Consts.USER_NAME, username);
                        editor.putString(Consts.PASSWORD, password);
                        editor.putBoolean(Consts.IS_REM_PASSWORD,
                                true);
                        editor.putBoolean(Consts.IS_AUTO_LOGIN,
                                true);
                        editor.commit();

                        Toast.makeText(activity, "登录成功",
                                Toast.LENGTH_SHORT).show();

                        if (mResult != null && mResult.size() > 0) {
                            /**
                             * 进入主页
                             * @return
                             */
                            Intent intent = new Intent(activity,
                                    MainTabActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            /**
                             * 进入添加设备页面
                             * @return
                             */
                            if(Consts.SHOULD_BIND) {
                                Intent intent = new Intent(activity,
                                        AddDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                intent.putExtra("DOTA", bundle);
                                activity.startActivity(intent);
                                activity.finish();
                            }else{
                                Intent intent = new Intent(activity,
                                        MainTabActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        }
                    } else if ("2".equals(resultCode)) {
                        Toast.makeText(activity, "账号尚未注册",
                                Toast.LENGTH_SHORT).show();
                    } else if ("12".equals(resultCode)) {
                        Toast.makeText(activity, "账号密码错误",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "服务器正在紧急修复",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Consts.MESSAGE_FAIL:
                    Toast.makeText(activity, "登录失败，请检查网络",
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

        ;
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        sp = getSharedPreferences(Consts.CONFIG, MODE_PRIVATE);
        editor = sp.edit();

        initView();
        /**
         * 检查登录信息
         * @return
         */
        checkLoginInfo();

    }

    private void checkLoginInfo() {
        if (sp.getBoolean(Consts.IS_REM_PASSWORD, false)) {
            cbRememberPassword.setChecked(true);
            username = sp.getString("username", "");
            password = sp.getString("password", "");
            etUserName.setText(username);
            etPassword.setText(password);
        }
    }

    private void initView() {
        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etUserName.setText(getPhoneNumber());
        cbRememberPassword = (CheckBox) findViewById(R.id.cb_remember_password);
        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
    }

    public void userLogin(View v) {
        username = etUserName.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        isRemPassword = cbRememberPassword.isChecked();
        isAutoLogin = cbAutoLogin.isChecked();

        if (username.length() != 11 || password.length() < 6) {
            Toast.makeText(this, "请检查用户信息", Toast.LENGTH_SHORT).show();
            return;
        } else {
            /**
             * 请求登录数据
             * @return
             */
            requestLoginData(username, password);
        }
    }

    public void requestLoginData(final String username, final String password) {
        if (NetUtils.isNetworkAvailable(this)) {

            showLoadingDialog();// 显示加载中对话框

            executorService = MyExecutorService.getExecutorService();

            executorService.execute(new Runnable() {

                @Override
                public void run() {
                    // 先将参数放入List，再对参数进行URL编码
                    List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("CD", "PL"));
                    params.add(new BasicNameValuePair("PC", username));
                    params.add(new BasicNameValuePair("PW", MD5Utils
                            .MD5(password)));
                    params.add(new BasicNameValuePair("AU", AUUtils.getAU("PL")));

                    try {

                        String jsonString = MyHttpSend.httpSend(
                                MyHttpSend.TYPE_GET, Consts.URL_PATH, params);

                        Gson gson = new Gson();
                        LoginJson mJson = gson.fromJson(jsonString,
                                LoginJson.class);

                        mResult = mJson.getResult();
                        resultCode = mJson.getCode();

                        if (!TextUtils.isEmpty(resultCode)) {
                            message = Message.obtain();
                            message.what = Consts.MESSAGE_SUCCESS;
                            handler.sendMessage(message);
                            SPUtils.putString(UserLoginActivity.this, "userinfosss", jsonString);
                        } else {
                            message = Message.obtain();
                            message.what = Consts.MESSAGE_FAIL;
                            handler.sendMessage(message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        message = Message.obtain();
                        message.what = Consts.MESSAGE_FAIL;
                        handler.sendMessage(message);
                    }
                }
            });

        } else {
            message = Message.obtain();
            message.what = Consts.MESSAGE_FAIL;
            handler.sendMessage(message);
        }
    }

    public void userRegister(View v) {
        Intent intent = new Intent(this, UserRegister.class);
        startActivityForResult(intent, Consts.RESULT_OK);
    }

    public void forgetPassword(View v) {
        Intent intent = new Intent(this, ForgetPassWordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Consts.RESULT_OK) {
            requestLoginData(sp.getString("username", ""),
                    sp.getString("password", ""));
        }
    }

    private void showLoadingDialog() {
        dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
        dialog.setMessage("数据加载中");
        dialog.show();
    }

    /**
     * 获取手机本机号码
     *
     * @return
     */
    private String getPhoneNumber() {
        // 获取手机号码
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        String tel = tm.getLine1Number();// 获取本机号码
        if (!TextUtils.isEmpty(tel)) {
            return tel.substring(3);
        }
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
