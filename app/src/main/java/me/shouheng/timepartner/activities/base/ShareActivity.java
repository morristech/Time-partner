package me.shouheng.timepartner.activities.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpShare;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etInShareWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_before_share);
        // 设置剩余可以输入的文字的数量
        final TextView tvWordsLeft = (TextView) findViewById(R.id.words_left);
        etInShareWords = (EditText) findViewById(R.id.share_words);
        etInShareWords.setText(R.string.default_share_words);//设置默认的分享文字
        int defaultLength = 140 - getString(R.string.default_share_words).length();
        tvWordsLeft.setText(String.valueOf(defaultLength));//设置可以输入的文字的默认数量
        // 监听EditText的事件，文字改变时，改变剩余可以输入的文字的数量
        etInShareWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                // 计算剩余可以输入的文字的数量
                int wordsLeft = 140 - s.length();
                tvWordsLeft.setText(String.valueOf(wordsLeft));
            }
        });
        // 设置按钮的监听事件
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share:
                // 分享APP
                TpShare.shareApp(this, etInShareWords.getText().toString());
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }
}
