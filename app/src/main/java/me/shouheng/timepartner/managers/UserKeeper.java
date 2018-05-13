package me.shouheng.timepartner.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.shouheng.timepartner.models.entities.user.User;
import me.shouheng.timepartner.utils.TpNet;
import me.shouheng.timepartner.utils.TpString;

/**
 * 本地用户信息管理类 */
public class UserKeeper {

    private static final String TAG = "UserKeeper__";

    private static User user;

    public final static class Keys {
        private final static String KEEPER_FILE_NAME = "account_keeper";
        private final static String ACCOUNT = "account";
        private final static String TOKEN = "token";
    }

    public final static class Login {
        // URL
        public final static String BASE_URL = "http://192.168.137.1:8080/android/";
        public final static String URL_APPENDIX = "login.jsp";
        // 请求参数
        public final static String PARAM_ACCOUNT = "account";
        public final static String PARAM_PASSWORD = "password";
        // 返回参数
        public final static String RET_STATUS = "status";
        public final static String RET_TOKEN = "token";
    }

    public final static class Reg {
        // URL
        public final static String BASE_URL = "http://192.168.137.1:8080/android/";
        public final static String URL_APPENDIX = "register.jsp";
        // 请求参数
        public final static String PARAM_ACCOUNT = "account";
        public final static String PARAM_PASSWORD = "password";
        // 返回参数
        public final static String RET_STATUS = "status";
    }

    public final static class LoginInfo {
        public static final String OFFLINE = "OffLine";                 // 网络没有连接
        public static final String LN_OK = "LoginSuccessfully";         // 登录成功
        public static final String LN_ERR = "LoginFailed";              // 登录失败
        public static final String ERR_NONE_EXIST = "AccountNotFound";  // 账号不存在
        public static final String ERR_WRONG_PSD = "WrongPassword";     // 密码错误
    }

    public final static class RegInfo {
        public static final String OFFLINE = "OffLine";                  // 网络没有连接
        public static final String REG_OK = "RegisterSuccessfully";     // 注册成功
        public static final String REG_ERR = "RegisterFailed";          // 注册失败
        public static final String ERR_PSD_SHORT = "ShortPassword";     // 密码太短 length < 6
        public static final String ERR_PSD_LONG = "LongPassword";       // 密码太长 length > 16
        public static final String ERR_PSD_SIMPLE = "SimplePassword";   // 密码太简单
        public static final String ERR_PSD_ILLEGAL = "IllegalPassword"; // 包含非法字符
        public static final String ERR_ACC_EXISTED = "AccountExisted";  // 邮箱已被注册
        public static final String ERR_WRONG_FORMAT = "WrongFormat";    // 邮箱格式错误
    }

    public static User getUser() {
        return user;
    }

    /**
     * 初始化用户信息
     * @param context 上下文 */
    public static void init(Context context) {
        if (user == null){
            user = UserKeeper.getUser(context);
        }
    }

    /**
     * 强制初始化用户信息 */
    public static void reset(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Keys.KEEPER_FILE_NAME, Context.MODE_PRIVATE);
        user = new User();
        user.setAccount(pref.getString(Keys.ACCOUNT, ""));
        user.setToken(pref.getLong(Keys.TOKEN, 0L));
    }

    /**
     * 解决可以用作文件名的账户名称 */
    public static String getLegalAccount(){
        return TpString.parseChars(UserKeeper.getUser().getAccount());
    }

    public static void keep(Context context, User user){
        SharedPreferences shared = context.getSharedPreferences(Keys.KEEPER_FILE_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(Keys.ACCOUNT, user.getAccount());
        editor.putLong(Keys.TOKEN, user.getToken());
        editor.apply();
    }

    public static User getUser(Context context){
        if (user != null){
            return user;
        }
        SharedPreferences pref = context.getSharedPreferences(Keys.KEEPER_FILE_NAME, Context.MODE_PRIVATE);
        user = new User();
        user.setAccount(pref.getString(Keys.ACCOUNT, ""));
        user.setToken(pref.getLong(Keys.TOKEN, 0L));
        return user;
    }

    public static void clear(Context context){
        SharedPreferences shared = context.getSharedPreferences(Keys.KEEPER_FILE_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = shared.edit();
        editor.clear();
        editor.apply();
    }

    public static void register(Context context, String account, String password, final AccountValidCallback callback){
        if (TpNet.isNetworkAvailable(context)){
            Map<String, String> map = new HashMap<>();
            map.put(Reg.PARAM_ACCOUNT, account);
            map.put(Reg.PARAM_PASSWORD, password);
            String url = Reg.BASE_URL + Reg.URL_APPENDIX;

            TpNet.postRequest(url, map, new TpNet.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString(Reg.RET_STATUS);
                        Log.d(TAG, "onFinish: " + status);
                        if (callback != null){
                            callback.onFinish(status);
                        }
                    } catch (Exception e) {
                        if (callback != null){
                            callback.onFinish(e.getMessage());
                        }
                    }
                }
                @Override
                public void onError(Exception e) {
                    if (callback != null){
                        callback.onFinish(e.getMessage());
                    }
                }
            });
        } else {
            if (callback != null){
                callback.onFinish(RegInfo.OFFLINE);
            }
        }
    }

    public static void login(final Context context, final String email, String password, final AccountValidCallback callback){
        if (TpNet.isNetworkAvailable(context)){
            Map<String, String> map = new HashMap<>();
            map.put(Login.PARAM_ACCOUNT, email);
            map.put(Login.PARAM_PASSWORD, password);
            String url = Login.BASE_URL + Login.URL_APPENDIX;

            TpNet.postRequest(url, map, new TpNet.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString(Login.RET_STATUS);
                        Log.d(TAG, "onFinish: " + "getResult");
                        if (callback != null){
                            if (status.equals(LoginInfo.LN_OK)){
                                Log.d(TAG, "onFinish: " + "ok");
                                long token = jsonObject.getLong(Login.RET_TOKEN);
                                User TPUser = new User();
                                TPUser.setAccount(email);
                                TPUser.setToken(token);
                                UserKeeper.keep(context, TPUser);
                                callback.onFinish(status);
                            } else {
                                Log.d(TAG, "onFinish: " + "failed");
                                callback.onFinish(status);
                            }
                        }
                    } catch (Exception e) {
                        if (callback != null){
                            callback.onFinish(e.getMessage());
                        }
                    }
                }
                @Override
                public void onError(Exception e) {
                    if (callback != null){
                        callback.onFinish(e.getMessage());
                    }
                }
            });
        } else {
            if (callback != null){
                callback.onFinish(LoginInfo.OFFLINE);
            }
        }
    }

    public interface AccountValidCallback{
        void onFinish(String result);
        void onError(Exception e);
    }
}
