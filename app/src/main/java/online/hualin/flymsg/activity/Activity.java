package online.hualin.flymsg.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramotion.circlemenu.CircleMenuView;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoSession;
import com.speedystone.greendaodemo.db.PoetryDao;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import online.hualin.flymsg.App;
import online.hualin.flymsg.R;
import online.hualin.flymsg.adapter.MainContentAdapter;
import online.hualin.flymsg.data.ChatMessage;
import online.hualin.flymsg.data.PoetryGson;
import online.hualin.flymsg.data.User;
import online.hualin.flymsg.db.Poetry;
import online.hualin.flymsg.fragment.HomeFrag;
import online.hualin.flymsg.net.NetThreadHelper;
import online.hualin.flymsg.utils.IpMessageConst;
import online.hualin.flymsg.utils.NotificationUtils;

import static online.hualin.flymsg.utils.CommonUtils.getLocalIpAddress;
import static online.hualin.flymsg.utils.CommonUtils.isWifiActive;

public class Activity extends BaseActivity implements OnClickListener
        , NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "BaseActivity";
    public static String hostIp;
    public List<User> mUserList = new ArrayList<>();
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
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private HomeFrag homeFragment;
    private TextView mainTitle;
    private Switch aSwitch;
    private HomeFrag homeFrag;
    private TextView poetryTitle;
    private String url = "http://47.102.85.59:8000/api/poetry/";
    private String[] tabTitiles = new String[]{"设备", "历史", "文件"};
    private int[] pics = {R.drawable.ic_devices_black_24dp, R.drawable.ic_card_membership_black_24dp, R.drawable.ic_file};
    private CircleMenuView circleMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        EventBus.getDefault().register(this);

        if (!NotificationUtils.isNotificationEnabled(getApplicationContext())) {
            NotificationUtils.openNotificationSetting();
        }
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (!isWifiActive()) {    //若wifi没有打开，提示
            Snackbar.make(getWindow().getDecorView(), "没有WiFi连接", Snackbar.LENGTH_LONG)
                    .setAction("Get it", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            makeTextLong("没有WiFi连接");
                        }
                    }).show();
        }

        iniNet();
        init();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void iniNet() {

        netThreadHelper = NetThreadHelper.newInstance();
        netThreadHelper.connectSocket();    //开始监听数据
        netThreadHelper.noticeOnline();    //广播上线

    }

    private void init() {
        hostIp = getLocalIpAddress();
        hostMac = getLocalMacAddress();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager, false);
        pagerAdapter = new MainContentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        for (int i = 0; i < tabTitiles.length; i++) {
            tabLayout.getTabAt(i).setCustomView(makeTabView(i));
        }


        poetryTitle = findViewById(R.id.poetry_title);
        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        mainTitle = findViewById(R.id.main_titile);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navView.setOnClickListener(this);
        View headerLayout = navView.inflateHeaderView(R.layout.nav_header);
        ipTextView = headerLayout.findViewById(R.id.mymood);
        ipTextView.setText(hostIp);    //设置IP

        aSwitch = headerLayout.findViewById(R.id.switch_online);
        aSwitch.setOnClickListener(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        ViewGroup.LayoutParams params = navView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels * 1 / 2;
        navView.setLayoutParams(params);
        Log.d(TAG, "设备宽度:" + getResources().getDisplayMetrics().widthPixels + "");

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.my_home);
        navView.setNavigationItemSelectedListener(this);


        sendRequestOkhttp();

    }


    public void refreshUserAndView() {
        netThreadHelper.refreshUsers();
        refreshUserList();
    }

    public void refreshUserList() {
        //清空数据
        mUserList.clear();

        Map<String, User> currentUsers = new HashMap<String, User>();
        currentUsers.putAll(netThreadHelper.getUsers());
        Queue<ChatMessage> msgQueue = netThreadHelper.getReceiveMsgQueue();
        Map<String, Integer> ip2Msg = new HashMap<String, Integer>();    //IP地址与未收消息个数的map
        Map<String, String> msg2Ip = new HashMap<>(); //get latest msg

        //遍历消息队列，填充ip2Msg
        Iterator<ChatMessage> it = msgQueue.iterator();
        while (it.hasNext()) {
            ChatMessage chatMsg = it.next();
            String ip = chatMsg.getSenderIp();    //得到消息发送者IP

            String tmpStr = chatMsg.getMsg();
            Integer tempInt = ip2Msg.get(ip);

            msg2Ip.put(ip, tmpStr); //put as latest msg

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

            if (msg2Ip.get(user.getIp()) != null) {
                user.setLastestMsg(msg2Ip.get(user.getIp()));
            }

            //设置每个在线用户对应的未收消息个数
            if (ip2Msg.get(user.getIp()) == null) {
                user.setMsgCount(0);
            } else {
                user.setMsgCount(ip2Msg.get(user.getIp()));
            }
            mUserList.add(user);
        }

        mainTitle.setText("当前在线" + currentUsers.size() + "个用户");
        Log.d(TAG, "userlist:" + mUserList.size());
//                EventBus.getDefault().post(mUserList);
    }

    private View makeTabView(int position) {
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_icon, null);
        TextView textView = tabView.findViewById(R.id.tab_text);
        ImageView imageView = tabView.findViewById(R.id.tab_image);
        textView.setText(tabTitiles[position]);
        imageView.setImageResource(pics[position]);

        return tabView;
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public void sendRequestOkhttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://47.102.85.59:8000/api/poetry/";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String retrunStr = response.body().string();
                    Gson gson = new Gson();
                    PoetryGson poetryGson= gson.fromJson(retrunStr, new TypeToken<PoetryGson>() {
                    }.getType());

                    DaoSession daoSession = ((App) getApplication()).getDaoSession();
                    PoetryDao poetryDao=daoSession.getPoetryDao();
                    Poetry poetryOne=new Poetry();
                    poetryOne.setAuthor(poetryGson.getAuthor());
                    poetryOne.setTitle(poetryGson.getTitle());
                    poetryOne.setContent(poetryGson.getContent());
                    poetryDao.insert(poetryOne);

                    Log.d(TAG, poetryGson.getContent());

                    Activity.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            poetryTitle.setText(poetryGson.getContent());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

//    public void sendRequestOkhttp() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url(url)
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    String retrunStr=response.body().string();
//                    Log.d(TAG,retrunStr);
//                    EventBus.getDefault().post(retrunStr);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        });
//    }
//
//    @Subscribe
//    public void changePoetry(String poetry){
//        Gson gson=new Gson();
//        Poetry poetry1=gson.fromJson(poetry,new TypeToken<Poetry>(){}.getType());
//        Log.d(TAG,poetry1.getContent());
//        poetryTitle.setText(poetry1.getContent());
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.my_home:
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
                closeDrawerAndReset();
                break;
            case R.id.action_qq:
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
            case R.id.intro:
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
                refreshUserList();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                makeTextLong("exit");
                exit();
            case KeyEvent.KEYCODE_HOME:
                globalToast("home");
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
            case R.id.switch_online:
                if (aSwitch.isChecked())
                    netThreadHelper.noticeOffline();
                else
                    netThreadHelper.noticeOnline();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}