package online.hualin.flymsg.imgaeloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Description：TODO
 * Create Time：2017/3/9 11:08
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class ImageLoader implements IImageLoader {
    private ImageLoader() {
        imageLoader=new GlideLoader();
    }
    private static ImageLoader mGlideLoader;
    private static IImageLoader imageLoader;

    @Override
    public void loadImage(Context context, String url, int resourceId, ImageView view) {
        imageLoader.loadImage(context, url, resourceId, view);
    }

    public static ImageLoader getDefault() {
        if (mGlideLoader == null) {
            synchronized (ImageLoader.class) {
                if (mGlideLoader == null) {
                    mGlideLoader = new ImageLoader();
                }
            }
        }
        return mGlideLoader;
    }
}
