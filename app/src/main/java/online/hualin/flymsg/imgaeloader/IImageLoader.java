package online.hualin.flymsg.imgaeloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Description：TODO
 * Create Time：2017/3/9 11:05
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public interface IImageLoader {
    void loadImage(Context context, String url, int resourceId, ImageView view);
}
