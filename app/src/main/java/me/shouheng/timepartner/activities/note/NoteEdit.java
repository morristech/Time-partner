package me.shouheng.timepartner.activities.note;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.LocationDAO;
import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.base.PictureDAO;
import me.shouheng.timepartner.database.dao.bo.NoteBoDAO;
import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.location.Location;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.Constants;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.models.entities.picture.Pictures;
import me.shouheng.timepartner.utils.*;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.managers.AudioManager;
import me.shouheng.timepartner.managers.ImageManager;
import me.shouheng.timepartner.widget.dialog.DateTimePickerDialog;
import me.shouheng.timepartner.widget.unit.NoteAlarmUnit;
import me.shouheng.timepartner.widget.unit.NoteAudioUnit;
import me.shouheng.timepartner.widget.unit.NoteLocateUnit;
import me.shouheng.timepartner.widget.unit.NotePictureUnit;
import me.shouheng.timepartner.widget.custom.RecordView;

public class NoteEdit extends BaseActivity implements View.OnClickListener{
    private String mColor = TpColor.COLOR_NOTE; // 默认颜色
    private RelativeLayout actionBar;
    private ImageManager imManager;
    private Uri imageUri;
    private String imageName;
    private final int REQUEST_CODE_COLLECTION = 111;

    private RecordView recordView;
    private TextView tvAddToCln;
    private LinearLayout listMedia;
    private EditText editTitle;
    private EditText editContent;

    private NotePictureUnit pictureUnit;
    private NoteAudioUnit audioUnit;
    private NoteAlarmUnit alarmUnit;
    private NoteLocateUnit locateUnit;

    /**
     * start edit
     * @param context context
     * @param noteId 0 if add a new note, noteId if edit an existed one */
    public static void edit(Context context, long noteId){
        Intent intent = new Intent(context, NoteEdit.class);
        intent.putExtra(Intents.EXTRA_ID, noteId);
        context.startActivity(intent);
    }

    private long getNoteId(){
        Intent intent = getIntent();
        return intent.getLongExtra(Intents.EXTRA_ID, 0L);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit_layout);

        ActivityManager.addNoteActivity(this);

        imManager = new ImageManager(this);

        initViews();

        initValues();
    }

    private void initViews() {
        tvAddToCln = (TextView) findViewById(R.id.add_to_cln);
        tvAddToCln.setTag(null);
        actionBar = (RelativeLayout) findViewById(R.id.action_bar);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.discard).setOnClickListener(this);

        editTitle = (EditText) findViewById(R.id.edit_title);
        editContent = (EditText) findViewById(R.id.edit_content);

        listMedia = (LinearLayout) findViewById(R.id.list_media);

        recordView = (RecordView) findViewById(R.id.record_view);
    }

    private void initValues(){
        long noteId = getNoteId();
        if (noteId == 0L)  return;

        NoteBoDAO noteBoDAO = NoteBoDAO.getInstance(this);
        NoteBO noteBO = noteBoDAO.get(noteId);
        Note note = noteBO.getNote();
        CollectionEntity clnEntity = noteBO.getCollection();
        List<Picture> pictureList = noteBO.getPictures();
        Location location = noteBO.getLocation();

        TextView tvDelete = (TextView) findViewById(R.id.delete);
        tvDelete.setVisibility(View.VISIBLE);

        if (clnEntity != null){
            mColor = clnEntity.getClnColor();
            int pColor = Color.parseColor(mColor);
            String strForCln = "#" + clnEntity.getClnTitle();
            tvAddToCln.setText(strForCln);
            tvAddToCln.setTextColor(pColor);
            tvAddToCln.setTag(clnEntity);
            actionBar.setBackgroundColor(pColor);
            TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
        }

        editTitle.setText(note.getNoteTitle());
        editContent.setText(note.getNoteContent());

        if (pictureList.size() != 0){
            Pictures pictures = new Pictures();
            pictures.setPictures(pictureList);
            pictureUnit = new NotePictureUnit(this);
            pictureUnit.setPictures(pictures);
            listMedia.addView(pictureUnit);
        }

        if (location != null){
            locateUnit = new NoteLocateUnit(this);
            locateUnit.setLocation(location);
            listMedia.addView(locateUnit);
        }

        String rcdPath = note.getRecordPath();
        if (!TextUtils.isEmpty(rcdPath) && TpFile.isFileExist(rcdPath)){
            addAudio(rcdPath);
        }

        int noticeTime = note.getNoteTime();
        long noticeDate = note.getNoteDate();
        if (noticeDate != 0 && noticeTime != 0){
            addAlarm(noticeDate, noticeTime);
        }

        noteBoDAO.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discard:
                onBack();
                break;
            case R.id.save:
                onSave();
                break;
            case R.id.add_audio:// 选择音频源
                selectAudioSrc();
                break;
            case R.id.add_video:
                break;
            case R.id.add_alarm:// 添加 提醒 单元
                final Calendar calendar = Calendar.getInstance();
                final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        null,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), false);
                final DateTimePickerDialog dlg = new DateTimePickerDialog(NoteEdit.this);
                final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        null,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false, false);
                dlg.setOnConfirmListener(new DateTimePickerDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(String strDate, String strTime) {
                        long lDate = TpTime.getMillisFromDate(strDate);
                        int iTime = TpTime.getMillisFromTime(strTime);
                        addAlarm(lDate, iTime);
                    }
                });
                dlg.setOnDateClickListener(new DateTimePickerDialog.OnDateClickListener() {
                    @Override
                    public void onClick() {
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                long millisOfDate = TpTime.getMillisFromDate(year, month, day);
                                dlg.setDate(millisOfDate);
                            }
                        });
                        datePickerDialog.setVibrate(false);
                        datePickerDialog.setYearRange(1985, 2037);
                        datePickerDialog.setCloseOnSingleTapDay(false);
                        datePickerDialog.show(getSupportFragmentManager(), "DATEEDIT");
                    }
                });
                dlg.setOnTimeClickListener(new DateTimePickerDialog.OnTimeClickListener() {
                    @Override
                    public void onClick() {
                        timePickerDialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                int millis = TpTime.getMillisFromTime(hourOfDay, minute);
                                dlg.setTime(millis);
                            }
                        });
                        timePickerDialog.setVibrate(false);
                        timePickerDialog.setCloseOnSingleTapMinute(false);
                        timePickerDialog.show(getSupportFragmentManager(), "TIMEEDIT");
                    }
                });
                dlg.show();
                break;
            case R.id.add_location:// 定位
                addLocation();
                break;
            case R.id.add_to_cln:
                NoteCollection.startForValue(this, REQUEST_CODE_COLLECTION);
                break;
            case R.id.add_picture:// 添加图片
                selectImageSrc();
                break;
            case R.id.delete: // DELETE
                onDelete();
                break;
        }
    }

    private void onBack(){
        if (editContent.getText().toString().isEmpty()
                && editTitle.getText().toString().isEmpty()
                && listMedia.getChildCount() == 0){
            finish();
        } else {
            Dialog dlg = new AlertDialog.Builder(this)
                    .setTitle(R.string.com_tip)
                    .setMessage(R.string.com_back_tip)
                    .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NoteEdit.this.finish();
                        }
                    })
                    .setNegativeButton(R.string.com_cancel, null)
                    .create();
            dlg.show();
        }
    }

    private void onSave(){
        String strTitle = editTitle.getText().toString();
        if (TextUtils.isEmpty(strTitle)){
            TpDisp.showMsgDlg(this, R.string.com_tip, R.string.note_save_title_require);
            return;
        }

        Object obj = tvAddToCln.getTag();
        if (obj == null){
            TpDisp.showMsgDlg(this, R.string.com_tip, R.string.note_save_collection_require);
            return;
        }
        long clnId = ((CollectionEntity) obj).getClnId();

        NoteDAO noteDAO = NoteDAO.getInstance(this);
        Note noteEntity;
        if (getNoteId() == 0L){
            noteEntity = new Note();
            noteEntity.setNoteId(TpTime.getLongId());
            noteEntity.setAddedDate(TpTime.millisOfCurrentDate());
            noteEntity.setAddedTime(TpTime.millisOfCurrentTime());
        } else {
            long noteId = getNoteId();
            noteEntity = noteDAO.get(noteId);
        }

        int time = 0;
        long date = 0;
        if (alarmUnit != null && listMedia.indexOfChild(alarmUnit) != -1){
            date = alarmUnit.getAlarmDate();
            time = alarmUnit.getAlarmTime();
        }

        if (pictureUnit != null && listMedia.indexOfChild(pictureUnit) != -1){
            PictureDAO pictureDAO = PictureDAO.getInstance(this);
            List<Picture> pictureList = pictureUnit.getPictures().getPictures();
            for (Picture picture : pictureList){
                picture.setPictureId(TpTime.getLongId());
                picture.setNoteId(noteEntity.getNoteId());
            }
            // delete existed and insert new ones
            pictureDAO.update(pictureList, noteEntity.getNoteId());
         }

        String audioPath = "";
        if (audioUnit != null && listMedia.indexOfChild(audioUnit) != -1){
            audioPath = audioUnit.getAudioPath();
        }

        if (locateUnit != null && listMedia.indexOfChild(locateUnit) != -1){
            LocationDAO locationDAO = LocationDAO.getInstance(this);
            Location location = locateUnit.getLocation();
            if (location.getNoteId() == 0L) { // not exist
                location.setLocationId(TpTime.getLongId());
                location.setNoteId(noteEntity.getNoteId());
                locationDAO.insert(location);
            } else {
                locationDAO.update(location);
            }
        }

        // Keep the 'like' state still
        noteEntity.setAccount(UserKeeper.getUser(this).getAccount());
        noteEntity.setNoteColor(mColor);
        noteEntity.setNoteTitle(strTitle);
        noteEntity.setNoteContent(editContent.getText().toString());
        noteEntity.setClnId(clnId);
        noteEntity.setRecordPath(audioPath);
        noteEntity.setNoteDate(date);
        noteEntity.setNoteTime(time);
        noteEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
        noteEntity.setLastModifyTime(TpTime.millisOfCurrentTime());
        noteEntity.setSynced(0);

//        设置闹钟信息 无需判断 内部会自动判断
//        AlarmEntity alarmEntity = AlarmLoader.wrapNoteEntity(
//                entity, dbManager.getCollection(clnId));
//        TpAlarmManager.setAlarm(this, alarmEntity);

        if (getNoteId() == 0L){
            noteDAO.insert(noteEntity);
            makeToast(R.string.com_toast_save_success);
        } else {
            noteDAO.update(noteEntity);
            makeToast(R.string.com_toast_update_success);
        }
        noteDAO.close();

        NoteViewer.view(this, noteEntity.getNoteId());
    }

    private void onDelete(){
        if (getNoteId() != 0L){
            NoteDAO noteDAO = NoteDAO.getInstance(this);
            Note noteEntity = noteDAO.get(getNoteId());
            noteDAO.delete(noteEntity);
            noteDAO.close();
            makeToast(R.string.com_toast_delete_success2);
        }
        ActivityManager.finishAllNotes();
    }

    private void addLocation(){
        if (!TpNet.isNetworkAvailable(this)){
            makeToast(R.string.mw_failed_2_location_net);
            return;
        }
        makeToast(R.string.beginning_location);
        final TpLocation tpLocation = new TpLocation(getApplicationContext());
        tpLocation.setOnFinishListener(new TpLocation.OnFinishListener() {
            @Override
            public void onCompleted(BDLocation bdLocation) {
                if (bdLocation != null){
                    String normalDistrict = bdLocation.getDistrict();
                    String locationCity = bdLocation.getCity();
                    if (!TextUtils.isEmpty(normalDistrict) && !TextUtils.isEmpty(locationCity)){
                        Location location = new Location();
                        location.setLocationId(TpTime.getLongId());
                        location.setAccount(UserKeeper.getUser(NoteEdit.this).getAccount());

                        location.setAltitude(bdLocation.getAltitude());
                        location.setLatitude(bdLocation.getLatitude());
                        location.setCountry(bdLocation.getCountry());
                        location.setCountryCode(bdLocation.getCountryCode());
                        location.setProvince(bdLocation.getProvince());
                        location.setCity(bdLocation.getCity());
                        location.setCityCode(bdLocation.getCityCode());
                        location.setDistrict(bdLocation.getDistrict());
                        location.setStreet(bdLocation.getStreet());
                        location.setStreetNumber(bdLocation.getStreetNumber());

                        location.setAddedDate(TpTime.millisOfCurrentDate());
                        location.setAddedTime(TpTime.millisOfCurrentTime());

                        if (locateUnit == null){
                            locateUnit = new NoteLocateUnit(NoteEdit.this);
                            locateUnit.setOnDeleteListener(
                                    new NoteLocateUnit.onDeleteOptionsListener() {
                                        @Override
                                        public void onDelete() {
                                            listMedia.removeView(locateUnit);
                                        }
                                    });
                        }

                        if (listMedia.indexOfChild(locateUnit) == -1){
                            listMedia.addView(locateUnit);
                        }

                        locateUnit.setLocation(location);
                    }
                }
            }
        });
        tpLocation.start();
    }

    private void addAlarm(long lDate, int iTime){
        if (alarmUnit ==null){
            alarmUnit = new NoteAlarmUnit(this);
            alarmUnit.setAlarmDate(lDate);
            alarmUnit.setAlarmTime(iTime);
            alarmUnit.setOnOptionsSelectedListener(new NoteAlarmUnit.OnOptionsSelectedListener() {
                @Override
                public void onSelected(int type) {
                    switch (type){
                        case NoteAlarmUnit.OPTIONS_TYPE_REEDIT:
                            // 显示闹钟编辑对话框
                            final Calendar calendar = Calendar.getInstance();
                            final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                                    null,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH), false);
                            final DateTimePickerDialog dlg = new DateTimePickerDialog(NoteEdit.this);
                            final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                                    null,
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false, false);
                            dlg.setDate(alarmUnit.getStrDate());
                            dlg.setTime(alarmUnit.getStrTime());
                            dlg.setOnConfirmListener(new DateTimePickerDialog.OnConfirmListener() {
                                @Override
                                public void onConfirm(String strDate, String strTime) {
                                    alarmUnit.setAlarmDate(strDate);
                                    alarmUnit.setAlarmTime(strTime);
                                }
                            });
                            dlg.setOnDateClickListener(new DateTimePickerDialog.OnDateClickListener() {
                                @Override
                                public void onClick() {
                                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                            long millisOfDate = TpTime.getMillisFromDate(year, month, day);
                                            dlg.setDate(millisOfDate);
                                        }
                                    });
                                    datePickerDialog.setVibrate(false);
                                    datePickerDialog.setYearRange(1985, 2037);
                                    datePickerDialog.setCloseOnSingleTapDay(false);
                                    datePickerDialog.show(getSupportFragmentManager(), "DATEREEDIT");
                                }
                            });
                            dlg.setOnTimeClickListener(new DateTimePickerDialog.OnTimeClickListener() {
                                @Override
                                public void onClick() {
                                    timePickerDialog.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                                            int millis = TpTime.getMillisFromTime(hourOfDay, minute);
                                            dlg.setTime(millis);
                                        }
                                    });
                                    timePickerDialog.setVibrate(false);
                                    timePickerDialog.setCloseOnSingleTapMinute(false);
                                    timePickerDialog.show(getSupportFragmentManager(), "TIMEREEDIT");
                                }
                            });
                            dlg.show();
                            break;
                        case NoteAlarmUnit.OPTIONS_TYPE_DELETE:
                            listMedia.removeView(alarmUnit);
                            break;
                    }
                }
            });
        }
        if (listMedia.indexOfChild(alarmUnit) == -1){
            listMedia.addView(alarmUnit);
        }
        alarmUnit.setAlarmDate(lDate);
        alarmUnit.setAlarmTime(iTime);
    }

    private void addAudio(final String audioPath){
        if (audioUnit == null){
            audioUnit = new NoteAudioUnit(this);
            audioUnit.setOnDeleteListener(new NoteAudioUnit.OnDeleteListener() {
                @Override
                public void onDelete(View v) {
                    listMedia.removeView(audioUnit);
                }
            });
        }
        if (listMedia.indexOfChild(audioUnit) == -1){
            listMedia.addView(audioUnit);
        }
        audioUnit.setAudioPath(audioPath);
    }

    private void selectAudioSrc(){
        View vi = getLayoutInflater().inflate(R.layout.dialog_audio_src_selector, null);
        final Dialog dlgI = new AlertDialog.Builder(this)
                .setTitle(R.string.n_record)
                .setView(vi)
                .setPositiveButton(R.string.dlg_img_cancel, null)
                .create();
        dlgI.show();
        vi.findViewById(R.id.from_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordView.show();
                recordView.setRecordEndListener(new RecordView.OnRecordEndListener() {
                    @Override
                    public void onRecordEnd(String filePath, String fileName) {
                        addAudio(filePath);
                    }
                });
                if (dlgI.isShowing()){
                    dlgI.dismiss();
                }
            }
        });
        vi.findViewById(R.id.from_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
                intent2.setType("audio/*");
                startActivityForResult(intent2, Constants.REQUEST_CODE_CHOOSE_AUDIO);
                if (dlgI.isShowing()){
                    dlgI.dismiss();
                }
            }
        });
    }

    private void selectImageSrc(){
        View vi = getLayoutInflater().inflate(R.layout.dialog_pic_src_selector, null);
        final Dialog dlgI = new AlertDialog.Builder(this)
                .setTitle(R.string.n_picture)
                .setView(vi)
                .setPositiveButton(R.string.dlg_img_cancel, null)
                .create();
        dlgI.show();
        vi.findViewById(R.id.from_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageName = TpFile.getFileName(".jpg");
                File iconFile = imManager.createImageFile(imageName);
                imageUri = Uri.fromFile(iconFile);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PHOTO);
                if (dlgI.isShowing()){
                    dlgI.dismiss();
                }
            }
        });
        vi.findViewById(R.id.from_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
                intent2.setType("image/*");
                startActivityForResult(intent2, Constants.REQUEST_CODE_CHOOSE_PHOTO);
                if (dlgI.isShowing()){
                    dlgI.dismiss();
                }
            }
        });
    }

    private void addPicture(String picPath){
        if (pictureUnit == null){
            pictureUnit = new NotePictureUnit(NoteEdit.this);
            listMedia.addView(pictureUnit);
        }
        Picture picture = new Picture();
        picture.setPicturePath(picPath);
        picture.setPictureId(TpTime.getLongId());
        picture.setAccount(UserKeeper.getUser(this).getAccount());
        picture.setAddedDate(TpTime.millisOfCurrentDate());
        picture.setAddedTime(TpTime.millisOfCurrentTime());
        pictureUnit.addPicture(picture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri,"image/*");
                    intent.putExtra("scale",true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent, Constants.REQUEST_CODE_CROP_PHOTO);
                }
                break;
            case Constants.REQUEST_CODE_CROP_PHOTO:
                if(resultCode == RESULT_OK){
                    addPicture(imManager.getImageByName(imageName).getPath());
                }
                break;
            case Constants.REQUEST_CODE_CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    String imagePath;
                    if(Build.VERSION.SDK_INT >= 19){
                        imagePath = imManager.handleImageOnKitKat(data);
                    }else{
                        imagePath = imManager.handleImageBeforeKitKat(data);
                    }
                    if (!TextUtils.isEmpty(imagePath)){
                        addPicture(imagePath);
                    }
                }
                break;
            case REQUEST_CODE_COLLECTION:
                if (resultCode == RESULT_OK){
                    Serializable serializable = data.getSerializableExtra(Intents.EXTRA_ENTITY);
                    if (serializable != null && serializable instanceof CollectionEntity){
                        CollectionEntity collectionEntity = (CollectionEntity) serializable;

                        mColor = collectionEntity.getClnColor();
                        int pColor = Color.parseColor(mColor);
                        String clnName = "#" + collectionEntity.getClnTitle();
                        tvAddToCln.setText(clnName);
                        tvAddToCln.setTextColor(pColor);
                        tvAddToCln.setTag(collectionEntity);
                        actionBar.setBackgroundColor(pColor);

                        TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
                    } else {
                        break;
                    }
                }
                break;
            case Constants.REQUEST_CODE_CHOOSE_AUDIO:
                if(resultCode == RESULT_OK){
                    AudioManager manager = new AudioManager(NoteEdit.this);
                    String audioPath;
                    if(Build.VERSION.SDK_INT >= 19){
                        audioPath = manager.handleAudioOnKitKat(data);
                    }else{
                        audioPath = manager.handleAudioBeforeKitKat(data);
                    }
                    if (!TextUtils.isEmpty(audioPath)){
                        addAudio(audioPath);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (recordView.isShowing()){
            recordView.hide();
        } else {
            onBack();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != audioUnit){
            audioUnit.release();
        }
    }
}
