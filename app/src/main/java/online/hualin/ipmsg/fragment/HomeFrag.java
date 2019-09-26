package online.hualin.ipmsg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import online.hualin.ipmsg.R;
import online.hualin.ipmsg.activity.MyFeiGeActivity;
import online.hualin.ipmsg.adapter.UserAdapter;
import online.hualin.ipmsg.data.ChatMessage;
import online.hualin.ipmsg.data.User;
import online.hualin.ipmsg.event.MyEvent;

public class HomeFrag extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
//    private Toolbar toolbar;
//    private myFeiGeActivity.getNetHelper() myFeiGeActivity.getNetHelper();
    public Handler mHandler;
    private MyFeiGeActivity myFeiGeActivity;
    private View view;
    private UserAdapter adapter;
    private List<User> mUserList = new ArrayList<>();

//    public static HomeFrag newInstance(int page){
//        Bundle args = new Bundle();
//        args.putInt("page", page);
//        HomeFrag fragment = new HomeFrag();
//        fragment.setArguments(args);
//        return fragment;
//    }

//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof MyFeiGeActivity){
//            mHandler=((MyFeiGeActivity)context).mHandler;
//        }
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void refreshAdapter(MyEvent event){
//        adapter.notifyDataSetChanged();
//    }

    @Subscribe
    public void setUserList(List<User> userList){
        mUserList=userList;
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        myFeiGeActivity=(MyFeiGeActivity) getActivity();
//        myFeiGeActivity.tranMsg(0);
        adapter=new UserAdapter(getContext(),mUserList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myFeiGeActivity.refreshUserAndView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        view=(LinearLayout)inflater.inflate(R.layout.main_frag,container,false);
        recyclerView = view.findViewById(R.id.recycle_view);

        return view;
    }


    public void makeTextShort(String text) {
        Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).show();
    }

    public void makeTextLong(String text) {
        Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
