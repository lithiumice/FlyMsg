package online.hualin.flymsg.injector.module;


import dagger.Module;
import dagger.Provides;
import online.hualin.flymsg.imgaeloader.GlideLoader;
import online.hualin.flymsg.imgaeloader.IImageLoader;

/**
 * Description：TODO
 * Create Time：2017/3/9 11:13
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
@Module
public class ImageLoaderModule {
    @Provides
    public IImageLoader provideImageLoader() {
        return new GlideLoader();
    }
}
