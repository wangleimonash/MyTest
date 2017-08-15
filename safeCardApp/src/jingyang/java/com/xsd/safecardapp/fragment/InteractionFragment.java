package com.xsd.safecardapp.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMClient;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.ContactsActivity;
import com.xsd.safecardapp.activity.DailyPerformanceActivity;
import com.xsd.safecardapp.activity.JobActivity;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.activity.ScheduleActivity;

public class InteractionFragment extends Fragment {

    private View rootView;
    private RelativeLayout rlSchedule;
    private RelativeLayout rlEvaluate;
    private RelativeLayout rlChat;
    private RelativeLayout rlList;
    private RelativeLayout rlJob;
    public static ImageView ivPoint,ivPointHomeWork;
    public boolean hasRead = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_interaction, null);
        rlSchedule = (RelativeLayout) rootView.findViewById(R.id.rl_schedule);
        rlEvaluate = (RelativeLayout) rootView.findViewById(R.id.rl_evaluate);
        rlChat = (RelativeLayout) rootView.findViewById(R.id.rl_chat);
        rlList = (RelativeLayout) rootView.findViewById(R.id.rl_list);
        rlJob = (RelativeLayout) rootView.findViewById(R.id.rl_job);
        ivPoint = (ImageView) rootView.findViewById(R.id.iv_point);
        ivPointHomeWork = (ImageView) rootView.findViewById(R.id.iv_point_homework);
        rlSchedule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(getActivity(), ScheduleActivity.class));
            }
        });
        rlEvaluate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(getActivity(), DailyPerformanceActivity.class));
            }
        });
        rlChat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(getActivity(), ContactsActivity.class));
                ivPoint.setVisibility(View.INVISIBLE);
                hasRead = true;
            }
        });
        rlList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(getActivity(), com.xsd.safecardapp.activity.ListActivity.class));
            }
        });
        rlJob.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(getActivity(), JobActivity.class));
                ivPointHomeWork.setVisibility(View.INVISIBLE);
                if(changeIvListener!=null){
                    changeIvListener.change(false);
                }
            }
        });
        getActivity().registerReceiver(receivedMsg, new IntentFilter("change_message_size"));
        getActivity().registerReceiver(homeWork,new IntentFilter("ILOVEDOTA"));
        return rootView;
    }

    @Override
    public void onStart() {
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (unreadMsgsCount > 0) {
            ivPoint.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }

    BroadcastReceiver receivedMsg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ivPoint.setVisibility(View.VISIBLE);

        }
    };

    BroadcastReceiver homeWork = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ivPointHomeWork.setVisibility(View.VISIBLE);
//            hasRead = false;
//            ((MainTabActivity)getActivity()).showPoint(true,0,0+"",true);
            if(changeIvListener!=null){
                changeIvListener.change(true);
            }
        }
    };
    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(receivedMsg);
            getActivity().unregisterReceiver(homeWork);
        }catch (Exception e){

        }
        super.onDestroy();
    }

    public interface ChangeIvListener{
         void change(boolean show);
    }
    public ChangeIvListener changeIvListener;
    public void setOnChangeIvListener(ChangeIvListener changeIvListener){
        this.changeIvListener = changeIvListener;
    }
}
