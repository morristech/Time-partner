package me.shouheng.timepartner.utils;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 *使用示例：(1).创建实体; (2).设置监听事件; (3).调用start()方法开始监听.
 LocationUtils locationUtils = new LocationUtils(getApplicationContext());
 locationUtils.setOnFinishListener(new LocationUtils.OnLocationCompletedListener() {
    public void onCompleted(BDLocation location) {
        //定位结束，获取信息
    }
 });
 locationUtils.loadAllExams();
 */
public class TpLocation {
    private Context mContext;
    private static final String TAG = "LocationUtils.";

    public TpLocation(Context context){
        mContext = context;
    }

    /**
     * 开始定位监听 */
    public void start(){
        // 初始化百度地图
        LocationClient mLocationClient = new LocationClient(mContext);
        BDLocationListener mListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    /**
     * 定位监听器：获取到的结果通过接口回调返回给指定的活动 */
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // 接收到了地址
            if (listener != null){
                listener.onCompleted(location);
                Log.d(TAG, "onReceiveLocation: " + location.getAddrStr());
            }
        }
    }

    private OnFinishListener listener;

    public interface OnFinishListener {
        void onCompleted(BDLocation location);
    }

    public void setOnFinishListener(OnFinishListener listener){
        this.listener = listener;
    }
}
