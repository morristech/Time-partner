package me.shouheng.timepartner.activities.note;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.bo.NoteBoDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.location.Location;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.managers.ActivityManager;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.models.entities.picture.Pictures;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.unit.MultiPicturesView;
import me.shouheng.timepartner.widget.unit.NoteAudioUnit;
import me.shouheng.timepartner.widget.unit.NoteContentView;
import me.shouheng.timepartner.widget.unit.NoteShareFooter;

public class NoteViewer extends BaseActivity implements View.OnClickListener{
    private NoteAudioUnit audioUnit;
    private NoteShareFooter shareFooter;

    public static void view(Context context, long id){
        Intent intent = new Intent(context, NoteViewer.class);
        intent.putExtra(Intents.EXTRA_ID, id);
        context.startActivity(intent);
    }

    private NoteBO getNoteBO(){
        NoteBoDAO noteBoDAO = NoteBoDAO.getInstance(this);
        long noteId = getIntent().getLongExtra(Intents.EXTRA_ID, 0L);
        NoteBO noteBO = noteBoDAO.get(noteId);
        noteBoDAO.close();
        return noteBO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_viewer_layout);

        ActivityManager.addNoteActivity(this);

        NoteBO noteBO = getNoteBO();
        initViews(noteBO);
    }

    private void initViews(final NoteBO noteBO){
        if (noteBO == null) return;

        final Note noteEntity = noteBO.getNote();
        List<Picture> pictureList = noteBO.getPictures();
        Location location = noteBO.getLocation();
        CollectionEntity clnEntity = noteBO.getCollection();

        String strNoteName = noteEntity.getNoteTitle();
        String strContent = noteEntity.getNoteContent();
        String strAudiosPath = noteEntity.getRecordPath();
        int noticeTime = noteEntity.getNoteTime();
        long noticeDate = noteEntity.getNoteDate();
        int addedTime = noteEntity.getAddedTime();
        long addedDate = noteEntity.getAddedDate();
        String strNoticeTime = TpTime.getExactTime(
                noticeDate + noticeTime, TpTime.EXACT_TIME_TYPE_4);
        String strAddedTime = TpTime.getExactTime(
                addedDate + addedTime, TpTime.EXACT_TIME_TYPE_4);

        String mColor = noteEntity.getNoteColor();
        int pColor = Color.parseColor(mColor);
        TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(strNoteName);
        toolbar.setSubtitle(strAddedTime);
        toolbar.setBackgroundColor(pColor);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LinearLayout list = (LinearLayout) findViewById(R.id.list);

        if (pictureList.size() > 0){
            Pictures pictures = new Pictures();
            pictures.setPictures(pictureList);

            MultiPicturesView picView = new MultiPicturesView(this);
            boolean hasContent = picView.setPictures(pictures);
            if (hasContent){
                list.addView(picView);
            }
        }

        if (!TextUtils.isEmpty(strAudiosPath)){
            audioUnit = new NoteAudioUnit(this);
            audioUnit.setAudioPath(strAudiosPath);
            audioUnit.setHeaderEnable(false);
            list.addView(audioUnit);
        }

        NoteContentView noteContentView = new NoteContentView(this);
        noteContentView.setTitle(strNoteName);
        noteContentView.setContent(strContent);
        if (location != null){
            noteContentView.setLocation(location.getCountry()
                    + " " + location.getProvince()
                    + " " + location.getCity()
                    + " " + location.getDistrict());
        }
        list.addView(noteContentView);

        shareFooter = new NoteShareFooter(this);
        shareFooter.setTime(strAddedTime);
        shareFooter.setLiked(noteEntity.getLiked());
        shareFooter.setOnShareClickListener(new NoteShareFooter.OnShareClickListener() {
            @Override
            public void onClick(int type) {
                if (type == NoteShareFooter.OnShareClickListener._LIKE){
                    noteEntity.setLiked(shareFooter.getLiked());
                    NoteDAO noteDAO = NoteDAO.getInstance(NoteViewer.this);
                    noteDAO.update(noteEntity);
                    noteDAO.close();
                }
            }
        });
        list.addView(shareFooter);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean   onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.com_viewer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                break;
            case android.R.id.home:
                onBack();
                break;
            case R.id.edit:
                if (audioUnit != null){
                    audioUnit.stop();
                }
                NoteBO noteBO = getNoteBO();
                if (noteBO != null) {
                    Note noteEntity = noteBO.getNote();
                    NoteEdit.edit(this, noteEntity.getNoteId());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        NoteBO noteBO = getNoteBO();
        if (noteBO != null) {
            Note note = noteBO.getNote();
            NoteDAO noteDAO = NoteDAO.getInstance(this);
            note.setLiked(shareFooter.getLiked());
            noteDAO.update(note);
            noteDAO.close();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (audioUnit != null){
            audioUnit.release();
        }
        super.onDestroy();
    }

    private void onBack(){
        ActivityManager.finishAllNotes();
    }

    private void showDlg(){
        Dialog dlg = new AlertDialog.Builder(this)
                .setTitle(R.string.asnv_dlg_title)
                .setMessage(R.string.asnv_dlg_msg)
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NoteBO noteBO = getNoteBO();
                        if (noteBO != null) {
                            Note noteEntity = noteBO.getNote();
                            NoteEdit.edit(NoteViewer.this, noteEntity.getNoteId());
                        }
                    }
                })
                .setNegativeButton(R.string.com_cancel, null)
                .create();
        dlg.show();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }
}
