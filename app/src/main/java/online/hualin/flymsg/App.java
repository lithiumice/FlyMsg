package online.hualin.flymsg;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import androidx.preference.PreferenceManager;

import com.kingja.loadsir.core.LoadSir;
import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoMaster;
import com.speedystone.greendaodemo.db.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.concurrent.Executor;

//import online.hualin.flymsg.injector.component.AppComponent;
//import online.hualin.flymsg.injector.module.ApiModule;
import online.hualin.flymsg.injector.module.AppModule;
import online.hualin.flymsg.injector.module.SharedPreferencesModule;
import online.hualin.flymsg.loadsir.callback.EmptyCallback;
import online.hualin.flymsg.loadsir.callback.LoadingCallback;


public class App extends Application {
    private static Context context;
    private static App instance;
    private static SharedPreferences mSharedPreferences;
    private Handler mHandler;
    private Executor mExecutor;
    private App app;
    private DaoSession daoSession;
    private ChatHistoryDao chatHistoryDao;
    private AppModule appModule;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public static Context getContext() {
        return context;
    }

    public static App getApplication() {
        return instance;
    }

    public ChatHistoryDao getChatHistoryDao() {
        return chatHistoryDao;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        mHandler = new Handler();
        mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
        context = getApplicationContext();
        app = getApplication();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

//        LoadSir.beginBuilder()
//                .addCallback(new EmptyCallback())
//                .addCallback(new LoadingCallback())
//                .setDefaultCallback(EmptyCallback.class)
//                .commit();
//    }

//    private void setupComponent() {
//        appComponent = AppComponent.builder()
//                .apiModule(new ApiModule())
//                .appModule(new AppModule(this))
//                .sharedPreferencesModule(new SharedPreferencesModule())
//                .build();
//        appModule = new AppModule(this);
//    }
    }

    public static SharedPreferences getPref(){
        return pref;
    }

    public static String getDeviceName(){
        return pref.getString("DeviceName","FlyMsg");
    }

    public static String getGroupName(){
        return pref.getString("GroupName","WORKGROUP");
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void runOnUi(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(runnable);
        }
    }

    public void runOnBackground(Runnable runnable) {
        if (mExecutor != null) {
            mExecutor.execute(runnable);
        } else {
            mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
            mExecutor.execute(runnable);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = base;
    }

}
