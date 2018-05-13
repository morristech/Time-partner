package me.shouheng.timepartner.activities.note;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import me.shouheng.timepartner.adapters.NoteClnAdapter;
import me.shouheng.timepartner.database.dao.base.CollectionDAO;
import me.shouheng.timepartner.managers.UserKeeper;
import me.shouheng.timepartner.models.business.intent.Intents;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.activities.base.BaseActivity;
import me.shouheng.timepartner.activities.base.SearchActivity;
import me.shouheng.timepartner.utils.TpColor;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.custom.ColorPickerView;

public class NoteCollection extends BaseActivity implements View.OnClickListener,
        NoteClnAdapter.OnItemClickListener, NoteClnAdapter.OnFooterClickListener,
        ColorPickerView.OnColorSelectedListener{
    private ColorPickerView setColor;
    private CollectionDAO collectionDAO;
    private NoteClnAdapter adapter;
    private RecyclerView recyclerView;
    private static final String _TYPE_FOR_VALUE = "_FOR_VALUE";
    private static final String _TYPE_FOR_VIEW = "_FOR_VIEW";

    public static void startForValue(Activity activity, int requestCode){
        Intent intent = new Intent(activity, NoteCollection.class);
        intent.putExtra(Intents.EXTRA_TYPE, _TYPE_FOR_VALUE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Context mContext){
        Intent intent = new Intent(mContext, NoteCollection.class);
        intent.putExtra(Intents.EXTRA_TYPE, _TYPE_FOR_VIEW);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_cln_layout);
        collectionDAO = CollectionDAO.getInstance(this);
        initViews();
    }

    private void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (getIntent().getStringExtra(Intents.EXTRA_TYPE).equals(_TYPE_FOR_VALUE)){
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.list_cln);

        updateLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                NoteEdit.edit(this, 0L);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (collectionDAO != null){
            collectionDAO.close();
        }
        super.onDestroy();
    }

    @Override
    public void onItemClick(View view) {
        Intent intent = new Intent();
        CollectionEntity clnEntity = (CollectionEntity) view.getTag();

        switch (getIntent().getStringExtra(Intents.EXTRA_TYPE)){
            case _TYPE_FOR_VALUE:
                intent.putExtra(Intents.EXTRA_ENTITY, clnEntity);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                NoteList.start(this, clnEntity);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateLayout();
    }

    @Override
    public void onFooterClick(View view) {
        final CollectionEntity collectionEntity = (CollectionEntity) view.getTag();
        if (collectionEntity != null) {
            final int totalNotes = collectionEntity.getCount();
            PopupMenu popupM = new PopupMenu(NoteCollection.this, view);
            popupM.inflate(R.menu.note_cln);
            popupM.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.edit:
                            // 显示重新编辑集合信息对话框
                            showEditClnDlg(collectionEntity);
                            break;
                        case R.id.delete:
                            if (totalNotes == 0){
                                // 选择的集合的记录为0
                                adapter.remove(collectionEntity);
                                collectionDAO.delete(collectionEntity);
                                makeToast(R.string.com_toast_delete_success);
                            } else {
                                makeToast(R.string.cln_delete1);
                            }
                            break;
                    }
                    return true;
                }
            });
            popupM.show();
        }
    }

    private void showEditClnDlg(final CollectionEntity clnEntity){
        View editView = getLayoutInflater().inflate(R.layout.note_cln_cln_edit_layout, null);
        final EditText editTitle = (EditText) editView.findViewById(R.id.edit_content);
        editTitle.setSingleLine(true);
        setColor = (ColorPickerView) editView.findViewById(R.id.set_color);
        setColor.setOnColorSelectedListener(this);

        if (clnEntity != null){
            String clnTitle = clnEntity.getClnTitle();
            String clnColor = clnEntity.getClnColor();
            if (!TextUtils.isEmpty(clnTitle)){
                editTitle.setText(clnTitle);
            }
            int colorRes = TpColor.getColorResource(clnColor);
            if (colorRes != 1){
                setColor.setImageResource(colorRes);
                setColor.setTag(clnColor);
            }
        }

        final Dialog editDlg = new AlertDialog.Builder(this)
                .setTitle(R.string.cln_dlg_title)
                .setView(editView)
                .setNegativeButton(R.string.com_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setPositiveButton(R.string.com_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strTitle = editTitle.getText().toString();
                        if (!TextUtils.isEmpty(strTitle)){
                            String strColor = setColor.getTag().toString();
                            if (clnEntity == null){
                                CollectionEntity entity = new CollectionEntity();
                                entity.setClnTitle(strTitle);
                                entity.setClnColor(strColor);
                                entity.setClnId(TpTime.getLongId());
                                entity.setAccount(UserKeeper.getUser(NoteCollection.this).getAccount());
                                entity.setAddedDate(TpTime.millisOfCurrentDate());
                                entity.setAddedTime(TpTime.millisOfCurrentTime());
                                entity.setLastModifyDate(TpTime.millisOfCurrentDate());
                                entity.setLastModifyTime(TpTime.millisOfCurrentTime());
                                entity.setSynced(0);

                                collectionDAO.insert(entity);
                                adapter.add(entity);
                                makeToast(R.string.com_toast_save_success);
                            } else {
                                adapter.update(clnEntity.getClnId(), strTitle, strColor);
                                clnEntity.setClnTitle(strTitle);
                                clnEntity.setClnColor(strColor);
                                clnEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
                                clnEntity.setLastModifyTime(TpTime.millisOfCurrentTime());

                                collectionDAO.update(clnEntity);
                                makeToast(R.string.com_toast_update_success);
                            }
                        } else {
                            makeToast(R.string.cln_edit_tip);
                        }
                    }
                })
                .create();
        editDlg.show();
    }

    @Override
    public void onColorSelected(int resColor, String strColor) {
        if (!TextUtils.isEmpty(strColor)){
            setColor.setTag(strColor);
            if (resColor != -1){
                setColor.setImageResource(resColor);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_cln_dashboard, menu);
        inflater.inflate(R.menu.menu_show_as, menu);
        inflater.inflate(R.menu.menu_sole_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_cln:
                showEditClnDlg(null);
                break;
            case R.id.menu_show_as_list:
                TpPrefer.getInstance(this).setClnGrid(false);
                updateLayout();
                return true;
            case R.id.menu_show_as_grid:
                TpPrefer.getInstance(this).setClnGrid(true);
                updateLayout();
                return true;
            case R.id.search:
                SearchActivity.startForNote(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLayout() {
        if (TpPrefer.getInstance(this).isClnGrid()){
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                recyclerView.setLayoutManager(new GridLayoutManager(
                        this, 3, LinearLayoutManager.VERTICAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(
                        this, 2, LinearLayoutManager.VERTICAL, false));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                recyclerView.setLayoutManager(new GridLayoutManager(
                        this, 2, LinearLayoutManager.VERTICAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(
                        this, 1, LinearLayoutManager.VERTICAL, false));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }
        // 重新设置Adapter
        adapter = new NoteClnAdapter();
        adapter.setClnEntityList(collectionDAO.getAll());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnItemClickListener(this);
        adapter.setOnFooterClickListener(this);
    }
}
