//package online.hualin.flymsg.injector.component;
//
//
//import android.app.Application;
//
//
//import javax.inject.Singleton;
//
//import dagger.Component;
//import online.hualin.flymsg.imgaeloader.IImageLoader;
//import online.hualin.flymsg.injector.module.ApiModule;
//import online.hualin.flymsg.injector.module.AppModule;
//import online.hualin.flymsg.injector.module.ImageLoaderModule;
//import online.hualin.flymsg.injector.module.SharedPreferencesModule;
//import online.hualin.flymsg.model.Api;
//import online.hualin.flymsg.util.SharedPreferencesManager;
//
///**
// * 项目名称：
// * 类描述：TODO
// * 创建人：KingJA
// * 创建时间：2016/6/13 9:42
// * 修改备注：
// */
//@Singleton
//@Component(modules = {ApiModule.class, AppModule.class, SharedPreferencesModule.class,ImageLoaderModule.class})
//public interface AppComponent {
//    Api getApi();
//    SharedPreferencesManager getSharedPreferencesManager();
//    Application getApplication();
//    IImageLoader getImageLoader();
//}
