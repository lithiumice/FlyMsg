package online.hualin.flymsg.imgaeloader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import online.hualin.flymsg.R;
import online.hualin.flymsg.Constant;


/**
 * Description：TODO
 * Create Time：2017/3/9 11:08
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class GlideLoader implements IImageLoader {
    @Override
    public void loadImage(Context context, String url, int resourceId, ImageView view) {
        Glide.with(context)
                .load(url)
//                .centerCrop()
//                .placeholder(resourceId == 0 ? R.drawable.head_default : resourceId)
//                .crossFade()
                .into(view);
    }
}
