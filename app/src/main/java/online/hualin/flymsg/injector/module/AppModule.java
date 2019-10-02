package online.hualin.flymsg.injector.module;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * 项目名称：
 * 类描述：TODO
 * 创建人：KingJA
 * 创建时间：2016/6/13 9:48
 * 修改备注：
 */
@Module
public class AppModule {
    private Application application;

    public AppModule(Application application) {
        this.application=application;
    }

    @Provides
    public Application provideApplication() {
        return application;
    }
}
