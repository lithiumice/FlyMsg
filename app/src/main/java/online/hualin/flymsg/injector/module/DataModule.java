package online.hualin.flymsg.injector.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 项目名称：
 * 类描述：数据Module 包括sp，数据库管理
 * 创建人：KingJA
 * 创建时间：2016/6/13 9:50
 * 修改备注：
 */
@Module
public class DataModule {
    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getSharedPreferences("xxx", Context.MODE_PRIVATE);
    }
}
