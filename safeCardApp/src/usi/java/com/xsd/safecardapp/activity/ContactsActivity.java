package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.App;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.javabean.ContactsBean;
import com.xsd.safecardapp.javabean.ContactsBean.ResultEntity;
import com.xsd.safecardapp.javabean.HXJson;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 联系人界面
 */
public class ContactsActivity extends Activity {

    private RelativeLayout rlContent;
    private FragmentManager fm;
    private FragmentTransaction ft;

    private EaseContactListFragment contactListFragment;

    private ListView lvContacts;
    private List<ResultEntity> result;

    private String jsonString;
    private Message msg;
    private MyLoadingDialog dialog;
    private HXJson mJson;

    private void showLoadingDialog() {
        dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
        dialog.setMessage("数据加载中");
        dialog.show();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            if (dialog != null && dialog.isShowing())
                dialog.dismiss();

            switch (msg.what) {
                case Consts.MESSAGE_SUCCESS:
                    lvContacts.setAdapter(new MyAdapter());
                    break;

                case Consts.MESSAGE_FAIL:
                    Toast.makeText(ContactsActivity.this, "服务器出错啦", 0).show();
                    break;


                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        lvContacts = (ListView) findViewById(R.id.lv_contacts);
        lvContacts.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Intent i = new Intent(ContactsActivity.this, ChatActivity.class);
                i.putExtra("DOTA", result.get(position).getThxid());
                i.putExtra("NAME", result.get(position).getTname());
                startActivity(i);
                view.findViewById(R.id.tv_msg_size).setVisibility(View.INVISIBLE);
            }
        });
        requestWhiteListData(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei());
        registerReceiver(receivedMsg, new IntentFilter("change_message_size"));
    }
    BroadcastReceiver receivedMsg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lvContacts.setAdapter(new MyAdapter());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(receivedMsg);
        }catch (Exception e){

        }
    }

    /**
     * 请求联系人
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
                    params.add(new BasicNameValuePair("CD", "GTXL"));
                    params.add(new BasicNameValuePair("PC", App.getInstance().getUsername()));
                    params.add(new BasicNameValuePair("AU", AUUtils.getAU("GTXL")));

                    try {
                        jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
                                Consts.URL_PATH, params);

                        System.out.println("WANAN" + jsonString);

                        Gson gson = new Gson();
                        ContactsBean bean = gson.fromJson(jsonString, ContactsBean.class);

                        result = bean.getResult();
//						entity = bean.getResult();
                        if (result != null && result.size() > 0) {
                            msg = Message.obtain();
                            msg.what = Consts.MESSAGE_SUCCESS;
                            handler.sendMessageDelayed(msg, 2000);
                        } else {
                            msg = Message.obtain();
                            msg.what = Consts.MESSAGE_FAIL;
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

    private class MyAdapter extends BaseAdapter {
        EMChatManager emChatManager = EMClient.getInstance().chatManager();

        @Override
        public int getCount() {
            return result.size();
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            View v = View.inflate(ContactsActivity.this, R.layout.item_lv_contacts, null);
            TextView tv = (TextView) v.findViewById(R.id.tv);
            TextView tvName = (TextView) v.findViewById(R.id.tv_name);
            TextView tvMsgSize = (TextView) v.findViewById(R.id.tv_msg_size);
            tv.setText("老师:" + result.get(arg0).getTname());
            tvName.setText("班级:" + result.get(arg0).getCname());
            tvMsgSize.setVisibility(View.INVISIBLE);
            try {
                int unreadMsgCount = emChatManager.getConversation(result.get(arg0).getThxid()).getUnreadMsgCount();
                if (unreadMsgCount > 0) {
                    tvMsgSize.setText(unreadMsgCount + "");
                    tvMsgSize.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return v;
        }

    }

    public void finishMyself(View v) {
        finish();
    }

}
