package online.hualin.ipmsg.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import online.hualin.ipmsg.MyApplication;
import online.hualin.ipmsg.R;
import online.hualin.ipmsg.SettingsActivity;
import online.hualin.ipmsg.adapter.UserAdapter;
import online.hualin.ipmsg.data.ChatMessage;
import online.hualin.ipmsg.data.User;
import online.hualin.ipmsg.net.NetThreadHelper;
import online.hualin.ipmsg.utils.IpMessageConst;

import androidx.appcompat.app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import static online.hualin.ipmsg.utils.utils.getLocalIpAddress;
import static online.hualin.ipmsg.utils.utils.isWifiActive;

public class MyFeiGeActivity extends MyFeiGeBaseActivity implements OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static String hostIp;

    private User users = new User("name", "alias", "groupname",
            "ip", "hostname", "mac", 0);

    private UserAdapter adapter;
    private List<User> mUserList = new ArrayList<>();

    private TextView totalUser;
    private FloatingActionButton refreshButton;
    private TextView ipTextView;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (!isWifiActive()) {    //若wifi没有打开，提示
            Snackbar.make(getWindow().getDecorView(), getApplicationContext().getString(R.string.no_wifi), Snackbar.LENGTH_LONG)
                    .setAction("Get it", null).show();
        }

        findViews();
        iniNet();
        mUserList.add(users);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UserAdapter(this, mUserList);
        recyclerView.setAdapter(adapter);

        refreshButton.setOnClickListener(this);
        refreshViews();


    }

    private void iniNet() {

        netThreadHelper = NetThreadHelper.newInstance();
        netThreadHelper.connectSocket();    //开始监听数据
        netThreadHelper.noticeOnline();    //广播上线

    }

    private void findViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navView.inflateHeaderView(R.layout.nav_header);
        refreshButton = (FloatingActionButton) findViewById(R.id.refresh);
        totalUser = (TextView) headerLayout.findViewById(R.id.totalUser);
        ipTextView = (TextView) headerLayout.findViewById(R.id.mymood);
        ipTextView.setText(getLocalIpAddress());    //设置IP

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        ViewGroup.LayoutParams params=navView.getLayoutParams();
        params.width=getResources().getDisplayMetrics().widthPixels*1/2;
        navView.setLayoutParams(params);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
        }
        navView.setCheckedItem(R.id.my_home);
        navView.setNavigationItemSelectedListener(this);
    }

    private void refreshViews() {
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

        //遍历currentUsers,更新strGroups和children
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

        adapter.notifyDataSetChanged();    //更新ExpandableListView
        String countStr = "当前在线" + currentUsers.size() + "个用户";
        totalUser.setText(countStr);    //更新TextView
        Snackbar.make(getWindow().getDecorView(), "刷新成功", Snackbar.LENGTH_SHORT).show();

    }

    //获取本机MAC地址
    public String getLocalMacAddress(){
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.my_home:
                Intent intent2 = new Intent(getApplicationContext(), MyFeiGeActivity.class);
                startActivity(intent2);
                break;
            case R.id.setting:
                Intent intent3 = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent3);
                break;
            case R.id.about:
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
            default:
                mDrawerLayout.closeDrawers();
        }
        return true;
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
                Toast.makeText(getApplicationContext(), "退出", Toast.LENGTH_SHORT);
                exit();
            case KeyEvent.KEYCODE_HOME:
                Toast.makeText(getApplicationContext(), "在后台运行", Toast.LENGTH_SHORT);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
			case R.id.refresh:
				netThreadHelper.refreshUsers();
				refreshViews();
				break;
        }
    }
}