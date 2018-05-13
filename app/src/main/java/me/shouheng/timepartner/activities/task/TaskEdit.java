package me.shouheng.timepartner.activities.task;

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
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.Serializable;
import java.util.Calendar;

import me.shouheng.timepartner.database.dao.base.ClassDAO;
import me.shouheng.timepartner.database.dao.base.ExamDAO;
import me.shouheng.timepartner.database.dao.base.TaskDAO;
import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.models.entities.tpclass.ClassEntity;
import me.shouheng.timepartner.models.entities.exam.Exam;
import me.shouheng.timepartner.models.entities.task.Task;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.activities.tpclass.ClassListActivity;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpString;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.custom.CustomActionBar;
import me.shouheng.timepartner.widget.dialog.EdtDialog;
import me.shouheng.timepartner.widget.textview.DateTextView;
import me.shouheng.timepartner.widget.textview.TimeTextView;

public class TaskEdit extends BaseActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private int EDIT_TYPE;

    private final int REQUEST_CODE_CLASS = 1001;
    private String mColor = TpColor.COLOR_TASK;

    private EditText editName;
    private EditText editContent;
    private EditText editComments;

    private CustomActionBar actionBar;

    private DateTextView tvDate;
    private TimeTextView tvTime;

    private TextView tvRoomState;
    private TextView tvSeatState;
    private TextView tvSpanState;
    private TextView tvAssociate;

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    /**
     * 打开的类型有：(这样做是因为当传入t==null的时候不知道是编辑考试还是编辑作业)
     * 1).当t==null, type=_TYPE_TASK_EDIT时编辑作业; type=_TYPE_EXAM_EDIT时编辑考试;
     * 2).当t为ExamEntity时重新编辑考试，当t为TaskEntity时重新编辑作业
     * @param mContext 上下文
     * @param type 类型
     * @param t 泛型
     * @param <T> 限制为BaseEntity的子类 */
    public static <T extends Entity> void edit(Context mContext, int type, T t){
        Intent intent = new Intent(mContext, TaskEdit.class);
        intent.putExtra(Intents.EXTRA_TYPE, type);
        if (t != null)
            intent.putExtra(Intents.EXTRA_ENTITY, t);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_edit_layout);

        ActivityManager.addTaskActivity(this);

        Intent intent = getIntent();
        EDIT_TYPE = intent.getIntExtra(Intents.EXTRA_TYPE, -1);

        initViews();

        if (EDIT_TYPE == Constants.TYPE_EXAM){
            if (intent.hasExtra(Intents.EXTRA_ENTITY)){
                Serializable serializable = intent.getSerializableExtra(Intents.EXTRA_ENTITY);
                if (serializable instanceof Exam){
                    initValues(serializable);
                }
            }
        } else if (EDIT_TYPE == Constants.TYPE_TASK){
            if (intent.hasExtra(Intents.EXTRA_ENTITY)){
                Serializable serializable = intent.getSerializableExtra(Intents.EXTRA_ENTITY);
                if (serializable instanceof Task){
                    initValues(serializable);
                }
            }
        }
    }

    private void initViews(){
        if (EDIT_TYPE == Constants.TYPE_TASK){
            findViewById(R.id.list_exam).setVisibility(View.GONE);
            TextView title = (TextView)findViewById(R.id.title);
            TextView tip1 = (TextView)findViewById(R.id.date_tip);
            TextView tip2 = (TextView)findViewById(R.id.time_tip);
            tip1.setText(R.string.task_edit_date2);
            tip2.setText(R.string.task_edit_time2);
            title.setText(R.string.task_edit_title1);
        }

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

        editName = (EditText) findViewById(R.id.name);
        editContent = (EditText) findViewById(R.id.content);
        editComments = (EditText) findViewById(R.id.comments);

        int[] ids = new int[]{R.id.room, R.id.seat, R.id.span, R.id.set_associate};
        for (int id : ids){
            findViewById(id).setOnClickListener(this);
        }

        tvDate = (DateTextView) findViewById(R.id.date_text_view);
        tvTime = (TimeTextView) findViewById(R.id.time_text_view);
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                false);
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false, false);
        tvDate.setOnDateTextClickListener(new DateTextView.OnDateTextClickListener() {
            @Override
            public void onClick(int year, int month, int day) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(1985, 2037);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });
        tvTime.setOnTimeTextClickListener(new TimeTextView.OnTimeTextClickListener() {
            @Override
            public void onClick(int hourOfDay, int minute) {
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });

        tvRoomState = (TextView) findViewById(R.id.room_state);
        tvSeatState = (TextView) findViewById(R.id.seat_state);
        tvSpanState = (TextView) findViewById(R.id.span_state);
        tvAssociate = (TextView) findViewById(R.id.associate_state);
        tvAssociate.setTextColor(Color.parseColor(mColor));
        tvAssociate.setTag(null);

        String examSpan = "90" + getString(R.string.com_minute);
        tvSpanState.setText(examSpan);                                        //default time span of exam
    }

    private <T>void initValues(T entity){
        TextView tvDelete = (TextView) findViewById(R.id.delete);
        tvDelete.setVisibility(View.GONE);
        if (entity instanceof Task){
            Task taskEntity = (Task) entity;

            editName.setText(taskEntity.getTaskTitle());
            editContent.setText(taskEntity.getTaskContent());
            editComments.setText(taskEntity.getTaskComments());
            tvTime.setTime(taskEntity.getTaskTime());
            tvDate.setDate(taskEntity.getTaskDate());

            ClassDAO classDAO = ClassDAO.getInstance(this);
            long clsId = taskEntity.getClsId();
            ClassEntity classEntity = classDAO.get(clsId);
            classDAO.close();

            if (classEntity != null) {
                mColor = classEntity.getClsColor();
                tvAssociate.setText(classEntity.getClsName());
                tvAssociate.setTag(classEntity);
                int pColor = Color.parseColor(mColor);
                tvAssociate.setTextColor(pColor);
                actionBar.setActionBarColor(pColor);
                TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
            }
        } else if (entity instanceof Exam){
            Exam examEntity = (Exam) entity;

            editName.setText(examEntity.getExamTitle());
            editContent.setText(examEntity.getExamContent());
            editComments.setText(examEntity.getExamComments());
            tvTime.setTime(examEntity.getExamTime());
            tvDate.setDate(examEntity.getExamDate());

            tvRoomState.setText(examEntity.getExamRoom());
            tvSeatState.setText(examEntity.getExamSeat());
            String duration = examEntity.getDuration() + getString(R.string.com_minute);
            tvSpanState.setText(duration);

            ClassDAO classDAO = ClassDAO.getInstance(this);
            long clsId = examEntity.getClsId();
            ClassEntity classEntity = classDAO.get(clsId);
            classDAO.close();

            if (classEntity != null) {
                mColor = classEntity.getClsColor();
                tvAssociate.setText(classEntity.getClsName());
                tvAssociate.setTag(classEntity);
                int pColor = Color.parseColor(mColor);
                tvAssociate.setTextColor(pColor);
                actionBar.setActionBarColor(pColor);
                TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_associate:
                ClassListActivity.startForValue(this, REQUEST_CODE_CLASS);
                break;
            case R.id.room:
                showEditDialog(getString(R.string.exam_room_title), getString(R.string.exam_room_edit_hint), tvRoomState);
                break;
            case R.id.seat:
                showEditDialog(getString(R.string.exam_seat_title), getString(R.string.exam_seat_edit_hint), tvSeatState);
                break;
            case R.id.span:
                showNumDialog(getString(R.string.exam_span_title), getString(R.string.exam_span_edit_hint), tvSpanState);
                break;
            case R.id.delete:
                onDelete();
                break;
        }
    }

    private void onBack(){
        if (editName.getText().toString().isEmpty()
                && editContent.getText().toString().isEmpty()
                && editComments.getText().toString().isEmpty()
                && tvRoomState.getText().toString().isEmpty()
                && tvSeatState.getText().toString().isEmpty()
                && tvAssociate.getText().toString().isEmpty()) {
            this.finish();
        } else {
            Dialog dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.com_tip)
                    .setMessage(R.string.com_back_tip)
                    .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TaskEdit.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.com_cancel, null)
                    .create();
            dlg.show();
        }
    }

    private void onSave(){
        String itemName = editName.getText().toString();
        if (TextUtils.isEmpty(itemName)){
            switch (EDIT_TYPE){
                case Constants.TYPE_EXAM:
                    makeToast(R.string.exam_save_tip);
                    break;
                case Constants.TYPE_TASK:
                    makeToast(R.string.task_save_tip);
                    break;
            }
        } else {
            if (EDIT_TYPE == Constants.TYPE_TASK){
                ClassEntity classEntity = null;
                Object obj = tvAssociate.getTag();
                if (obj != null && obj instanceof ClassEntity){
                    classEntity = (ClassEntity) obj;
                }

                Task taskEntity;
                if (getIntent().hasExtra(Intents.EXTRA_ENTITY)){
                    taskEntity = (Task) getIntent().getSerializableExtra(Intents.EXTRA_ENTITY);
                } else {
                    taskEntity = new Task();
                    taskEntity.setTaskId(TpTime.getLongId());
                    taskEntity.setAddedDate(TpTime.millisOfCurrentDate());
                    taskEntity.setAddedTime(TpTime.millisOfCurrentTime());
                }

                if (classEntity != null){
                    taskEntity.setClsId(classEntity.getClsId());
                }
                taskEntity.setAccount(UserKeeper.getUser(this).getAccount());
                taskEntity.setTaskTitle(editName.getText().toString());
                taskEntity.setTaskDate(tvDate.getMillisOfDate());
                taskEntity.setTaskTime(tvTime.getMillisOfTime());
                taskEntity.setTaskContent(editContent.getText().toString());
                taskEntity.setTaskComments(editComments.getText().toString());
                taskEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
                taskEntity.setLastModifyTime(TpTime.millisOfCurrentTime());
                taskEntity.setSynced(0);

                TaskDAO taskDAO = TaskDAO.getInstance(this);
                if (getIntent().hasExtra(Intents.EXTRA_ENTITY)){
                    taskDAO.update(taskEntity);
                } else {
                    taskDAO.insert(taskEntity);
                }
                taskDAO.close();

                TaskViewer.view(this, taskEntity);
            } else if (EDIT_TYPE == Constants.TYPE_EXAM){
                ClassEntity classEntity = null;
                Object obj = tvAssociate.getTag();
                if (obj != null && obj instanceof ClassEntity){
                    classEntity = (ClassEntity) obj;
                }

                Exam examEntity;
                if (getIntent().hasExtra(Intents.EXTRA_ENTITY)){
                    examEntity = (Exam) getIntent().getSerializableExtra(Intents.EXTRA_ENTITY);
                } else {
                    examEntity = new Exam();
                    examEntity.setExamId(TpTime.getLongId());
                    examEntity.setAddedDate(TpTime.millisOfCurrentDate());
                    examEntity.setAddedTime(TpTime.millisOfCurrentTime());
                }

                if (classEntity != null){
                    examEntity.setClsId(classEntity.getClsId());
                }
                examEntity.setAccount(UserKeeper.getUser(this).getAccount());
                examEntity.setExamTitle(editName.getText().toString());
                examEntity.setExamDate(tvDate.getMillisOfDate());
                examEntity.setExamTime(tvTime.getMillisOfTime());
                examEntity.setExamRoom(tvRoomState.getText().toString());
                examEntity.setExamSeat(tvSeatState.getText().toString());
                examEntity.setDuration(Integer.parseInt(TpString.parseNumber(tvSpanState.getText().toString())));
                examEntity.setExamContent(editContent.getText().toString());
                examEntity.setExamComments(editComments.getText().toString());
                examEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
                examEntity.setLastModifyTime(TpTime.millisOfCurrentTime());
                examEntity.setSynced(0);

                ExamDAO examDAO = ExamDAO.getInstance(this);
                if (getIntent().getSerializableExtra(Intents.EXTRA_ENTITY) == null){
                    examDAO.insert(examEntity);
                    makeToast(R.string.com_toast_save_success);
                } else {
                    examDAO.update(examEntity);
                    makeToast(R.string.com_toast_update_success);
                }
                examDAO.close();

                //                    AlarmEntity alarmEntity = AlarmLoader.wrapTaskEntity(
//                            taskEntity, dbManager.getClass(clsId));
//                    if (alarmEntity != null) {
//                        TpAlarmManager.setAlarm(this, alarmEntity);
//                    }

//                    AlarmEntity alarmEntity2 = AlarmLoader.wrapExamEntity(
//                            examEntity, dbManager.getClass(clsId2));
//                    if (alarmEntity2 != null) {
//                        TpAlarmManager.setAlarm(this, alarmEntity2);
//                    }

                TaskViewer.view(this, examEntity);
            }
        }
    }

    /**
     * 显示一个自定义编辑对话框
     * @param strTitle 对话框的标题
     * @param strHint 编辑框的提示
     * @param tvBased 编辑要设置的组件 */
    private void showNumDialog(String strTitle, String strHint, final TextView tvBased){
        EdtDialog dialog = new EdtDialog(this,
                EdtDialog.DIALOG_TYPE_DIGITAL,
                TpString.parseNumber(tvBased.getText().toString()),
                strTitle, strHint);
        dialog.setNegativeListener(R.string.com_cancel, null);
        dialog.setPositiveButton(R.string.com_confirm, new EdtDialog.PositiveButtonClickListener() {
            @Override
            public void onClick(String content) {
                if (!TextUtils.isEmpty(content)){
                    String str = content + getString(R.string.com_minute);
                    tvBased.setText(str);
                }
            }
        });
        dialog.setSingleLine(true);
        dialog.show();
    }

    /**
     * 显示一个普通编辑对话框
     * @param strTitle 标题
     * @param strHint 提示信息
     * @param tvBased 该对话框编辑基于的组件 */
    private void showEditDialog(String strTitle, String strHint, final TextView tvBased){
        EdtDialog dialog = new EdtDialog(this,
                EdtDialog.DIALOG_TYPE_NORMAL,
                tvBased.getText().toString(),
                strTitle, strHint);
        dialog.setPositiveButton(R.string.com_confirm, new EdtDialog.PositiveButtonClickListener() {
            @Override
            public void onClick(String content) {
                tvBased.setText(content);
            }
        });
        dialog.setNegativeListener(R.string.com_cancel, null);
        dialog.show();
    }

    private void onDelete(){
        switch (EDIT_TYPE){
            case Constants.TYPE_EXAM:
                Exam examEntity = (Exam) getIntent()
                        .getSerializableExtra(Intents.EXTRA_ENTITY);

                ExamDAO examDAO = ExamDAO.getInstance(this);
                examDAO.delete(examEntity);
                makeToast(R.string.com_toast_delete_success2);
                ActivityManager.finishAllTasks();
                examDAO.close();
                break;
            case Constants.TYPE_TASK:
                Task taskEntity = (Task) getIntent()
                        .getSerializableExtra(Intents.EXTRA_ENTITY);

                TaskDAO taskDAO = TaskDAO.getInstance(this);
                taskDAO.delete(taskEntity);
                makeToast(R.string.com_toast_delete_success2);
                ActivityManager.finishAllTasks();
                taskDAO.close();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_CLASS:
                if (resultCode == RESULT_OK){
                    ClassEntity classEntity = (ClassEntity) data.getSerializableExtra(Intents.EXTRA_ENTITY);

                    mColor = classEntity.getClsColor();
                    int pColor = Color.parseColor(mColor);
                    actionBar.setActionBarColor(pColor);

                    String clsName = classEntity.getClsName();
                    tvAssociate.setTextColor(pColor);
                    tvAssociate.setText(clsName);
                    tvAssociate.setTag(classEntity);

                    TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        tvDate.setDate(TpTime.getMillisFromDate(year, month, day));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        tvTime.setTime(TpTime.getMillisFromTime(hourOfDay, minute));
    }
}