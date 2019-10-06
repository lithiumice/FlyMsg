package online.hualin.flymsg.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import online.hualin.flymsg.R;
import online.hualin.flymsg.adapter.UserAdapter;
import online.hualin.flymsg.data.User;

import static online.hualin.flymsg.App.getContext;

public class ShareToActivity extends BaseActivity {
    public static String hostIp;
    public List<User> mUserList = new ArrayList<>();
    private Toolbar toolbar;
    private TextView mainTitle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String shareText;
    private String sharePath;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to);
        handleShare();

//        EventBus.getDefault().register(this);
//        EventBus.getDefault().post("refreshUserList");

        initView();

    }

    @Override
    public void processMessage(Message msg) {

    }

    @Subscribe
    public void setUserList(List<User> userList) {
        mUserList = userList;
        adapter.notifyDataSetChanged();
    }

    private void handleShare() {
        Intent intent = getIntent();
        Log.d("share to",intent.toString());
        String action = intent.getAction();
        String type = intent.getType();

        Log.d("share",type);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Log.d("share", sharedText);
                if (sharedText != null) {
                    this.shareText = sharedText;
                }
            } else if (type.startsWith("image/")) {
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Uri uri=intent.getData();
//                Log.d("share",intent.getDataString());

//                String filePath = getRealPathFromUri(getContext(),uri);
//                Log.d("share", filePath);
//                this.sharePath = filePath;
            }
        }


//        else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
//            if (type.startsWith("image/")) {
//            }
//        }
    }


    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        mainTitle = findViewById(R.id.main_titile);
        setToolbar(toolbar, 1);
        recyclerView = findViewById(R.id.main_recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
//                    EventBus.getDefault().post("refreshUserList");
                    swipeRefreshLayout.setRefreshing(false);
                }
        );
        adapter = new UserAdapter(this, mUserList, shareText,sharePath);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void finish() {

        super.finish();
        netThreadHelper.noticeOffline();    //通知下线
        netThreadHelper.disconnectSocket(); //停止监听

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                makeTextLong("退出");
                exit();
            case KeyEvent.KEYCODE_HOME:
                globalToast("后台运行");
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }
}