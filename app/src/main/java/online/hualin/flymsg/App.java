package online.hualin.flymsg;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.speedystone.greendaodemo.db.ChatHistoryDao;
import com.speedystone.greendaodemo.db.DaoMaster;
import com.speedystone.greendaodemo.db.DaoSession;

import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;

import java.util.concurrent.Executor;

import online.hualin.flymsg.db.ChatHistory;

public class App extends Application {
    private static Context context;
    private static App instance;
    private Handler mHandler;
    private Executor mExecutor;
    private App app;
    private DaoSession daoSession;
    private ChatHistoryDao chatHistoryDao;

    public static Context getContext() {
        return context;
    }
    public ChatHistoryDao getChatHistoryDao(){
        return chatHistoryDao;
    }

    public static App getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mHandler = new Handler();
        mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
        context = getApplicationContext();
        app=(App) getApplication();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();

        daoSession = new DaoMaster(db).newSession();
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
