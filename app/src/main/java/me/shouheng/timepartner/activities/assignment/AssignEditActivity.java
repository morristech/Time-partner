package me.shouheng.timepartner.activities.assignment;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.shouheng.timepartner.widget.assignments.SubAssignEditor;
import me.shouheng.timepartner.widget.assignments.SubAssignView;
import me.shouheng.timepartner.database.dao.bo.AssignBoDAO;
import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.assignment.AssignmentBO;
import me.shouheng.timepartner.models.entities.assignment.Assignment;
import me.shouheng.timepartner.models.entities.assignment.SubAssignment;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.custom.ColorPickerView;
import me.shouheng.timepartner.widget.custom.CustomActionBar;
import me.shouheng.timepartner.widget.dialog.EdtDialog;
import me.shouheng.timepartner.widget.textview.DateTextView;
import me.shouheng.timepartner.widget.textview.TimeTextView;

public class AssignEditActivity extends BaseActivity implements View.OnClickListener, ColorPickerView.OnColorSelectedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private EditText editName;
    private EditText editContent;
    private DateTextView tvStartDate;
    private TimeTextView tvStartTime;
    private TextView tvCommentState;
    private TextView tvSubAssignsState;
    private CustomActionBar actionBar;
    private ColorPickerView ivSetColor;
    private LinearLayout subLayout;

    private String mColor = TpColor.COLOR_ASSIGN;

    private List<SubAssignView> subList = new ArrayList<>(); // Sub-Assignments Views List

    public static final String DATEPICKER_TAG = "datepicker", DATEPICKER_TAG2 = "datepicker2";
    public static final String TIMEPICKER_TAG = "timepicker", TIMEPICKER_TAG2 = "timepicker2";

    public static void edit(Context mContext, AssignmentBO assignBO){
        Intent intent = new Intent(mContext, AssignEditActivity.class);
        if (assignBO != null){
            intent.putExtra(Intents.EXTRA_ENTITY, assignBO);
        }
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_edit_layout);
        ActivityManager.addAssignActivity(this);

        initViews();

        AssignmentBO assignBO = getAssignBo();
        if (assignBO != null) {
            initValues(assignBO);
        }

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
            TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }
    }

    private void initViews(){
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

        findViewById(R.id.add_comments).setOnClickListener(this);
        findViewById(R.id.add_sub_assigns).setOnClickListener(this);
        tvStartDate = (DateTextView) findViewById(R.id.date_text_view);
        tvStartTime = (TimeTextView) findViewById(R.id.time_text_view) ;

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
        tvStartDate.setOnDateTextClickListener(new DateTextView.OnDateTextClickListener() {
            @Override
            public void onClick(int year, int month, int day) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(1985, 2037);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });
        tvStartTime.setOnTimeTextClickListener(new TimeTextView.OnTimeTextClickListener() {
            @Override
            public void onClick(int hourOfDay, int minute) {
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });

        tvCommentState = (TextView) findViewById(R.id.comments_state) ;
        tvSubAssignsState = (TextView) findViewById(R.id.sub_num_state) ;

        editName = (EditText) findViewById(R.id.assign_name);
        editContent = (EditText) findViewById(R.id.assign_content);

        ivSetColor = (ColorPickerView) findViewById(R.id.set_color);
        ivSetColor.setOnColorSelectedListener(this);

        subLayout = (LinearLayout) findViewById(R.id.list_sub);
    }

    private void initValues(AssignmentBO assignBO){
        TextView tvDelete = (TextView) findViewById(R.id.delete);
        tvDelete.setVisibility(View.VISIBLE);
        tvDelete.setOnClickListener(this);

        Assignment entity = assignBO.getAssignEntity();

        editName.setText(entity.getAsnTitle());
        editContent.setText(entity.getAsnContent());
        tvCommentState.setText(entity.getAsnComment());
        tvStartDate.setDate(entity.getAsnDate());
        tvStartTime.setTime(entity.getAsnTime());

        mColor = entity.getAsnColor();
        int colorRes = TpColor.getColorResource(mColor);
        int pColor = Color.parseColor(mColor);
        if (colorRes != -1){
            ivSetColor.setImageResource(colorRes);
        }
        TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
        actionBar.setActionBarColor(pColor);

        List<SubAssignment> list = assignBO.getSubEntities();
        for (SubAssignment subEntity : list){
            addSubView(subEntity);
        }

        setSubNumState();
    }

    private AssignmentBO getAssignBo(){
        Intent intent = getIntent();
        if (intent.hasExtra(Intents.EXTRA_ENTITY)){
            return (AssignmentBO) intent.getSerializableExtra(Intents.EXTRA_ENTITY);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_comments:
                EdtDialog dialog = new EdtDialog(this,
                        EdtDialog.DIALOG_TYPE_NORMAL,
                        tvCommentState.getText().toString(),
                        getString(R.string.edit_comments),
                        getString(R.string.edit_comments_hint));
                dialog.setPositiveButton(R.string.com_confirm, new EdtDialog.PositiveButtonClickListener() {
                    @Override
                    public void onClick(String content) {
                        if (TextUtils.isEmpty(content)){
                            makeToast(R.string.edit_empty);
                        } else {
                            tvCommentState.setText(content);
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.add_sub_assigns:
                final SubAssignEditor editor = new SubAssignEditor(this);
                editor.setOnConfirmListener(new SubAssignEditor.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isNew, SubAssignment entity) {
                        addSubView(entity);
                        String str = subList.size() + getString(R.string.assign_num_appendix);
                        tvSubAssignsState.setText(str);
                    }
                });
                editor.showDialog();
                break;
            case R.id.delete:
                onDelete();
                break;
        }
    }

    private void onBack() {
        if (editName.getText().toString().isEmpty()
                && editContent.getText().toString().isEmpty()
                && subList.size() == 0
                && tvCommentState.getText().toString().isEmpty()){
            this.finish();
        } else {
            Dialog dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.com_tip)
                    .setMessage(R.string.com_back_tip)
                    .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AssignEditActivity.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.com_cancel, null)
                    .create();
            dlg.show();
        }
    }

    private void onSave(){
        String asnName = editName.getText().toString();
        if (TextUtils.isEmpty(asnName)){
            makeToast(R.string.asn_save_tip);
        } else {
            Assignment assignEntity;
            AssignmentBO assignBO = getAssignBo();
            if (assignBO != null) {
                assignEntity = assignBO.getAssignEntity();
            } else {
                assignBO = new AssignmentBO();
                assignEntity = new Assignment();
                assignEntity.setAsnId(TpTime.getLongId());
                assignEntity.setAsnProg(0);
                assignEntity.setAddedDate(TpTime.millisOfCurrentDate());
                assignEntity.setAddedTime(TpTime.millisOfCurrentTime());
            }

            long asnId = assignEntity.getAsnId();
            String account = UserKeeper.getUser(this).getAccount();
            long asnDate = tvStartDate.getMillisOfDate();
            int asnTime = tvStartTime.getMillisOfTime();

            assignEntity.setAsnTitle(asnName);
            assignEntity.setAccount(account);
            assignEntity.setAsnContent(editContent.getText().toString());
            assignEntity.setAsnDate(asnDate);
            assignEntity.setAsnTime(asnTime);
            assignEntity.setAsnComment(tvCommentState.getText().toString());
            assignEntity.setAsnColor(mColor);
            assignEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
            assignEntity.setLastModifyTime(TpTime.millisOfCurrentTime());
            assignEntity.setSynced(0);

//            AlarmEntity alarmEntity = AlarmLoader.wrapAssignEntity(entity);
//            if (alarmEntity != null){
//                TpAlarmManager.setAlarm(this, alarmEntity);
//            }

            List<SubAssignment> subEntities = new LinkedList<>();
            for (SubAssignView view : subList){
                SubAssignment subEntity = view.getSubEntity();
                subEntity.setAsnId(asnId);
                subEntity.setSubId(TpTime.getLongId());
                subEntity.setAccount(account);
                subEntity.setSynced(0);
                subEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
                subEntity.setLastModifyTime(TpTime.millisOfCurrentTime());
                subEntities.add(subEntity);

//                AlarmEntity alarmEntity1 = AlarmLoader.wrapSubAssignEntity(
//                        TPSubAssign, entity);
//                if (alarmEntity1 != null) {
//                    TpAlarmManager.setAlarm(this, alarmEntity);
//                }
            }
            assignBO.setAssignEntity(assignEntity);
            assignBO.setSubEntities(subEntities);

            AssignBoDAO assignBoDAO = AssignBoDAO.getInstance(this);
            if (getIntent().getSerializableExtra(Intents.EXTRA_ENTITY) != null){
                assignBoDAO.update(assignBO);
                makeToast(R.string.com_toast_update_success);
            } else {
                assignBoDAO.insert(assignBO);
                makeToast(R.string.com_toast_save_success);
            }
            assignBoDAO.close();

            AssignViewActivity.view(this, assignBO);
        }
    }

    private void onDelete(){
        AssignmentBO assignBO = getAssignBo();
        if (assignBO != null) {
            AssignBoDAO assignBoDAO = AssignBoDAO.getInstance(this);
            assignBoDAO.trash(assignBO);
            assignBoDAO.close();
            makeToast(R.string.com_toast_delete_success2);
        }
        ActivityManager.finishAllAssigns();
    }

    private void addSubView(SubAssignment entity){
        final SubAssignView subView = new SubAssignView(this);
        subView.setSubEntity(entity);
        subView.setColor(mColor);
        subView.setEditListener(new SubAssignView.OnEditListener() {
            @Override
            public void onEditClick() {
                final SubAssignEditor editor = new SubAssignEditor(AssignEditActivity.this);
                editor.setEntity(subView.getSubEntity());
                editor.showDialog();
                editor.setOnConfirmListener(new SubAssignEditor.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean isNew, SubAssignment entity) {
                        subView.setSubEntity(entity);
                    }
                });
            }
        });
        subView.setDeleteListener(new SubAssignView.OnDeleteListener() {
            @Override
            public void onDeleteClick() {
                Dialog dlg = new AlertDialog.Builder(AssignEditActivity.this)
                        .setTitle(R.string.asn_sub_del_title)
                        .setMessage(R.string.asn_sub_del_content)
                        .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 移除组件
                                subLayout.removeView(subView);
                                subList.remove(subView);
                                // 设置数量状态信息
                                setSubNumState();
                            }
                        })
                        .setNegativeButton(R.string.com_cancel, null)
                        .create();
                dlg.show();
            }
        });
        subList.add(subView);
        subLayout.addView(subView);
    }

    private void setSubNumState(){
        String str = subList.size() + getString(R.string.assign_num_appendix);
        tvSubAssignsState.setText(str);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @Override
    public void onColorSelected(int resColor, String strColor) {
        if (!TextUtils.isEmpty(strColor)){
            if (resColor != -1){
                ivSetColor.setImageResource(resColor);
            }
            mColor = strColor;
            int pColor = Color.parseColor(mColor);
            TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
            actionBar.setActionBarColor(pColor);
            for (SubAssignView view : subList){
                view.setColor(pColor);
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        tvStartDate.setDate(TpTime.getMillisFromDate(year, month, day));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        tvStartTime.setTime(TpTime.getMillisFromTime(hourOfDay, minute));
    }
}
