package me.shouheng.timepartner.selector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import java.io.File;
import java.util.ArrayList;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.utils.TpDisp;

public class MultiImageSelectorActivity extends FragmentActivity
        implements MultiImageSelectorFragment.Callback, View.OnClickListener{

    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;

    private static final int DEFAULT_IMAGE_SIZE = 9;

    private ArrayList<String> resultList = new ArrayList<>();

    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_activity_default);

        TpDisp.setStatusBarColor(this, Color.parseColor("#37474F"), TpDisp.DEFAULT_COLOR_ALPHA);

        Intent intent = getIntent();
        int mDefaultCount = intent.getIntExtra(Intents.EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
        int mode = intent.getIntExtra(Intents.EXTRA_SELECT_MODE, 1);
        boolean isShow = intent.getBooleanExtra(Intents.EXTRA_SHOW_CAMERA, true);
        if ((mode == 1) && (intent.hasExtra(Intents.EXTRA_DEFAULT_SELECTED_LIST))) {
            this.resultList = intent.getStringArrayListExtra(Intents.EXTRA_DEFAULT_SELECTED_LIST);
        }

        this.mSubmitButton = ((Button) findViewById(R.id.commit));
        LinearLayout llTopBar = ((LinearLayout) findViewById(R.id.topbar));
        if (mode == 1) {
            updateDoneText(this.resultList);
            this.mSubmitButton.setVisibility(View.VISIBLE);
            this.mSubmitButton.setOnClickListener(this);
        } else {
            llTopBar.setVisibility(View.GONE);
            this.mSubmitButton.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(Intents.EXTRA_SELECT_COUNT, mDefaultCount);
            bundle.putInt(Intents.EXTRA_SELECT_MODE, mode);
            bundle.putBoolean(Intents.EXTRA_SHOW_CAMERA, isShow);
            bundle.putStringArrayList(Intents.EXTRA_DEFAULT_SELECTED_LIST, this.resultList);

            // 使用指定的碎片的名称实例化碎片，并添加到界面中
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_grid, Fragment.instantiate(this,
                            MultiImageSelectorFragment.class.getName(), bundle))
                    .commit();
        }
    }

    private void updateDoneText(ArrayList<String> resultList) {
        int size = 0;
        if ((resultList == null) || (resultList.size() <= 0)) {
            this.mSubmitButton.setText(R.string.mis_action_done);
            this.mSubmitButton.setEnabled(false);
        } else {
            size = resultList.size();
            this.mSubmitButton.setEnabled(true);
        }
        this.mSubmitButton.setText(
                String.format(getString(R.string.mis_action_select_down),
                        new Object[] {Integer.valueOf(size)}));
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        this.resultList.add(path);
        data.putStringArrayListExtra(Intents.EXTRA_RESULT, this.resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if (!this.resultList.contains(path)) {
            this.resultList.add(path);
        }
        updateDoneText(this.resultList);
    }

    @Override
    public void onImageUnselected(String path) {
        if (this.resultList.contains(path)) {
            this.resultList.remove(path);
        }
        updateDoneText(this.resultList);
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE",
                    Uri.fromFile(imageFile)));

            Intent data = new Intent();
            this.resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(Intents.EXTRA_RESULT, this.resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.commit:
                if ((resultList != null) && (resultList.size() > 0)) {
                    Intent data = new Intent();
                    data.putStringArrayListExtra(Intents.EXTRA_DEFAULT_SELECTED_LIST, resultList);
                    setResult(Activity.RESULT_OK, data);
                } else {
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();
                break;
        }
    }
}
