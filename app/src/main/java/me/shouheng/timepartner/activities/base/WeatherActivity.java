package me.shouheng.timepartner.activities.base;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpNet;
import me.shouheng.timepartner.utils.TpString;
import me.shouheng.timepartner.managers.WeatherManager;
import me.shouheng.timepartner.utils.TpWeather;
import me.shouheng.timepartner.utils.WeatherKey;

public class WeatherActivity extends BaseActivity implements View.OnClickListener{
    private ProgressDialog pDialog;
    private boolean isWeatherChanged = false;
    private final int WEATHER_INFO_UNCHANGED = -300;
    private final int WEATHER_INFO_CHANGED = 300;
    private TpWeather tpWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_weather_detail);
        tpWeather = TpWeather.getInstance(this);
        initViews();
    }

    /**
     * 初始化组件
     */
    private void initViews(){
        // 初始化工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // 更新天气信息：主界面 + 界面下部的四个组件
        updateWeather();
        // 生活信息
        int[] ids = new int[]{R.id.life1, R.id.life2, R.id.life3, R.id.life4};
        for (int id : ids){
            findViewById(id).setOnClickListener(this);
        }
        // 界面下方的四个天气信息
        int[] footersIds = new int[]{R.id.footer1, R.id.footer2, R.id.footer3, R.id.footer4};
        for (int id : footersIds){
            findViewById(id).setOnClickListener(this);
        }
    }

    /**
     * 更新天气信息，注意这里应该独立出来，
     * 因为当主动按下更新天气的时候需要更新界面显示的天气信息
     */
    private void updateWeather(){
        // 主界面的天气信息
        // 城市信息
        TextView tvLocation = (TextView) findViewById(R.id.location);
        String currentCity = tpWeather.getCityName();
        tvLocation.setText(currentCity);
        // 获取今天的天气信息
        TextView tvWeather = (TextView) findViewById(R.id.weather);
        HashMap<String ,String> map = tpWeather.getDetailOfDay(WeatherKey.WEATHER_KEY_DETAIL_DAY1);
        // 设置日期
        if (map.containsKey(WeatherKey.WEATHER_MAP_KEY_DATE)){
            String strDate = map.get(WeatherKey.WEATHER_MAP_KEY_DATE);
            TextView tvDate = (TextView) findViewById(R.id.date);
            tvDate.setText(TpString.getLastUpdateDate(strDate));
            // 设置温度
            TextView tvTemp = (TextView) findViewById(R.id.temperature);
            tvTemp.setText(TpString.getRealTimeTemp(strDate));
            TextView tvTempUnit = (TextView) findViewById(R.id.temp_unit);
            tvTempUnit.setText(R.string.temp_unit);
        }
        // 设置天气
        if (map.containsKey(WeatherKey.WEATHER_MAP_KEY_WEATHER)){
            String strWeather = map.get(WeatherKey.WEATHER_MAP_KEY_WEATHER);
            tvWeather.setText(strWeather);
            int imgRes = TpString.getWeatherImage(strWeather);
            if (imgRes != -1){
                ImageView ivWeather = (ImageView) findViewById(R.id.weather_pic);
                ivWeather.setImageResource(imgRes);
            }
        }
        // 设置PM2.5
        String pm25 = "PM2.5  " + tpWeather.getPm25();
        TextView tvPm25 = (TextView) findViewById(R.id.show_pm25);
        tvPm25.setText(pm25);
        // 设置风速
        if (map.containsKey(WeatherKey.WEATHER_MAP_KEY_WIND)){
            String strWind = map.get(WeatherKey.WEATHER_MAP_KEY_WIND);
            TextView tvWind = (TextView) findViewById(R.id.show_wind);
            tvWind.setText(strWind);
        }
        // 界面下面的四个天气信息
        String[] footerKeys = new String[]{
                WeatherKey.WEATHER_KEY_DETAIL_DAY1,
                WeatherKey.WEATHER_KEY_DETAIL_DAY2,
                WeatherKey.WEATHER_KEY_DETAIL_DAY3,
                WeatherKey.WEATHER_KEY_DETAIL_DAY4};
        int[][] footerViews = new int[][]{
                new int[]{R.id.day1_date, R.id.day1_weather, R.id.day1_temp},
                new int[]{R.id.day2_date, R.id.day2_weather, R.id.day2_temp},
                new int[]{R.id.day3_date, R.id.day3_weather, R.id.day3_temp},
                new int[]{R.id.day4_date, R.id.day4_weather, R.id.day4_temp}};
        for (int i=0;i<footerKeys.length;i++){
            setFooterValue(footerKeys[i], footerViews[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:// 返回
                onBack();
                break;
            case R.id.update_data:
                // 判断当前的网络状态是否可用
                boolean netWorkAvailable = TpNet.isNetworkAvailable(this);
                if (netWorkAvailable){
                    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, new OnGetPermissionCallback() {
                        @Override
                        public void onGetPermission() {
                            fetchWeather();
                        }
                    });
                } else {
                    makeToast(R.string.mw_network_unavailable);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 抓取天气信息
     */
    private void fetchWeather(){
        // 天气信息被更新了
        isWeatherChanged = true;
        // 显示正在更新对话框
        showUpdatingDialog();
        // 获取天气
        WeatherManager manager = new WeatherManager(getApplicationContext());
        manager.setOnGetWeatherListener(new WeatherManager.OnGetWeatherListener() {
            @Override
            public void onGetWeather(boolean isSucceed) {
                if (isSucceed){
                    // 更新天气成功
                    Message msg = Message.obtain();
                    msg.what = WEATHER_INFO_CHANGED;
                    handler.sendMessage(msg);
                } else {
                    // 更新天气失败
                    Message msg = Message.obtain();
                    msg.what = WEATHER_INFO_UNCHANGED;
                    handler.sendMessage(msg);
                }
            }
        });
        manager.start();
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WEATHER_INFO_UNCHANGED:
                    // 天气没有更新
                    makeToast(R.string.mw_failed_2_get_response);
                    break;
                case WEATHER_INFO_CHANGED:
                    // 天气已更新
                    updateWeather();
                    break;
            }
            // 撤销 正在更新 对话框
            if (pDialog != null && pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.footer1:
                showFooterDetail(WeatherKey.WEATHER_KEY_DETAIL_DAY1);
                break;
            case R.id.footer2:
                showFooterDetail(WeatherKey.WEATHER_KEY_DETAIL_DAY2);
                break;
            case R.id.footer3:
                showFooterDetail(WeatherKey.WEATHER_KEY_DETAIL_DAY3);
                break;
            case R.id.footer4:
                showFooterDetail(WeatherKey.WEATHER_KEY_DETAIL_DAY4);
                break;
            // 0:wearing, 2:visiting, 3:cold, 4:sport
            case R.id.life1:
                showLifeState(WeatherKey.WEATHER_KEY_WEARING_STATE);
                break;
            case R.id.life2:
                showLifeState(WeatherKey.WEATHER_KEY_VISITING_STATE);
                break;
            case R.id.life3:
                showLifeState(WeatherKey.WEATHER_KEY_COLD_STATE);
                break;
            case R.id.life4:
                showLifeState(WeatherKey.WEATHER_KEY_SPORT_STATE);
                break;
        }
    }

    /**
     * 设置界面底部的显示信息的值
     * @param keyDay 指定日期的KEY值
     * @param viewIds 组件的编号
     */
    private void setFooterValue(String keyDay, int[] viewIds){
        Map<String, String> map = tpWeather.getDetailOfDay(keyDay);
        // 获取映射表
        if (map != null) {
            // 获取组件
            TextView tvDate = (TextView) findViewById(viewIds[0]);
            ImageView ivWeather = (ImageView) findViewById(viewIds[1]);
            TextView tvTemp = (TextView) findViewById(viewIds[2]);
            // 为组件设置值
            // 日期
            String strDate = map.get(WeatherKey.WEATHER_MAP_KEY_DATE);
            if (!TextUtils.isEmpty(strDate)){
                tvDate.setText(TpString.getWeekFromDate(strDate));
            }
            // 天气
            String strWeather = map.get(WeatherKey.WEATHER_MAP_KEY_WEATHER);
            if (!TextUtils.isEmpty(strWeather)){
                int resId1 = TpString.getWeatherImage(strWeather);
                if (resId1 != -1){
                    ivWeather.setImageResource(resId1);
                }
            }
            // 温度
            String strTemp = map.get(WeatherKey.WEATHER_MAP_KEY_TEMP);
            if (!TextUtils.isEmpty(strTemp)){
                tvTemp.setText(TpString.getTemperature(strTemp));
            }
        }
    }

    /**
     * 顶部的信息显示组件
     * @param dayKey “天”的键值
     */
    private void showFooterDetail(String dayKey){
        // 获取指定日期的天气信息的映射表
        Map<String, String> map = tpWeather.getDetailOfDay(dayKey);
        if (map != null){
            // 获取天气信息
            String strWeather = map.get(WeatherKey.WEATHER_MAP_KEY_WEATHER);
            String strDate = map.get(WeatherKey.WEATHER_MAP_KEY_DATE);
            String strTemp = map.get(WeatherKey.WEATHER_MAP_KEY_TEMP);
            String strWind = map.get(WeatherKey.WEATHER_MAP_KEY_WIND);
            // 组装天气信息
            String msg = strWeather + "\n" + strTemp + "\n" + strWind;
            // 显示天气信息对话框
            Dialog dlg = new AlertDialog.Builder(this)
                    .setTitle(strDate)
                    .setMessage(msg)
                    .create();
            dlg.show();
        }
    }

    /**
     * 显示生活信息
     * @param stateKey “天”的键值
     */
    private void showLifeState(String stateKey){
        // 获取生活信息的映射表
        Map<String, String> map = tpWeather.getLifeState(stateKey);
        if (map != null){
            // 获取生活信息
            String strTitle = map.get(WeatherKey.WEATHER_MAP_KEY_TITLE);
            String strZs = map.get(WeatherKey.WEATHER_MAP_KEY_ZS);
            String strDes = map.get(WeatherKey.WEATHER_MAP_KEY_DES);
            // 组装生活信息
            String msg = strTitle + " : " + strZs + "\n\t" + strDes;
            // 显示生活信息
            TextView tvShowLifeState = (TextView) findViewById(R.id.show_life_state);
            tvShowLifeState.setText(msg);
        }
    }

    /**
     * 显示更新对话框
     */
    private void showUpdatingDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);// 点击可以取消Dialog的展现
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage(getString(R.string.mw_updating));
        pDialog.show();
    }

    /**
     * 按下返回时调用的方法
     */
    private void onBack(){
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_DATA_CHANGED, isWeatherChanged);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }
}
