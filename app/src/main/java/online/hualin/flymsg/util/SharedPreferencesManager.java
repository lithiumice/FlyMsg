package online.hualin.flymsg.util;

/**
 * Description：TODO
 * Create Time：2016/8/15 13:51
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class SharedPreferencesManager {
    private SharedPreferencesIO mSharedPreferencesIO;

    public SharedPreferencesManager(SharedPreferencesIO mSharedPreferencesIO) {
        this.mSharedPreferencesIO = mSharedPreferencesIO;
    }


    private static final String TOKEN = "TOKEN";
    private static final String NAME = "NAME";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String EMPTY_STRING = "";

    /*================================GET================================*/
    public String getName() {
        return (String) mSharedPreferencesIO.get(NAME, EMPTY_STRING);
    }

    public long getAccountId() {
        return (Long) mSharedPreferencesIO.get(ACCOUNT_ID, 0L);
    }

    public String getToken() {
        return (String) mSharedPreferencesIO.get(TOKEN, EMPTY_STRING);
    }



    /*================================PUT================================*/

    public void putName(String name) {
        mSharedPreferencesIO.put(NAME, name);
    }

    public void putToken(String token) {
        mSharedPreferencesIO.put(TOKEN, token);
    }

    public void putAccountId(long accountId) {
        mSharedPreferencesIO.put(ACCOUNT_ID, accountId);
    }


}
