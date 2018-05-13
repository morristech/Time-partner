package me.shouheng.timepartner.activities.tpclass;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.widget._classes.ClassDetailEditor;
import me.shouheng.timepartner.widget._classes.ClassDetailView;
import me.shouheng.timepartner.database.dao.bo.ClassBoDAO;
import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.tpclass.ClassBO;
import me.shouheng.timepartner.models.entities.tpclass.ClassDetail;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.custom.ColorPickerView;
import me.shouheng.timepartner.widget.custom.CustomActionBar;
import me.shouheng.timepartner.widget.textview.DateTextView;

public class ClassEditActivity extends BaseActivity implements View.OnClickListener, ColorPickerView.OnColorSelectedListener{

    private EditText editName;
    private DateTextView tvEndDate, tvStartDate;
    private ColorPickerView setColor;
    private CustomActionBar actionBar;
    private LinearLayout detailsLayout;
    private List<ClassDetailView> detailsList = new LinkedList<>();

    private String mColor = TpColor.COLOR_CLASS;

    public static final String DATEPICKER1_TAG = "datepicker1", DATEPICKER2_TAG = "datepicker2";
    public static final String TIMEPICKER1_TAG = "timepicker1", TIMEPICKER2_TAG = "timepicker2";

    public static void edit(Context mContext, ClassBO classBO){
        Intent intent = new Intent(mContext, ClassEditActivity.class);
        if (classBO != null){
            intent.putExtra(Intents.EXTRA_ENTITY, classBO);
        }
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_edit_layout);

        ActivityManager.addClassActivity(this);

        initViews();

        Intent intent = getIntent();
        if (intent.hasExtra(Intents.EXTRA_ENTITY)){
            Serializable serializable = intent.getSerializableExtra(Intents.EXTRA_ENTITY);
            if (serializable instanceof ClassBO){
                ClassBO classBO = (ClassBO) serializable;
                initValues(classBO);
            }
        }
    }

    private void initViews() {
        actionBar = (CustomActionBar) findViewById(R.id.action_bar);
        actionBar.setOnBackButtonClickListener(new CustomActionBar.OnBackButtonClickListener() {
            @Override
            public void onClick() {
                onBack();
            }
        });
        actionBar.setOnHeadButtonClickListener(new CustomActionBar.OnHeadButtonClickListener() {
            @Override
            public void onClick() {
                onSave();
            }
        });

        editName = (EditText) findViewById(R.id.class_name); // 名称编辑组件
        setColor = (ColorPickerView) findViewById(R.id.set_color); // 设置颜色
        setColor.setOnColorSelectedListener(this);

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog1 = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        if (tvStartDate != null){
                            tvStartDate.setDate(TpTime.getMillisFromDate(year, month, day));
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                false);
        final DatePickerDialog datePickerDialog2 = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        if (tvEndDate != null){
                            tvEndDate.setDate(TpTime.getMillisFromDate(year, month, day));
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                false);
        tvStartDate = (DateTextView) findViewById(R.id.start_date_text_view);
        tvStartDate.setOnDateTextClickListener(new DateTextView.OnDateTextClickListener() {
            @Override
            public void onClick(int year, int month, int day) {
                datePickerDialog1.setVibrate(false);
                datePickerDialog1.setYearRange(1985, 2037);
                datePickerDialog1.setCloseOnSingleTapDay(false);
                datePickerDialog1.show(getSupportFragmentManager(), DATEPICKER1_TAG);
            }
        });
        tvEndDate = (DateTextView) findViewById(R.id.end_date_text_view);
        tvEndDate.setOnDateTextClickListener(new DateTextView.OnDateTextClickListener() {
            @Override
            public void onClick(int year, int month, int day) {
                datePickerDialog2.setVibrate(false);
                datePickerDialog2.setYearRange(1985, 2037);
                datePickerDialog2.setCloseOnSingleTapDay(false);
                datePickerDialog2.show(getSupportFragmentManager(), DATEPICKER2_TAG);
            }
        });

        findViewById(R.id.add_item).setOnClickListener(this);
        detailsLayout = (LinearLayout) findViewById(R.id.list_item);
    }

    private void initValues(ClassBO classBO){
        TextView tvDelete = (TextView) findViewById(R.id.delete);
        tvDelete.setVisibility(View.VISIBLE);
        tvDelete.setOnClickListener(this);

        ClassEntity clsEntity = classBO.getClassEntity();
        if (clsEntity != null){
            String clsColor = clsEntity.getClsColor();
            if (!TextUtils.isEmpty(clsColor)){
                mColor = clsColor;
                int resId = TpColor.getColorResource(clsColor);
                if (resId != -1){
                    setColor.setImageResource(resId);
                }
                int pColor = Color.parseColor(clsColor);
                actionBar.setActionBarColor(pColor);
                TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
            }

            String clsName = clsEntity.getClsName();
            editName.setText(clsName);

            tvStartDate.setDate(clsEntity.getStartDate());
            tvEndDate.setDate(clsEntity.getEndDate());
        }

        List<ClassDetail> details = classBO.getDetails();
        for (ClassDetail entity : details){
            addDetail(entity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_item:
                final ClassDetailEditor dlg = new ClassDetailEditor(this);
                dlg.showDialog();
                final Calendar calendar = Calendar.getInstance();
                final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                dlg.setStartTime(
                                        TpTime.getFormatTime(
                                                TpTime.getMillisFromTime(hourOfDay, minute)));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false, false);
                final TimePickerDialog timePickerDialog2 = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                dlg.setEndTime(
                                        TpTime.getFormatTime(
                                                TpTime.getMillisFromTime(hourOfDay, minute)));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false, false);
                dlg.setOnSTimeTextClickListener(new ClassDetailEditor.OnSTimeTextClickListener() {
                    @Override
                    public void onClick(int hourOfDay, int minute) {
                        timePickerDialog.setVibrate(false);
                        timePickerDialog.setCloseOnSingleTapMinute(false);
                        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER1_TAG);
                    }
                });
                dlg.setOnETimeTextClickListener(new ClassDetailEditor.OnETimeTextClickListener() {
                    @Override
                    public void onClick(int hourOfDay, int minute) {
                        timePickerDialog2.setVibrate(false);
                        timePickerDialog2.setCloseOnSingleTapMinute(false);
                        timePickerDialog2.show(getSupportFragmentManager(), TIMEPICKER2_TAG);
                    }
                });
                dlg.setOnEditCompletedListener(new ClassDetailEditor.OnEditCompletedListener() {
                    @Override
                    public void onCompleted(boolean isNew, final ClassDetail entity) {
                        addDetail(entity);
                    }
                });
                break;
            case R.id.delete:// 删除
                onDelete();
                break;
        }
    }

    private void onDelete(){
        Dialog dlg1 = new AlertDialog.Builder(ClassEditActivity.this)
                .setTitle(R.string.com_tip)
                .setMessage(R.string.clse_del_msg)
                .setPositiveButton(R.string.com_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClassBO classBO = (ClassBO) getIntent().getSerializableExtra(Intents.EXTRA_ENTITY);
                        ClassBoDAO classBoDAO = ClassBoDAO.getInstance(ClassEditActivity.this);
                        classBoDAO.trash(classBO, ClassEntity.TrashType.WITHOUT_TASK_EXAM);
                        classBoDAO.close();
                        ActivityManager.finishAllClasses();
                        makeToast(R.string.com_toast_delete_success2);
                    }
                })
                .setNegativeButton(R.string.com_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClassBO classBO = (ClassBO) getIntent().getSerializableExtra(Intents.EXTRA_ENTITY);
                        ClassBoDAO classBoDAO = ClassBoDAO.getInstance(ClassEditActivity.this);
                        classBoDAO.trash(classBO, ClassEntity.TrashType.WITH_TASK_EXAM);
                        classBoDAO.close();
                        ActivityManager.finishAllClasses();
                        makeToast(R.string.com_toast_delete_success2);
                    }
                })
                .create();
        dlg1.show();
    }

    private void onSave(){
        String strTitle = editName.getText().toString();
        if (TextUtils.isEmpty(strTitle)){
            makeToast(R.string.class_save_tip2);
        } else if (detailsList.size() == 0) {
            makeToast(R.string.class_save_tip1);
        } else {
            // 判断日期是否正确
            long sl = tvStartDate.getMillisOfDate();
            long el = tvEndDate.getMillisOfDate();
            if (sl > el){
                makeToast(R.string.clse_d_error_msg);
            } else {
                ClassEntity classEntity;
                ClassBO classBO;
                if (getIntent().hasExtra(Intents.EXTRA_ENTITY)){
                    classBO = (ClassBO) getIntent().getSerializableExtra(Intents.EXTRA_ENTITY);
                    classEntity = classBO.getClassEntity();
                } else {
                    classBO = new ClassBO();
                    classEntity = new ClassEntity();
                    classEntity.setClsId(TpTime.getLongId());
                    classEntity.setAddedDate(TpTime.millisOfCurrentDate());
                    classEntity.setAddedTime(TpTime.millisOfCurrentTime());
                }

                long clsId = classEntity.getClsId();
                String strAccount = UserKeeper.getUser(this).getAccount();

                classEntity.setAccount(strAccount);
                classEntity.setClsColor(mColor);
                classEntity.setClsName(editName.getText().toString());
                classEntity.setStartDate(sl);
                classEntity.setEndDate(el);
                classEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
                classEntity.setLastModifyTime(TpTime.millisOfCurrentTime());
                classEntity.setSynced(0);

                List<ClassDetail> list = new LinkedList<>();
                for (ClassDetailView view : detailsList){
                    ClassDetail entity = view.getEntity();
                    entity.setClassId(clsId);
                    entity.setAccount(strAccount);
                    entity.setSynced(0);
                    list.add(entity);
                }
                classBO.setClassEntity(classEntity);
                classBO.setDetails(list);
//                List<AlarmEntity> alarmEntities =
//                        AlarmLoader.wrapClassEntity(entity, list);
//                for (AlarmEntity alarmEntity : alarmEntities){
//                    TpAlarmManager.setAlarm(this, alarmEntity);
//                }
                ClassBoDAO classBoDAO = ClassBoDAO.getInstance(this);
                if (getIntent().hasExtra(Intents.EXTRA_ENTITY)){
                    classBoDAO.update(classBO);
                    makeToast(R.string.com_toast_update_success);
                } else {
                    classBoDAO.insert(classBO);
                    makeToast(R.string.com_toast_save_success);
                }
                classBoDAO.close();

                ClassViewActivity.view(this, classBO);
            }
        }
    }

    private void onBack() {
        String strTitle = editName.getText().toString();
        if (TextUtils.isEmpty(strTitle) && detailsList.size() == 0){
            finish();
        } else {
            // 退出对话框
            Dialog dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.com_tip)
                    .setMessage(R.string.com_back_tip)
                    .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClassEditActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.com_cancel, null)
                    .create();
            dlg.show();
        }
    }

    private void addDetail(ClassDetail entity){
        final ClassDetailView item = new ClassDetailView(this);
        item.setColor(mColor);
        item.setDeleteListener(new ClassDetailView.OnDeleteListener() {
            @Override
            public void onDeleteClick() {
                Dialog dlg2 = new AlertDialog.Builder(ClassEditActivity.this)
                        .setTitle(R.string.clse_del_dlg_title)
                        .setMessage(R.string.clse_del_dlg_content)
                        .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                detailsLayout.removeView(item);
                                detailsList.remove(item);
                            }
                        })
                        .setNegativeButton(R.string.com_cancel, null)
                        .create();
                dlg2.show();
            }
        });
        item.setEditListener(new ClassDetailView.OnEditListener() {
            @Override
            public void onEditClick() {
                final ClassDetailEditor dlg = new ClassDetailEditor(ClassEditActivity.this);
                dlg.setEntity(item.getEntity());
                dlg.showDialog();
                final Calendar calendar = Calendar.getInstance();
                final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                dlg.setStartTime(
                                        TpTime.getFormatTime(
                                                TpTime.getMillisFromTime(hourOfDay, minute)));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false, false);
                final TimePickerDialog timePickerDialog2 = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                dlg.setEndTime(
                                        TpTime.getFormatTime(
                                                TpTime.getMillisFromTime(hourOfDay, minute)));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false, false);
                dlg.setOnSTimeTextClickListener(new ClassDetailEditor.OnSTimeTextClickListener() {
                    @Override
                    public void onClick(int hourOfDay, int minute) {
                        timePickerDialog.setVibrate(false);
                        timePickerDialog.setCloseOnSingleTapMinute(false);
                        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER1_TAG);
                    }
                });
                dlg.setOnETimeTextClickListener(new ClassDetailEditor.OnETimeTextClickListener() {
                    @Override
                    public void onClick(int hourOfDay, int minute) {
                        timePickerDialog2.setVibrate(false);
                        timePickerDialog2.setCloseOnSingleTapMinute(false);
                        timePickerDialog2.show(getSupportFragmentManager(), TIMEPICKER2_TAG);
                    }
                });
                dlg.setOnEditCompletedListener(new ClassDetailEditor.OnEditCompletedListener() {
                    @Override
                    public void onCompleted(boolean isNew, ClassDetail entity) {
                        // 更新值
                        item.setEntity(entity);
                    }
                });
            }
        });
        item.setEntity(entity);
        detailsLayout.addView(item);
        detailsList.add(item);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onColorSelected(int resColor, String strColor) {
        if (null != strColor){
            mColor = strColor;
            int pColor = Color.parseColor(mColor);
            TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
            actionBar.setActionBarColor(pColor);
            for (ClassDetailView view : detailsList){
                view.setColor(pColor);
            }
        }
        if (-1 != resColor)
            setColor.setImageResource(resColor);
    }
}
