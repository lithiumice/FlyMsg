package online.hualin.flymsg.util;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import online.hualin.flymsg.Constant;

public class SharedPreferencesIO {

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesIO(Application application) {
        sharedPreferences = application.getSharedPreferences(Constant.APPLICATION_NAME,
                Context.MODE_PRIVATE);
    }


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public  void put(String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public  Object get(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();

        if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        }

        return null;
    }
}

