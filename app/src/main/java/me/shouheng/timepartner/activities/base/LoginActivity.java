package me.shouheng.timepartner.activities.base;

import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.widget.custom.TPRippleButton;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editPassword;
    private EditText editAccount;

    private final int MSG_LN = 1001;
    private final int MSG_REG = 1002;

    private final String KEY_STATUS = "STATUS";
    private final String KEY_ACCOUNT = "ACCOUNT";
    private final String KEY_PASSWORD = "PASSWORD";

    private Dialog regDlg;

//    private AuthInfo mAuthInfo;
//    private Oauth2AccessToken mAccessToken;
//    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_login_layout);

        ActivityManager.addActivity(this);

        initViews();

        // 第一次启动本应用，AccessToken 不可用
//        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
//        mSsoHandler = new SsoHandler(ActivityLogin.this, mAuthInfo);
//        mAccessToken = WeiboTokenKeeper.readAccessToken(this);
    }

    private void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editAccount = (EditText) findViewById(R.id.in_account);
        editPassword = (EditText)findViewById(R.id.in_password);

        final View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_register_layout, null);
        regDlg = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(R.string.rgst_title)
                .setView(view)
                .create();
        regDlg.setCancelable(false);
        view.findViewById(R.id.cfm_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPsd = ((EditText) view.findViewById(R.id.in_psd)).getText().toString();
                String strEmail = ((EditText) view.findViewById(R.id.in_account)).getText().toString();
                onConfirmResister(v, strEmail, strPsd);
            }
        });
        view.findViewById(R.id.cancel_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regDlg != null && regDlg.isShowing()) {
                    regDlg.dismiss();
                }
            }
        });
        TPRippleButton tprRegister = (TPRippleButton) findViewById(R.id.register);
        tprRegister.setOnRippleClickListener(new TPRippleButton.OnRippleClickListener() {
            @Override
            public void onClick(View v) {
                if (regDlg != null){
                    regDlg.show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                onConfirmLogin(v);
                break;
            case R.id.weibo:
//                mSsoHandler.authorize(new AuthListener());
                break;
        }
    }

    private void onConfirmResister(final View v, final String strEmail, final String strPsd){
        if(TextUtils.isEmpty(strEmail)){
            showSnackBar(v, R.string.empty_account_toast);
        } else if (TextUtils.isEmpty(strPsd)){
            showSnackBar(v, R.string.empty_psd_toast);
        } else {
            UserKeeper.register(this, strEmail, strPsd, new UserKeeper.AccountValidCallback() {
                @Override
                public void onFinish(String result) {
                    Bundle data = new Bundle();
                    data.putString(KEY_STATUS, result);
                    data.putString(KEY_ACCOUNT, strEmail);
                    data.putString(KEY_PASSWORD, strPsd);
                    Message msg = Message.obtain();
                    msg.what = MSG_REG;
                    msg.obj = v;
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                @Override
                public void onError(Exception e) {
                    Bundle data = new Bundle();
                    data.putString(KEY_STATUS, UserKeeper.RegInfo.REG_ERR);
                    Message msg = Message.obtain();
                    msg.what = MSG_REG;
                    msg.obj = v;
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
        }
    }

    private void onConfirmLogin(final View v){
        String strAccount = editAccount.getText().toString();
        String strPassword = editPassword.getText().toString();
        if(TextUtils.isEmpty(strAccount)){
            showSnackBar(v, R.string.account_toast);
        } else if (TextUtils.isEmpty(strPassword)){
            showSnackBar(v, R.string.password_toast);
        } else {
            UserKeeper.login(this, strAccount, strPassword, new UserKeeper.AccountValidCallback() {
                @Override
                public void onFinish(String result) {
                    Bundle data = new Bundle();
                    data.putString(KEY_STATUS, result);
                    Message msg = new Message();
                    msg.what = MSG_LN;
                    msg.obj = v;
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                @Override
                public void onError(Exception e) {
                    Bundle data = new Bundle();
                    data.putString(KEY_STATUS, UserKeeper.LoginInfo.LN_ERR);
                    Message msg = new Message();
                    msg.what = MSG_LN;
                    msg.obj = v;
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            });
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           handle(msg);
        }
    };

    private void handle(Message msg){
        View v = (View) msg.obj;
        switch (msg.what){
            case MSG_LN:
                switch (msg.getData().getString(KEY_STATUS, "")){
                    case UserKeeper.LoginInfo.OFFLINE:
                        showSnackBar(v, R.string.check_net_toast);
                        break;
                    case UserKeeper.LoginInfo.ERR_NONE_EXIST:
                        showSnackBar(v, R.string.account_none_exist_toast);
                        break;
                    case UserKeeper.LoginInfo.ERR_WRONG_PSD:
                        showSnackBar(v, R.string.password_error_toast);
                        break;
                    case UserKeeper.LoginInfo.LN_ERR:
                        showSnackBar(v, R.string.known_error);
                        break;
                    case UserKeeper.LoginInfo.LN_OK:
                        MainActivity.activityStart(LoginActivity.this, editAccount.getText().toString());
                }
                break;
            case MSG_REG:
                switch (msg.getData().getString(KEY_STATUS, "")){
                    case UserKeeper.RegInfo.ERR_WRONG_FORMAT:
                        showSnackBar(v, R.string.email_format);
                        break;
                    case UserKeeper.RegInfo.ERR_PSD_LONG:
                        showSnackBar(v, R.string.psd_long);
                        break;
                    case UserKeeper.RegInfo.ERR_PSD_SHORT:
                        showSnackBar(v, R.string.psd_short);
                        break;
                    case UserKeeper.RegInfo.ERR_PSD_SIMPLE:
                        showSnackBar(v, R.string.psd_numeric);
                        break;
                    case UserKeeper.RegInfo.ERR_PSD_ILLEGAL:
                        showSnackBar(v, R.string.psd_Ill_char);
                        break;
                    case UserKeeper.RegInfo.OFFLINE:
                        showSnackBar(v, R.string.check_net);
                        break;
                    case UserKeeper.RegInfo.ERR_ACC_EXISTED:
                        showSnackBar(v, R.string.email_registered);
                        break;
                    case UserKeeper.RegInfo.REG_OK:
                        editAccount.setText(msg.getData().getString(KEY_ACCOUNT, ""));
                        editPassword.setText(msg.getData().getString(KEY_PASSWORD, ""));
                        if (regDlg != null && regDlg.isShowing()){
                            regDlg.dismiss();
                        }
                        showSnackBar(v, R.string.rgst_success);
                        break;
                    case UserKeeper.RegInfo.REG_ERR:
                        showSnackBar(v, R.string.known_error);
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ActivityManager.finishAll();
    }

//    class AuthListener implements WeiboAuthListener {
//
//        @Override
//        public void onComplete(Bundle values) {
//            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
//            if (mAccessToken.isSessionValid()) {
//                WeiboTokenKeeper.writeAccessToken(ActivityLogin.this, mAccessToken);
//                TPDisp.showToast(ActivityLogin.this, "授权成功");
//                //AccountKeeper.regist();
//               // AccountKeeper.keep(ActivityLogin.this, String.valueOf(AccountKeeper.generateUID(ActivityLogin.this)), "");
//            } else {
//                String code = values.getString("code");
//                String message = "授权失败";
//                if (!TextUtils.isEmpty(code)) {
//                    message = message + "\nObtained the code: " + code;
//                }
//                TPDisp.showToast(ActivityLogin.this, message);
//            }
//        }
//
//        @Override
//        public void onCancel() {
//            TPDisp.showToast(ActivityLogin.this, "取消授权");
//        }
//
//        @Override
//        public void onWeiboException(WeiboException e) {
//            TPDisp.showToast(ActivityLogin.this, "Auth exception : " + e.getMessage());
//        }
//    }

    private void showSnackBar(View view, int res){
        Snackbar.make(view, res, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}
