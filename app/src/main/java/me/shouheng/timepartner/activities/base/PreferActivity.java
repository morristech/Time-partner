package me.shouheng.timepartner.activities.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import com.bumptech.glide.Glide;

import me.shouheng.timepartner.managers.ImageManager;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.PreferKey;
import me.shouheng.timepartner.utils.TpFile;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.preference.BasePreferView;
import me.shouheng.timepartner.widget.preference.OnPreferenceClickListener;
import me.shouheng.timepartner.widget.preference.PreferCheckbox;
import me.shouheng.timepartner.widget.preference.PreferImageView;
import me.shouheng.timepartner.widget.preference.PreferTextView;
import me.shouheng.timepartner.widget.dialog.ColorPickerDialog;
import me.shouheng.timepartner.widget.dialog.EdtDialog;
import me.shouheng.timepartner.widget.dialog.ImageSelectDialog;

public class PreferActivity extends BaseActivity implements OnPreferenceClickListener{
    private boolean isDataChanged = false;
    private final int CLS_START_TIME = 1, CLS_END_TIME = 2, CLD_START_TIME = 3, CLD_END_TIME = 4;
    private final int EDIT_CLS_HEIGHT = 0, EDIT_CLD_HEIGHT = 1;
    private TpPrefer prefer;
    private Dialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        prefer = TpPrefer.getInstance(getApplicationContext());

        initValue();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack(){
        if (isDataChanged){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private void initValue(){
        PreferImageView bgPrefer = (PreferImageView) findViewById(R.id.set_background);
        Bundle bundle = prefer.getBackground();
        int bgMode = bundle.getInt(PreferKey.KEY_BG_MODE);
        String bg = bundle.getString(PreferKey.KEY_KEY_BG);
        if (bgMode == PreferKey.BG_MODE_COLOR){
            int colorRes = TpColor.getColorResource(bg);
            bgPrefer.setImageRes(colorRes);
        } else if (bgMode == PreferKey.BG_MODE_IMAGE){
            if (TpFile.isFileExist(bg))
                Glide.with(this).load(bg).into(bgPrefer.getImageView());
        }
        bgPrefer.setOnPreferenceClickListener(this);

        PreferCheckbox weatherPrefer = (PreferCheckbox) findViewById(R.id.set_circumstance);
        weatherPrefer.setChecked(prefer.getShowWeather());
        weatherPrefer.setOnPreferenceClickListener(this);

        PreferTextView sayingPrefer = (PreferTextView) findViewById(R.id.set_saying);
        sayingPrefer.setDetail(prefer.getSaying());
        sayingPrefer.setOnPreferenceClickListener(this);

        PreferCheckbox lunarPrefer = (PreferCheckbox) findViewById(R.id.set_show_lunar);
        lunarPrefer.setChecked(prefer.getShowLunar());
        lunarPrefer.setOnPreferenceClickListener(this);

        int[] ids = new int[]{
            R.id.set_notice_mode,
            R.id.set_daily_notice_time,
            R.id.set_item_notice_time,
            R.id.set_week_calendar_height,
            R.id.set_week_calendar_start_time,
            R.id.set_week_calendar_end_time,
            R.id.set_class_board_height,
            R.id.set_class_board_start_time,
            R.id.set_class_board_end_time};
        for (int id : ids){
            ((BasePreferView) findViewById(id)).setOnPreferenceClickListener(this);
        }
    }

    @Override
    public void OnPreferenceClick(View view) {
        isDataChanged = true;
        switch (view.getId()){
            case R.id.set_background:showBgOptionsDlg();break;
            case R.id.set_circumstance:prefer.setShowWeather(((PreferCheckbox) findViewById(R.id.set_circumstance)).isChecked());break;
            case R.id.set_saying:showEditDlg();break;
            case R.id.set_show_lunar:prefer.setShowLunar(((PreferCheckbox) findViewById(R.id.set_show_lunar)).isChecked());break;
            case R.id.set_notice_mode:showNoticeModeOptions();break;
            case R.id.set_daily_notice_time:showDailyNoticeOptions();break;
            case R.id.set_item_notice_time:showNoticeTimeOptions();break;
            case R.id.set_week_calendar_height:showHourHeightDlg(R.string.set_week_calendar_height_title, EDIT_CLD_HEIGHT);break;
            case R.id.set_week_calendar_start_time:showPickerDlg(R.string.start_time_title, CLD_START_TIME);break;
            case R.id.set_week_calendar_end_time:showPickerDlg(R.string.end_time_title, CLD_END_TIME);break;
            case R.id.set_class_board_height:showHourHeightDlg(R.string.set_cls_hour_height_title, EDIT_CLS_HEIGHT);break;
            case R.id.set_class_board_start_time:showPickerDlg(R.string.cls_start_time_title, CLS_START_TIME);break;
            case R.id.set_class_board_end_time:showPickerDlg(R.string.cls_end_time_title, CLS_END_TIME);break;
        }
    }

    private void showNoticeModeOptions(){
        int checkedItem = 0;
        switch (TpPrefer.getInstance(this).getNoticeMode()){
            case PreferKey.NOTICE_MODE1:
                checkedItem = 0;
                break;
            case PreferKey.NOTICE_MODE2:
                checkedItem = 1;
                break;
            case PreferKey.NOTICE_MODE3:
                checkedItem = 2;
                break;
        }
        dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.set_notice_title)
                .setSingleChoiceItems(R.array.notice_mode_items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefer.setNoticeMode(which);
                        if (null != dlg) dlg.dismiss();
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create();
        dlg.show();
    }

    private void showDailyNoticeOptions(){
        int checkedItem = prefer.getDailyNoticeTime() - 5;
        dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.daily_notice_time)
                .setSingleChoiceItems(R.array.daily_notice_time, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefer.setDailyNoticeTime(which + 5);
                        if (null != dlg) dlg.dismiss();
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create();
        dlg.show();
    }

    private void showNoticeTimeOptions(){
        final long modes[] = new long[]{
                0,
                5 * TpTime.MIN_MILLIS,
                10 * TpTime.MIN_MILLIS,
                15 * TpTime.MIN_MILLIS,
                30 * TpTime.MIN_MILLIS,
                TpTime.HOUR_MILLIS,
                2 * TpTime.HOUR_MILLIS,
                TpTime.DAY_MILLIS};
        int checkedItem = 3;
        long oldMillis = prefer.getNoticeTime();
        int len = modes.length;
        for (int i=0;i<len;i++){
            if (modes[i] == oldMillis){
                checkedItem = i;
                break;
            }
        }
        dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.notice_time)
                .setSingleChoiceItems(R.array.item_notice_time, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefer.setNoticeTime(modes[which]);
                        if (null != dlg) dlg.dismiss();
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create();
        dlg.show();
    }

    private void showBgOptionsDlg(){
        Dialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.set_dash_bg_title)
                .setItems(R.array.bg_select_dialog_items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                new ColorPickerDialog(PreferActivity.this)
                                        .setTitle(R.string.bg_color_selector_title)
                                        .setOnColorSelectedListener(new ColorPickerDialog.OnColorSelectedListener() {
                                            @Override
                                            public void onSelected(int position) {
                                                String strColor = TpColor.getColorString(position);
                                                if (!TextUtils.isEmpty(strColor)){
                                                    prefer.setBackground(
                                                            strColor, PreferKey.BG_MODE_COLOR);
                                                    int resId = TpColor.getColorResource(position);
                                                    if (resId != -1){
                                                        ((PreferImageView)findViewById(R.id.set_background)).setImageRes(resId);
                                                    }
                                                }
                                            }
                                        })
                                        .show();
                                break;
                            case 1:
                                new ImageSelectDialog(PreferActivity.this).show();
                                break;
                        }
                    }
                })
                .create();
        dlg.show();
    }

    private void showEditDlg() {
       new EdtDialog(this, prefer.getSaying(),
                getString(R.string.set_dash_saying),
                getString(R.string.edit_saying_tip))
                .setPositiveButton(R.string.com_confirm, new EdtDialog.PositiveButtonClickListener(){
                    @Override
                    public void onClick(String content) {
                        ((PreferTextView)findViewById(R.id.set_saying)).setDetail(content);
                        prefer.setSaying(content);
                        if (TextUtils.isEmpty(content)) {
                            makeToast(R.string.edit_empty);
                        }
                    }
                }).show();
    }

    private void showHourHeightDlg(int titleRes, final int mode){
        final int MIN_HEIGHT = 6;
        int checkedItem;
        if (mode == EDIT_CLS_HEIGHT){
            checkedItem = prefer.getClsHourHeight() - MIN_HEIGHT;
        } else {
            checkedItem = prefer.getCldHourHeight() - MIN_HEIGHT;
        }
        Dialog dialog = new AlertDialog.Builder(this).setSingleChoiceItems(
                R.array.board_height_items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (mode){
                    case EDIT_CLD_HEIGHT:
                        prefer.setCldHourHeight(which + MIN_HEIGHT);
                        break;
                    case EDIT_CLS_HEIGHT:
                        prefer.setClsHourHeight(which + MIN_HEIGHT);
                        break;
                }
            }
        }).create();
        dialog.setTitle(titleRes);
        dialog.show();
    }

    private void showPickerDlg(int titleRes, final int mode){
        // 课程表的开始时间 mode=1 课程表的结束时间 mode=2, 日历的开始时间 mode=3  日历的结束时间 mode=4
        View view = getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
        final NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.picker);
        hourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                String tmpStr = String.valueOf(value) + ":00";
                if (value < 10) tmpStr = "0" + tmpStr;
                return tmpStr;
            }
        });
        hourPicker.setMaxValue(23);// 注意默认的时间是0~23（包含0和23）
        hourPicker.setMinValue(0);
        int dValue = 8;
        switch (mode){
            case CLD_START_TIME:dValue = prefer.getCldStartTime();break;
            case CLD_END_TIME:dValue = prefer.getCldEndTime();break;
            case CLS_START_TIME:dValue = prefer.getClsStartTime();break;
            case CLS_END_TIME:dValue = prefer.getClsEndTime();break;
        }
        hourPicker.setValue(dValue);
        new AlertDialog.Builder(this)
                .setTitle(titleRes)
                .setView(view)
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (mode){
                            case CLD_START_TIME:
                                int cldET = prefer.getCldEndTime();
                                if (hourPicker.getValue() >= cldET){
                                    makeToast(R.string.cld_toast_error);
                                    break;
                                }
                                prefer.setCldStartTime(hourPicker.getValue());
                                break;
                            case CLD_END_TIME:
                                int cldST = prefer.getCldStartTime();
                                if (hourPicker.getValue() <= cldST){
                                    makeToast(R.string.cld_toast_error);
                                    break;
                                }
                                prefer.setCldEndTime(hourPicker.getValue());
                                break;
                            case CLS_START_TIME:
                                int clsET = prefer.getClsEndTime();
                                if (hourPicker.getValue() >= clsET){
                                    makeToast(R.string.cld_toast_error);
                                    break;
                                }
                                prefer.setClsStartTime(hourPicker.getValue());
                                break;
                            case CLS_END_TIME:
                                int clsST = prefer.getClsStartTime();
                                if (hourPicker.getValue() <= clsST){
                                    makeToast(R.string.cld_toast_error);
                                    break;
                                }
                                prefer.setClsEndTime(hourPicker.getValue());
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constants.REQUEST_CODE_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri bgUri = Uri.fromFile(TpFile.getTempBGFile(PreferActivity.this));
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(bgUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, bgUri);
                    startActivityForResult(intent, Constants.REQUEST_CODE_CROP_PHOTO);
                }
                break;
            case Constants.REQUEST_CODE_CROP_PHOTO:
                if(resultCode == RESULT_OK){
                    String path = TpFile.getBGFile(PreferActivity.this).getPath();
                    prefer.setBackground(path, PreferKey.BG_MODE_IMAGE);
                    if (TpFile.isFileExist(path)){
                        Glide.with(PreferActivity.this).load(path).into(
                                ((PreferImageView)findViewById(R.id.set_background)).getImageView());
                    }
                }
                break;
            case Constants.REQUEST_CODE_CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    String path;
                    if(Build.VERSION.SDK_INT>=19){
                        path = new ImageManager(PreferActivity.this).handleImageOnKitKat(data);
                    }else{
                        path = new ImageManager(PreferActivity.this).handleImageBeforeKitKat(data);
                    }
                    if (TpFile.isFileExist(path)){
                        Glide.with(PreferActivity.this).load(path)
                                .into(((PreferImageView)findViewById(R.id.set_background)).getImageView());
                        prefer.setBackground(path, PreferKey.BG_MODE_IMAGE);
                    }
                }
                break;
        }
    }
}
