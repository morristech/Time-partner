package me.shouheng.timepartner.activities.base;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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

import java.io.File;

import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.managers.TpAlarmManager;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpFile;
import me.shouheng.timepartner.widget.dialog.EdtDialog;

public class UserActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvMale, tvFemale, tvSecret;
    private TextView tvNameState;
    private TextView tvCityState;
    private TextView tvPhoneState;
    private TextView tvWeChatState;
    private TextView tvQQState;
    private TextView tvWeiBoState;
    private final int SWITCH2MALE = 1001;
    private final int SWITCH2FEMALE = 1002;
    private final int SWITCH2SECRET = 1003;
    private Dialog dlgI;
    private boolean isIconChanged = false;
    private boolean isNameChanged = false;
    private static final String TAG = "UserInfo__";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_user_info_layout);
        // 将当前活动添加到管理中
        ActivityManager.addActivity(this);
        // 初始化工具栏
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        TPDisp.translucentStatusBar(this, true);
        // 初始化各个组件
        initViews();
    }

    /**
     * 初始化组件
     */
    private void initViews(){
        initUp();
        findViewById(R.id.set_icon).setOnClickListener(this);
        TextView tvAccount = (TextView)findViewById(R.id.user_account);
        tvAccount.setText(UserKeeper.getUser().getAccount());
        // 各个项目的值的设置
        // 性别
        tvMale = (TextView) findViewById(R.id.male);
        tvFemale = (TextView) findViewById(R.id.female);
        tvSecret = (TextView) findViewById(R.id.secret);
        // 其他
        tvNameState = (TextView) findViewById(R.id.nick_name_state);
//        tvNameState.setText(User.getUserName());
        tvCityState = (TextView) findViewById(R.id.city_state);
        tvPhoneState = (TextView) findViewById(R.id.phone_state);
        tvWeChatState = (TextView) findViewById(R.id.weChat_state);
        tvWeiBoState = (TextView) findViewById(R.id.weiBo_state);
    }

    /**
     * 初始化顶部的信息显示面板
     */
    private void initUp(){
        //Glide.getAccount(this).clearDiskCache();
        ImageView userIcon = (ImageView) findViewById(R.id.userIcon);
        ImageView ivBlurBg = (ImageView) findViewById(R.id.icon_bg);
        File iconPath = TpFile.getIcon(this);
        if (TpFile.isFileExist(iconPath)){
            TpFile.loadBitmap(iconPath.getPath(), userIcon);
            File blurIcon = TpFile.getBlurIcon(this);
            if (TpFile.isFileExist(blurIcon)){
                TpFile.loadBitmap(blurIcon.getPath(), ivBlurBg);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap dBG = BitmapFactory.decodeFile(iconPath.getPath(), options);
                Bitmap bluredIcon = TpDisp.blur(this, dBG, 12);
                TpFile.saveBlurIcon(this, bluredIcon);
                ivBlurBg.setImageBitmap(bluredIcon);
            }
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap dBG = BitmapFactory.decodeResource(getResources(), R.drawable.tp_user_icon, options);
            Bitmap bpBlur = TpDisp.blur(this, dBG, 12);
            ivBlurBg.setImageBitmap(bpBlur);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_icon:
                showIconEditDlg();
                break;
            case R.id.from_camera:
                shootPicture();
                break;
            case R.id.from_album:
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    // 申请WRITE_EXTERNAL_STORAGE权限
//                    Log.d(TAG, "onClick: " + "request permission!");
//                    ActivityCompat.requestPermissions(this, new String[]{
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            TPConstants.STORAGE_PERMISSION_REQUEST_CODE);
//                } else {
//                    Log.d(TAG, "onClick: " + "permission granted!");
//                    pickerPicture();
//                }
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new OnGetPermissionCallback() {
                    @Override
                    public void onGetPermission() {
                        pickerPicture();
                    }
                });
                break;
            case R.id.nick_name:
                editNickName();
                isNameChanged = true;
                break;
            case R.id.city:
                break;
            case R.id.school:
                break;
            case R.id.phone:
                break;
            case R.id.weChat:
                break;
            case R.id.weiBo:
                break;
            case R.id.male:
                switchGender(SWITCH2MALE);
                break;
            case R.id.female:
                switchGender(SWITCH2FEMALE);
                break;
            case R.id.secret:
                switchGender(SWITCH2SECRET);
                break;
        }
    }

    /**
     * 拍摄图片
     */
    private void shootPicture(){
        File tempImage = TpFile.createTempIconFile(this);
        Uri iconUri = Uri.fromFile(tempImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, iconUri);
        startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PHOTO);
        if (dlgI != null && dlgI.isShowing()){
            dlgI.cancel();
        }
        isIconChanged = true;
    }

    /**
     * 从相册中选择图片
     */
    private void pickerPicture(){
        Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
        intent2.setType("image/*"); //该MIME的含义是图片中的任何类型，比如image/png
        startActivityForResult(intent2, Constants.REQUEST_CODE_CHOOSE_PHOTO);
        if (dlgI!=null&&dlgI.isShowing()){
            dlgI.cancel();
        }
        isIconChanged = true;
    }

    /**
     * 显示图片编辑对话框
     */
    private void showIconEditDlg(){
        View vi = getLayoutInflater().inflate(R.layout.dialog_pic_src_selector, null);
        vi.findViewById(R.id.from_camera).setOnClickListener(this);
        vi.findViewById(R.id.from_album).setOnClickListener(this);
        dlgI = new AlertDialog.Builder(this)
                .setTitle(R.string.dlg_img_title)
                .setView(vi)
                .setPositiveButton(R.string.dlg_img_cancel, null)
                .create();
        dlgI.show();
    }

    /**
     * 编辑用户昵称
     */
    private void editNickName(){
        EdtDialog dlg = new EdtDialog(this,
                EdtDialog.DIALOG_TYPE_NORMAL,
                tvNameState.getText().toString(),
                getString(R.string.edit_nick_name),
                getString(R.string.edit_nick_name_tip));
        dlg.setPositiveButton(R.string.com_confirm,
                new EdtDialog.PositiveButtonClickListener(){
                    @Override
                    public void onClick(String content) {
                        if (!TextUtils.isEmpty(content)){
                            //将编辑好的昵称设置到昵称显示组件
                            tvNameState.setText(content);
                        }
                    }
                });
        dlg.setSingleLine(true);
        dlg.show();
    }

    /**
     * 转换用户的性别信息
     * @param switchMode 性别信息转换值
     */
    private void switchGender(int switchMode){
        tvMale.setTextColor(Color.parseColor("#5d000000"));
        tvFemale.setTextColor(Color.parseColor("#5d000000"));
        tvSecret.setTextColor(Color.parseColor("#5d000000"));
        switch (switchMode){
            case SWITCH2MALE:
                tvMale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvMale.setTextColor(Color.WHITE);
                tvFemale.setBackgroundResource(R.drawable.frame_black_gray);
                tvSecret.setBackgroundResource(R.drawable.frame_black_gray);
                break;
            case SWITCH2FEMALE:
                tvFemale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvFemale.setTextColor(Color.WHITE);
                tvMale.setBackgroundResource(R.drawable.frame_black_gray);
                tvSecret.setBackgroundResource(R.drawable.frame_black_gray);
                break;
            case SWITCH2SECRET:
                tvSecret.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tvSecret.setTextColor(Color.WHITE);
                tvMale.setBackgroundResource(R.drawable.frame_black_gray);
                tvFemale.setBackgroundResource(R.drawable.frame_black_gray);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constants.REQUEST_CODE_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri iconUri = Uri.fromFile(TpFile.getTempIconFile(this));
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(iconUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, iconUri);
                    startActivityForResult(intent, Constants.REQUEST_CODE_CROP_PHOTO);
                }
                break;
            case Constants.REQUEST_CODE_CROP_PHOTO:
                if(resultCode == RESULT_OK){
                    TpFile.setTempIconFormal(this);
                    File bFile = TpFile.getBlurIcon(this);
                    if (TpFile.isFileExist(bFile)){
                        bFile.delete();
                    }
                    initUp();
                }
                break;
            case Constants.REQUEST_CODE_CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    String imagePath;
                    // 根据SDK的版本选择不同操作：
                    if(Build.VERSION.SDK_INT>=19){
                        imagePath = TpFile.handleImageOnKitKat(this, data);
                    }else{
                        imagePath = TpFile.handleImageBeforeKitKat(this, data);
                    }
                    TpFile.renameImage(imagePath, TpFile.getIconPath(this));
                    File bFile = TpFile.getBlurIcon(this);
                    if (TpFile.isFileExist(bFile)){
                        bFile.delete();
                    }
                    initUp();
                }
                break;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case TPConstants.STORAGE_PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Log.d(TAG, "onRequestPermissionsResult: " + "permission granted!");
//                    pickerPicture();
//                } else {
//                    Log.d(TAG, "onRequestPermissionsResult: " + "denied!");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                        Log.d(TAG, "onRequestPermissionsResult: " + "M");
//                        if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
//                            String msg = getString(R.string.permission_set_dialog_msg_body1)
//                                    + getString(R.string.permission_storage)
//                                    + getString(R.string.permission_set_dialog_msg_body2);
//                            Dialog dlg = new AlertDialog.Builder(this)
//                                    .setTitle(R.string.permission_set_dilog_title)
//                                    .setMessage(msg)
//                                    .setPositiveButton(R.string.permission_set_button, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                            Uri uri = Uri.fromParts("package", "me.shouheng.timepartner", null);
//                                            intent.setData(uri);
//                                            startActivity(intent);
//                                        }
//                                    })
//                                    .setNegativeButton(R.string.com_cancel, null)
//                                    .create();
//                            dlg.show();
//                        } else {
//                            TPDisp.showToast(this, R.string.permission_denied_toast);
//                        }
//                    } else {
//                        TPDisp.showToast(this, R.string.permission_denied_toast);
//                    }
//                }
//                break;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_exit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBack();
                break;
            case R.id.exit:
                TpAlarmManager.cancelAlarmsToday(this);
                UserKeeper.clear(this);
                ActivityManager.finishAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBack(){
        Intent intent = new Intent();
        intent.putExtra("icon_changed", isIconChanged);
        intent.putExtra("name_changed", isNameChanged);
        setResult(RESULT_OK,intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }
}
