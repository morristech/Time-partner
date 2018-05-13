package me.shouheng.timepartner.activities.base;

import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.models.entities.user.User;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.managers.ActivityManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_welcome_layout);

        ActivityManager.addActivity(this);

        initViews();

        String account = UserKeeper.getUser().getAccount();
        if (account.equals("")){
            // 使用本地账号登录
            User user = new User();
            user.setAccount("local");
            user.setToken(0L);
            UserKeeper.keep(this, user);
            account = "local";
        }
        MainActivity.activityStart(this, account);
    }

    /**
     * 初始化组件
     */
    private void initViews(){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.welcome_in);
        TextView tvSkip = (TextView) findViewById(R.id.skip);
        tvSkip.setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        TextView tvScript = (TextView) findViewById(R.id.script);
        ImageView ivicon = (ImageView) findViewById(R.id.img);
        tvTitle.startAnimation(anim);
        ivicon.startAnimation(anim);
        tvScript.setText(R.string.welcome_script );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.skip:
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
