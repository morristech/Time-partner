package me.shouheng.timepartner.managers;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.shouheng.timepartner.models.business.weather.WeatherRequest;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpNet;
import me.shouheng.timepartner.utils.TpLocation;
import me.shouheng.timepartner.utils.TpWeather;

/**
 * 使用示例：(1).创建实体; (2).添加监听事件; (3).调用start()方法开始.
 WeatherManager manager = new WeatherManager(getApplicationContext());
 manager.setOnGetWeatherListener(new WeatherManager.OnGetWeatherListener() {
 public void onGetWeather(boolean isSucceed) {
        if (isSucceed){ // 成功
            ......
        } else { // 失败
            .....
        }
    }
 });
 manager.loadAllExams();
 */
public class WeatherManager {
    private Context mContext;
    private String normalDistrict;
    private String locationCity;
    private static final String TAG = "WeatherManager.";

    public WeatherManager(Context context){
        this.mContext = context;
    }

    /**
     * 开始获取天气：先定位，再根据定位发送获取天气请求 */
    public void start(){
        // 开始定位监听 广播接收的处理还是处于主线程中 可以使用TOAST
        TpLocation TPLocation = new TpLocation(mContext);
        TPLocation.setOnFinishListener(new TpLocation.OnFinishListener() {
            @Override
            public void onCompleted(BDLocation location) {
                // 定位结束，获取获得的位置信息
                if (location != null) {
                    normalDistrict = location.getDistrict();
                    locationCity = location.getCity();
                    Log.d(TAG, "onCompleted: " + locationCity);
                    if(locationCity == null){
                        TpDisp.showToast(mContext, R.string.mw_failed_2_location_net);
                    } else {
                        String[] str = locationCity.split("市");
                        locationCity = str[0];
                        if("".equals(locationCity)){
                            TpDisp.showToast(mContext, R.string.mw_failed_2_location);
                        } else {
                            Log.d(TAG, "onCompleted: " + locationCity);
                            // 定位成功
                            TpWeather.getInstance(mContext).setLastLocation(
                                    location.getCity() + " " + location.getDistrict());
                            // 获取天气
                            getWeather();
                        }
                    }
                }
            }
        });
        TPLocation.start();
    }

    /**
     * 使用定位得到的位置获取天气信息 */
    private void getWeather(){
        // 从网络当中获取数据
        try {
            locationCity = URLEncoder.encode(locationCity, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WeatherRequest.setCity(locationCity);
        String urlAddress = WeatherRequest.getData();
        // 回调方法仍然处于子线程，需要使用Handler来更新界面的组件
        TpNet.sendUrlRequest(urlAddress, new TpNet.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.d(TAG, "onFinish: " + response);
                // 解析Json数据并通知已经获取到了天气信息
                TpWeather.getInstance(mContext).parseJson(response);
                if (listener != null){
                    Log.d(TAG, "onFinish: " + true);
                    listener.onGetWeather(true);
                }
            }
            @Override
            public void onError(Exception e) {
                // 通知获取天气信息失败
                if (listener != null){
                    Log.d(TAG, "onError: " + false);
                    listener.onGetWeather(false);
                }
            }
        });
    }

    private OnGetWeatherListener listener;

    public interface OnGetWeatherListener{
        void onGetWeather(boolean isSucceed);
    }

    public void setOnGetWeatherListener(OnGetWeatherListener listener){
        this.listener = listener;
    }
}
