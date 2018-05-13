package me.shouheng.timepartner.activities.note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.bo.NoteBoDAO;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.adapters.NoteListAdapter;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.utils.TpDisp;

public class NoteList extends BaseActivity{

    public static void start(Context mContext, CollectionEntity collectionEntity){
        Intent intent = new Intent(mContext, NoteList.class);
        if (collectionEntity != null){
            intent.putExtra(Intents.EXTRA_ENTITY, collectionEntity);
        }
        mContext.startActivity(intent);
    }

    private CollectionEntity getCollectionEntity() {
        Intent intent = getIntent();
        if (intent.hasExtra(Intents.EXTRA_ENTITY)){
            Serializable serializable = intent.getSerializableExtra(Intents.EXTRA_ENTITY);
            if (serializable != null && serializable instanceof CollectionEntity) {
                return  (CollectionEntity) serializable;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list_layout);

        CollectionEntity collectionEntity = getCollectionEntity();
        if (collectionEntity != null) {
            initViews(collectionEntity);
            updateLayout(collectionEntity);
        }
    }

    private void initViews(CollectionEntity collectionEntity){
        int pColor = getResources().getColor(R.color.note_prime);
        String clnName = collectionEntity.getClnTitle();;
        String mColor = collectionEntity.getClnColor();
        if (!TextUtils.isEmpty(mColor)){
            pColor = Color.parseColor(mColor);
            TpDisp.setStatusBarColor(this, pColor, TpDisp.DEFAULT_COLOR_ALPHA);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(pColor);
        toolbar.setTitle(clnName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void updateLayout(CollectionEntity collectionEntity){
        NoteDAO noteDAO = NoteDAO.getInstance(this);
        NoteBoDAO noteBoDAO = NoteBoDAO.getInstance(this);

        long clnId = collectionEntity.getClnId();
        List<Note> notes = noteDAO.gets(clnId);
        List<NoteBO> noteBOs = new ArrayList<>();
        for (Note note : notes){
            long noteId = note.getNoteId();
            NoteBO noteBO = noteBoDAO.get(noteId);
            noteBOs.add(noteBO);
        }
        noteBoDAO.close();
        noteDAO.close();
        NoteListAdapter adapter = new NoteListAdapter(this);
        adapter.setNoteBOs(noteBOs);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        adapter.setOnListItemSelectedListener(
                new NoteListAdapter.OnListItemSelectedListener() {
                    @Override
                    public void onSelected(View v, Note noteEntity) {
                        NoteViewer.view(NoteList.this, noteEntity.getNoteId());
                    }
                });

        if (adapter.getItemCount() == 0){
            TextView emptyTip = (TextView) findViewById(R.id.empty_tip);
            emptyTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CollectionEntity collectionEntity = getCollectionEntity();
        if (collectionEntity != null) {
            updateLayout(collectionEntity);
        }
    }
}
