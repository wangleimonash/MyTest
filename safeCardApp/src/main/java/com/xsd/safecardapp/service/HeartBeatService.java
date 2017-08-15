package com.xsd.safecardapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.asynctask.HeartBeatTask;
import com.xsd.safecardapp.db.dao.MessageDAO;
import com.xsd.safecardapp.db.dao.isReadDAO;
import com.xsd.safecardapp.javabean.MessageBean;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class HeartBeatService extends Service {

    private HeartBeatTask task;
    private NotificationManager mNM;
    private Notification mNotification;
    private isReadDAO dao;
    private MessageDAO msgDAO;
    private Message msg;
    private List<MessageBean> newList = new ArrayList<MessageBean>();
    private String username;
    private Context mContext;


    // 声明按钮  

    // 声明Notification  
    private Notification notification;

    private int firstRunning = 0;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 110) {
                if (msg.arg1 > 0) {
//                    MainTabActivity.addMessageSize(msg.arg1);
                    Intent i = new Intent();
                    i.setAction("ILOVEDOTA");
                    sendBroadcast(i);

                    System.out.println("HB有新的消息");


//					Notification notification = new Notification();
//					notification.icon = R.drawable.ic_launcher;
//					notification.tickerText = "TickerText:您有新消息，请注意查收！";
//					notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//					mNM.notify();
                }
                start();
            }
        }

        ;
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initNotifiManager();
        username = getSharedPreferences("config", MODE_PRIVATE).getString("username", "");
        task = new HeartBeatTask(this);
        dao = new isReadDAO(this);
        msgDAO = new MessageDAO(this);
        start();
    }

    private Runnable heartbeatTask = new Runnable() {

        public void run() {
            try {
                if (NetUtils.isNetworkAvailable(getApplicationContext())) {
                    List<String> tmpList = dao.getList(username);
                    newList.clear();
                    List<MessageBean> messageList = task.getheartbeatData();
                    if (messageList != null) {
                        boolean mustBeSend = false;
                        //List<MessageBean> homeWork=(List<MessageBean>)heartData.get("HOME_WORK");


//						if( isFirst) {
//							isFirst=false;
//							if( homeWork !=null) {
//								for(MessageBean mb:homeWork) {
//									newMessageList.add(StringUtils.getMD5String(mb.getCreateDate()+mb.getContent()));
//								}
//							}
//							if( notice!=null) {
//								for(MessageBean mb:notice) {
//									newMessageList.add(StringUtils.getMD5String(mb.getCreateDate()+mb.getContent()));
//								}
//							}
//							if(!newMessageList.isEmpty()) {
//								FileUtils.writeMessageFileDate("newmessage"+strHistory+ studentId, newMessageList);
//							}
//						}else {
                        int workItem = 0;

                        if (messageList != null && !messageList.isEmpty()) {
                            Log.d("showNotification", "0===homeWork。。" + messageList.size());
                            for (MessageBean mb : messageList) {
                                if (!tmpList.contains(mb.getCreateDate())) {
                                    workItem++;
                                    newList.add(mb);
                                    tmpList.add(mb.getCreateDate());
                                    dao.add(mb.getCreateDate(), username);

                                    //msgDAO.addAll(msgList);
                                    //newMessageList.add(StringUtils.getMD5String(mb.getCreateDate()+mb.getContent()));
                                }
                            }
                            msgDAO.addAll(newList, username);

                            Intent intent = new Intent();
                            intent.putExtra("HAS_NEW_HOME_WORK", true);
                            intent.putExtra("HOME_COUNT", workItem);
                            mustBeSend = true;


                        }
                        if (workItem > 0) {

                            Notification notification = new Notification();
                            notification.icon = R.drawable.ic_launcher;
                            notification.tickerText = "TickerText:您有新消息，请注意查收！";
                            notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
                            mNM.notify();
                        }
                    }


                }
//					showNotification("NOTICE",workItem,noticeItem) ;
//				}else {
//					//网络不通啊 。。
//				}
//				showNotification();
                Log.d("showNotification", "0===showNotification。。");
            } catch (Exception ex) {
                Log.d("heartbeat", "心跳异常。。。");
            } finally {

                handler.postDelayed(heartbeatTask, 60 * 3 * 1000);
            }

        }
    };


    // 初始化通知栏配置
    private void initNotifiManager() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotification = new Notification();
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.tickerText = "有新通知";
        mNotification.icon = R.drawable.ic_launcher;

    }

    public void start() {
        msg = Message.obtain();
        msg.what = 110;

        ExecutorService executorService = MyExecutorService
                .getExecutorService();
        executorService.execute(new Runnable() {

            @Override
            public void run() {

                try {
                    if (NetUtils.isNetworkAvailable(getApplicationContext())) {

                        List<String> tmpList = dao.getList(username);

                        List<MessageBean> newList = new ArrayList<MessageBean>();

                        List<MessageBean> messageList = task.getheartbeatData();

                        if (messageList != null) {
                            boolean mustBeSend = false;
                            //List<MessageBean> homeWork=(List<MessageBean>)heartData.get("HOME_WORK");


//							if( isFirst) {
//								isFirst=false;
//								if( homeWork !=null) {
//									for(MessageBean mb:homeWork) {
//										newMessageList.add(StringUtils.getMD5String(mb.getCreateDate()+mb.getContent()));
//									}
//								}
//								if( notice!=null) {
//									for(MessageBean mb:notice) {
//										newMessageList.add(StringUtils.getMD5String(mb.getCreateDate()+mb.getContent()));
//									}
//								}
//								if(!newMessageList.isEmpty()) {
//									FileUtils.writeMessageFileDate("newmessage"+strHistory+ studentId, newMessageList);
//								}
//							}else {
                            int workItem = 0;

                            if (messageList != null && !messageList.isEmpty()) {

                                for (MessageBean mb : messageList) {

                                    if ((!tmpList.contains(mb.getCreateDate())) && (mb.getType().equals("0") || mb.getType().equals("1"))) {
                                        workItem++;
                                        newList.add(0, mb);
                                        dao.add(mb.getCreateDate(), username);
                                        tmpList.add(mb.getCreateDate());
                                        //msgDAO.addAll(msgList);
                                        //newMessageList.add(StringUtils.getMD5String(mb.getCreateDate()+mb.getContent()));
                                    }
                                }
                                msgDAO.addAll(newList, username);

                                msg.arg1 = newList.size();
                            }
                        }
                    }
                    Log.d("showNotification", "0===showNotification。。");
                } catch (Exception ex) {
                    Log.d("heartbeat", "心跳异常。。。");
                } finally {

                    System.out.println("aaaaa3336" + newList.size());
                    if (firstRunning == 0) {
                        firstRunning++;
                        handler.sendMessageDelayed(msg, 10000);
                    } else {
                        handler.sendMessageDelayed(msg, 60000);
                    }
                    //handler.postDelayed(heartbeatTask, 60*3*1000);
                }


            }
        });
        //handler.postDelayed(heartbeatTask, 0);

    }

    public void stop() {
        handler.removeMessages(110);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
        handler = null;
    }


}
