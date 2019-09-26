package online.hualin.ipmsg.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import online.hualin.ipmsg.R;
import online.hualin.ipmsg.adapter.MainContentAdapter;
import online.hualin.ipmsg.data.ChatMessage;
import online.hualin.ipmsg.data.User;
import online.hualin.ipmsg.fragment.HomeFrag;
import online.hualin.ipmsg.net.NetThreadHelper;
import online.hualin.ipmsg.utils.IpMessageConst;
import online.hualin.ipmsg.utils.NotificationUtils;

import static online.hualin.ipmsg.utils.utils.getLocalIpAddress;
import static online.hualin.ipmsg.utils.utils.isWifiActive;

public class MyFeiGeActivity extends MyFeiGeBaseActivity implements OnClickListener
        , NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MyFeiGeBaseActivity";
    public static String hostIp;
    private ArrayList<Fragment> tabFragments = new ArrayList<>();
    private TextView ipTextView;
    private ActionBar actionBar;
    //    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
//    @BindView(R.id.toolbar) Toolbar toolbar;
//    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
//    @BindView(R.id.nav_view) NavigationView navView;
//    @BindView(R.id.tab_layout) TabLayout tabLayout;
//    @BindView(R.id.view_pager) ViewPager viewPager;
    private MainContentAdapter pagerAdapter;
    private Toolbar toolbar;
    private String hostMac;
    //    public Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 233) {
//                toolbar.setTitle((String) msg.obj);
//            }
//
//        }
//    };
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private HomeFrag homeFragment;
    //    private UserAdapter adapter;
    private List<User> mUserList = new ArrayList<>();

    //    @Override
//    public void tranMsg(int i) {
//        switch (i){
//            case 0:
//                homeFragment.setAdapter(adapter);
//        }
//    }
//    @Subscribe
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            init_fade();//淡入淡出
        }
        setContentView(R.layout.main);
//        EventBus.getDefault().register(HomeFrag);
//        ButterKnife.bind(this);

        if (!NotificationUtils.isNotificationEnabled(getApplicationContext())) {
            NotificationUtils.openNotificationSetting();
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (!isWifiActive()) {    //若wifi没有打开，提示
            Snackbar.make(getWindow().getDecorView(), getApplicationContext().getString(R.string.no_wifi), Snackbar.LENGTH_LONG)
                    .setAction("Get it", null).show();
        }

        iniNet();
        init();
    }

    private void iniNet() {

        netThreadHelper = NetThreadHelper.newInstance();
        netThreadHelper.connectSocket();    //开始监听数据
        netThreadHelper.noticeOnline();    //广播上线

    }

    public NetThreadHelper getNetHelper() {
        return netThreadHelper;
    }

    private void init() {
        hostIp = getLocalIpAddress();
        hostMac=getLocalMacAddress();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager, false);
        pagerAdapter = new MainContentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navView.setOnClickListener(this);

        View headerLayout = navView.inflateHeaderView(R.layout.nav_header);
        ipTextView = headerLayout.findViewById(R.id.mymood);
        ipTextView.setText(hostIp);    //设置IP

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        toolbar.setTitleMargin(10, 5, 10, 5);
        toolbar.setTitleTextColor(getResources().getColor(R.color.gray));

//        ViewGroup.LayoutParams params = navView.getLayoutParams();
//        params.width = getResources().getDisplayMetrics().widthPixels * 1 / 2;
//        navView.setLayoutParams(params);
//        Log.d(TAG,"设备宽度:"+getResources().getDisplayMetrics().widthPixels+"");

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.my_home);
        navView.setNavigationItemSelectedListener(this);
        refreshUserAndView();

//        fragmentManager=getSupportFragmentManager();
//        homeFragment=(HomeFrag) fragmentManager.findFragmentById(R.id.home_frag);
//        homeFragment.refreshViews();
//        MainContentAdapter  tmpAdapter= (MainContentAdapter)viewPager.getAdapter();
//        homeFragment=(HomeFrag) tmpAdapter.getItem(1);
//        MyEvent myEvent = new MyEvent(adapter);
//        EventBus.getDefault().post(myEvent);
    }

    public void refreshUserAndView(){
        netThreadHelper.refreshUsers();
        refreshViews();
    }

    //    private void refreshViews() {
//
//        Map<String, User> currentUsers = new HashMap<String, User>();
//        currentUsers.putAll(netThreadHelper.getUsers());
//        Queue<ChatMessage> msgQueue = netThreadHelper.getReceiveMsgQueue();
//        Map<String, Integer> ip2Msg = new HashMap<String, Integer>();    //IP地址与未收消息个数的map
//        //遍历消息队列，填充ip2Msg
//        Iterator<ChatMessage> it = msgQueue.iterator();
//        while (it.hasNext()) {
//            ChatMessage chatMsg = it.next();
//            String ip = chatMsg.getSenderIp();    //得到消息发送者IP
//            Integer tempInt = ip2Msg.get(ip);
//            if (tempInt == null) {    //若map中没有IP对应的消息个数,则把IP添加进去,值为1
//                ip2Msg.put(ip, 1);
//            } else {    //若已经有对应ip，则将其值加一
//                ip2Msg.put(ip, ip2Msg.get(ip) + 1);
//            }
//        }
//
//        //更新未读消息数
//        Iterator<String> iterator = currentUsers.keySet().iterator();
//        while (iterator.hasNext()) {
//            User user = currentUsers.get(iterator.next());
//            //设置每个在线用户对应的未收消息个数
//            if (ip2Msg.get(user.getIp()) == null) {
//                user.setMsgCount(0);
//            } else {
//                user.setMsgCount(ip2Msg.get(user.getIp()));
////                user.setPlusMsg();
//            }
//            mUserList.add(user);
//        }
//
//        adapter.notifyDataSetChanged();    //更新ListView
//        toolbar.setTitle("当前在线" + currentUsers.size() + "个用户");
//        makeTextLong("刷新成功");
//
//    }
    public void refreshViews() {
        //清空数据
        mUserList.clear();

        Map<String, User> currentUsers = new HashMap<String, User>();
        currentUsers.putAll(netThreadHelper.getUsers());
        Queue<ChatMessage> msgQueue = netThreadHelper.getReceiveMsgQueue();
        Map<String, Integer> ip2Msg = new HashMap<String, Integer>();    //IP地址与未收消息个数的map
        //遍历消息队列，填充ip2Msg
        Iterator<ChatMessage> it = msgQueue.iterator();
        while (it.hasNext()) {
            ChatMessage chatMsg = it.next();
            String ip = chatMsg.getSenderIp();    //得到消息发送者IP
            Integer tempInt = ip2Msg.get(ip);
            if (tempInt == null) {    //若map中没有IP对应的消息个数,则把IP添加进去,值为1
                ip2Msg.put(ip, 1);
            } else {    //若已经有对应ip，则将其值加一
                ip2Msg.put(ip, ip2Msg.get(ip) + 1);
            }
        }

        //更新未读消息数
        Iterator<String> iterator = currentUsers.keySet().iterator();
        while (iterator.hasNext()) {
            User user = currentUsers.get(iterator.next());
            //设置每个在线用户对应的未收消息个数
            if (ip2Msg.get(user.getIp()) == null) {
                user.setMsgCount(0);
            } else {
                user.setMsgCount(ip2Msg.get(user.getIp()));
            }
            mUserList.add(user);
        }

        toolbar.setTitle("当前在线" + currentUsers.size() + "个用户");
        EventBus.getDefault().post(mUserList);
        makeTextLong("刷新成功");

    }


    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.my_home:
//                ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
//                if (info != null & !info.topActivity.getShortClassName().equals("MyFeiGeActivity")) {
//                    Intent intent2 = new Intent(getApplicationContext(), MyFeiGeActivity.class);
//                    startActivity(intent2);
//                }
                closeDrawerAndReset();


                break;
            case R.id.setting:
                Intent intent3 = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent3);
                closeDrawerAndReset();


                break;
            case R.id.about:
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                closeDrawerAndReset();


                break;
            case R.id.change_skin:
                setTheme(android.R.style.Theme_Black);
                recreate();
//                SkinCompatManager.getInstance().loadSkin("new.skin", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
//                SkinCompatManager.getInstance().restoreDefaultTheme();
                closeDrawerAndReset();


                break;
            case R.id.action_qq:
                Intent intent4 = new Intent(getApplicationContext(), LottieActivity.class);
                startActivity(intent4);
                closeDrawerAndReset();


            default:
                closeDrawerAndReset();
        }

        return true;
    }

    private void closeDrawerAndReset() {
        navView.setCheckedItem(R.id.my_home);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.welcome:
                Intent intent2 = new Intent(this, IntroActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }

    @Override
    public void finish() {

        super.finish();
        netThreadHelper.noticeOffline();    //通知下线
        netThreadHelper.disconnectSocket(); //停止监听

    }

    @Override
    public void processMessage(Message msg) {

        switch (msg.what) {
            case IpMessageConst.IPMSG_BR_ENTRY:
            case IpMessageConst.IPMSG_BR_EXIT:
            case IpMessageConst.IPMSG_ANSENTRY:
            case IpMessageConst.IPMSG_SENDMSG:
                refreshViews();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                makeTextLong("exit");
//                Toast.makeText(getApplicationContext(), "退出", Toast.LENGTH_SHORT);
                exit();
            case KeyEvent.KEYCODE_HOME:
//                Toast.makeText(getApplicationContext(), "在后台运行", Toast.LENGTH_SHORT);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_header:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}