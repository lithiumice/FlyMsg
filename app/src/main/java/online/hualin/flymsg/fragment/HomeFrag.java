package online.hualin.flymsg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import online.hualin.flymsg.R;
import online.hualin.flymsg.activity.Activity;
import online.hualin.flymsg.adapter.UserAdapter;
import online.hualin.flymsg.data.User;

public class HomeFrag extends Fragment {
    public static final String TAG = "HomeFrag";
    public static String[] avatars = {"R.drawable.ic_1", "R.drawable.ic_2", "R.drawable.ic_3"};
    public Handler mHandler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Activity myFeiGeActivity;
    private View view;
    private UserAdapter adapter;
    private List<User> mUserList = new ArrayList<>();
    private Context context;

    @Subscribe
    public void setUserList(List<User> userList) {
        mUserList = userList;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Log.d(TAG, "create!");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated!");

//        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        myFeiGeActivity.refreshUserList();
        this.mUserList = myFeiGeActivity.mUserList;
        adapter = new UserAdapter(myFeiGeActivity, mUserList);
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "start!");
        Log.d(TAG, "userlist!" + this.mUserList.size());
        Log.d(TAG, "adapter:" + adapter.getItemCount());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        adapter = null;
        mUserList = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (LinearLayout) inflater.inflate(R.layout.main_frag, container, false);
        recyclerView = view.findViewById(R.id.recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        myFeiGeActivity = (Activity) getActivity();


        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
                    myFeiGeActivity.refreshUserAndView();
                    myFeiGeActivity.sendRequestOkhttp();

                    swipeRefreshLayout.setRefreshing(false);
                    makeTextShort("刷新成功");
                }
        );
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
