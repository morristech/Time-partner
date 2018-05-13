package me.shouheng.timepartner.activities.base;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

import me.shouheng.timepartner.database.dao.loader.DayDetailsLoader;
import me.shouheng.timepartner.managers.TpAlarmManager;
import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.DailyDetailsAdapter;
import me.shouheng.timepartner.activities.assignment.AssignEditActivity;
import me.shouheng.timepartner.activities.tpclass.ClassEditActivity;
import me.shouheng.timepartner.activities.assignment.AssignActivity;
import me.shouheng.timepartner.activities.tpclass.ClassActivity;
import me.shouheng.timepartner.activities.note.NoteCollection;
import me.shouheng.timepartner.activities.note.NoteEdit;
import me.shouheng.timepartner.activities.task.TaskDashboard;
import me.shouheng.timepartner.activities.task.TaskEdit;
import me.shouheng.timepartner.activities.tpcalendar.CalendarActivity;
import me.shouheng.timepartner.selector.MultiImageSelectorActivity;
import me.shouheng.timepartner.utils.PreferKey;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpFile;
import me.shouheng.timepartner.utils.TpNet;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.utils.WeatherKey;
import me.shouheng.timepartner.widget.dialog.AboutDialog;
import me.shouheng.timepartner.widget.dialog.RateDialog;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpString;
import me.shouheng.timepartner.managers.WeatherManager;
import me.shouheng.timepartner.utils.TpWeather;
import me.shouheng.timepartner.widget.fab.TpFloatActionButton;

public class MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, TpFloatActionButton.OnFabClickListener{
    private TimeChangeReceiver timeChangeReceiver;
    private AppBarLayout barLayout;
    private TextView userName;
    private final int UPDATE_WEATHER = 50;
    private final int FAILED_TO_GET_WEATHER = 51;
    private final int REQUEST_CODE_FOR_WEATHER = 46;
    private DailyDetailsAdapter adapter;
    private DayDetailsLoader loader;
    private Toolbar toolbar;
    private View vUserInfo;
    private static final String TAG = "MainActivity__";
    private int tbColor = Color.parseColor(TpColor.COLOR_PRIME);

    public static void activityStart(Context context, String account){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Intents.EXTRA_ACCOUNT, account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_main_layout);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(Intents.EXTRA_ACCOUNT))
            finish();

        ActivityManager.addActivity(this);
        UserKeeper.reset(getApplicationContext());
        TpPrefer.reset();
        TpWeather.reset();
        initViews();
        TpAlarmManager.setAlarmsToday(this);
    }

    void initViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        barLayout = (AppBarLayout) findViewById(R.id.bar);
        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int barHeight = toolbar.getHeight();
                int maxOffset = barLayout.getTotalScrollRange() - barHeight;
                if (maxOffset != 0){
                    int alpha = -verticalOffset * 255 / maxOffset;
                    alpha = alpha > 255 ? 255 : alpha;
                    toolbar.setBackgroundColor(
                            Color.argb(alpha, Color.red(tbColor), Color.green(tbColor), Color.blue(tbColor)));
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.main_nav_drawer_open, R.string.main_nav_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        if (navView.getHeaderCount() != 0){
            vUserInfo = navView.getHeaderView(0);
        }
        initUserInfo();

        initAppBar();

        loader = DayDetailsLoader.getInstance(this);
        adapter = new DailyDetailsAdapter(this);
        adapter.addDetails(loader.loadOfToday());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.show_daily);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        TpFloatActionButton fabMenu = (TpFloatActionButton) findViewById(R.id.fab_menu);
        fabMenu.setOnFabClickListener(this);
    }

    private void initUserInfo(){
        if (vUserInfo == null) return;

        ImageView userIcon = (ImageView) vUserInfo.findViewById(R.id.userIcon);
        userIcon.setOnClickListener(this);
        File fIcon = TpFile.getIcon(this);
        if (TpFile.isFileExist(fIcon)){
            TpFile.loadBitmap(fIcon.getPath(), userIcon);
        }
        TextView navAccount = (TextView) vUserInfo.findViewById(R.id.user_account);
//        userName = (TextView) vUserInfo.findViewById(R.id.user_name);
        navAccount.setText(UserKeeper.getUser().getAccount());
    }

    private void initAppBar(){
        initUpCard();

        initTimeInfo();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);

        findViewById(R.id.weather_detail).setOnClickListener(this);
        findViewById(R.id.right_arrow).setOnClickListener(this);

        if (TpNet.isNetworkAvailable(this)){
            // 判断是否需要更新天气信息
            boolean dataOutOfDate = TpWeather.getInstance(this).isDataOutOfDate();
            if (dataOutOfDate) {
                // 抓取天气数据
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, new OnGetPermissionCallback() {
                    @Override
                    public void onGetPermission() {
                        fetchWeather();
                    }
                });
            } else {
                // 数据没过期
                updateWeather();
            }
        } else {
            // 网络不可用
            updateWeather();
        }
    }

    private void initUpCard(){
        ImageView barBg = (ImageView) findViewById(R.id.bar_bg);
        Bundle bundle = TpPrefer.getInstance(this).getBackground();
        int bgMode = bundle.getInt(PreferKey.KEY_BG_MODE);
        switch (bgMode){
            case PreferKey.BG_MODE_COLOR:
                String bgColor = bundle.getString(PreferKey.KEY_KEY_BG);
                if (!TextUtils.isEmpty(bgColor)){
                    int resId = TpColor.getColorResource(bgColor);
                    barBg.setImageResource(resId);
                    int pColor = Color.parseColor(bgColor);
                    TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
                    tbColor = pColor;
                }
                break;
            case PreferKey.BG_MODE_IMAGE:
                String bgPath = bundle.getString(PreferKey.KEY_KEY_BG);
                Glide.with(this)
                        .load(bgPath)
                        .crossFade()
                        .into(barBg);
                TpDisp.translucentStatusBar(this, true);
                break;
        }

        String saying = TpPrefer.getInstance(this).getSaying();
        TextView tvSaying = (TextView) findViewById(R.id.saying);
        tvSaying.setText(saying);
    }

    public void fetchWeather(){
        WeatherManager manager = new WeatherManager(getApplicationContext());
        manager.setOnGetWeatherListener(new WeatherManager.OnGetWeatherListener() {
            @Override
            public void onGetWeather(boolean isSucceed) {
                if (isSucceed) {
                    Message msg = Message.obtain();
                    msg.what = UPDATE_WEATHER;
                    handler.sendMessage(msg);
                } else {
                    Message msg = Message.obtain();
                    msg.what = FAILED_TO_GET_WEATHER;
                    handler.sendMessage(msg);
                }
            }
        });
        manager.start();
    }

    private void initTimeInfo(){
        TextView tempDate = (TextView) findViewById(R.id.date);
        TextView tempYear = (TextView) findViewById(R.id.year);
        TextView tempWeek = (TextView) findViewById(R.id.week);
        String[] strs = TpTime.getDashDate();
        tempDate.setText(strs[0]);
        tempWeek.setText(strs[1]);
        tempYear.setText(strs[2]);
    }

    private void updateWeather(){
        TextView tvTemperature = (TextView) findViewById(R.id.temperature);
        ImageView ivWeather = (ImageView) findViewById(R.id.weather_pic);
        TextView tvLocation = (TextView) findViewById(R.id.location);
        TextView tvWeather = (TextView) findViewById(R.id.weather_text);
        if (!TpPrefer.getInstance(this).getShowWeather()){
            tvTemperature.setVisibility(View.GONE);
            ivWeather.setVisibility(View.GONE);
            tvLocation.setVisibility(View.GONE);
            tvWeather.setVisibility(View.GONE);
            return;
        }
        tvLocation.setVisibility(View.VISIBLE);
        ivWeather.setVisibility(View.VISIBLE);
        tvWeather.setVisibility(View.VISIBLE);
        tvTemperature.setVisibility(View.VISIBLE);

        Map<String, String> map = TpWeather.getInstance(this).getDetailOfDay(WeatherKey.WEATHER_KEY_DETAIL_DAY1);
        Log.d(TAG, "updateWeather: " + (map == null));
        if (map != null) {
            String strDate = map.get(WeatherKey.WEATHER_MAP_KEY_DATE);
            if (!TextUtils.isEmpty(strDate)){
                String realTemperature = TpString.getRealTimeTemp(strDate) + getString(R.string.temp_unit);
                tvTemperature.setText(realTemperature);
            }

            String strWeather = map.get(WeatherKey.WEATHER_MAP_KEY_WEATHER);
            if (!TextUtils.isEmpty(strWeather)){
                tvWeather.setText(strWeather);
                int resId = TpString.getWeatherImage(strWeather);
                if (resId != -1){
                    ivWeather.setImageResource(resId);
                }
            }

            String strLocation = TpWeather.getInstance(this).getCityName();
            if (!TextUtils.isEmpty(strLocation)){
                tvLocation.setText(TpWeather.getInstance(this).getLastLocation());
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FAILED_TO_GET_WEATHER:
                    updateWeather();
                    TpDisp.showToast(MainActivity.this, R.string.mw_failed_2_get_response);
                case UPDATE_WEATHER:
                    updateWeather();
                    break;
            }

        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.userIcon:
                startActivity(new Intent(this, MultiImageSelectorActivity.class));
//                closeDrawer();
//                intent.setClass(this, UserActivity.class);
//                postStartActivityForResult(intent, Constants.REQUEST_CODE_USER_INFO);
                break;
            case R.id.menu1:// 日历
                intent.setClass(this, CalendarActivity.class);
                postStartActivity(intent);
                break;
            case R.id.menu2:// 课程
                intent.setClass(this, ClassActivity.class);
                postStartActivity(intent);
                break;
            case R.id.menu3:// 日程
                intent.setClass(this, AssignActivity.class);
                postStartActivity(intent);
                break;
            case R.id.menu4:// 作业
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TaskDashboard.start(MainActivity.this, TaskDashboard._TYPE_TASK);
                    }
                }, openDelay);
                break;
            case R.id.menu5:// 考试
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TaskDashboard.start(MainActivity.this, TaskDashboard._TYPE_EXAM);
                    }
                }, openDelay);
                break;
            case R.id.menu6:// 随手记
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NoteCollection.start(MainActivity.this);
                    }
                }, openDelay);
                break;
            case R.id.right_arrow:
            case R.id.weather_detail:// 天气信息
                postStartActivityForResult(
                        new Intent(this, WeatherActivity.class), REQUEST_CODE_FOR_WEATHER);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        closeDrawer();
        Intent intent = new Intent();
        switch (item.getItemId()){
            case R.id.nav_rate_today:
                closeDrawer();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new RateDialog(MainActivity.this).rate();
                    }
                }, openDelay);
                break;
            case R.id.nav_sync:
                break;
            case R.id.nav_trash:
                intent.setClass(this, TrashActivity.class);
                postStartActivity(intent);
                break;
            case R.id.nav_about:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new AboutDialog(MainActivity.this).show();
                    }
                }, openDelay);
                break;
            case R.id.nav_setting:
                intent.setClass(this, PreferActivity.class);
                postStartActivityForResult(intent, Constants.REQUEST_CODE_SETTING);
                break;
            case R.id.nav_feedback:
                intent.setAction(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:shouheng2015@gmail.com");
                intent.setData(data);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e){
//                    makeToast();
                }
                break;
        }
        return false;
    }

    @Override
    public void onFabClick(int pos) {
        switch (pos){
            case 0:break;
            case 1:NoteEdit.edit(this, 0L);
                break;
            case 2:
                TaskEdit.edit(this, Constants.TYPE_EXAM, null);
                break;
            case 3:TaskEdit.edit(this, Constants.TYPE_TASK, null);
                break;
            case 4:
                AssignEditActivity.edit(this, null);
                break;
            case 5:
                ClassEditActivity.edit(this, null);
                break;
        }
    }

    class TimeChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar now = Calendar.getInstance();
            int mHour = now.get(Calendar.HOUR_OF_DAY);
            int mMin = now.get(Calendar.MINUTE);
            if (mHour == 0 && mMin == 0){
                initTimeInfo();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constants.REQUEST_CODE_USER_INFO:
                if (resultCode == RESULT_OK){
                    if (data.getBooleanExtra("icon_changed",false)){
                        initUserInfo();
                    }
                    if (data.getBooleanExtra("name_changed", false)){
//                        userName.setText(User.getUserName());
                    }
                }
                break;
            case Constants.REQUEST_CODE_SETTING:
                if (resultCode == RESULT_OK){
                    initUpCard();
                }
                break;
            case REQUEST_CODE_FOR_WEATHER:
                if (resultCode == RESULT_OK){
                    if (data.getBooleanExtra(Constants.KEY_DATA_CHANGED, false)){
                        updateWeather();
                    }
                }
                break;
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            TpDisp.showToast(MainActivity.this, R.string.press_again_exit_app);
            exitTime = System.currentTimeMillis();
        } else {
            ActivityManager.finishAll();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            doExitApp();
        }
    }

    @Override
    protected void onDestroy() {
        if (timeChangeReceiver != null) {
            unregisterReceiver(timeChangeReceiver);
        }
        super.onDestroy();
    }

    private void closeDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // use broadcast to send the update message
//        loader.start();
//        adapter.addDetails(loader.get());
//        adapter.notifyDataSetChanged();
    }
}
