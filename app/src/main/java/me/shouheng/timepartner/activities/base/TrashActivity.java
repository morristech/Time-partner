package me.shouheng.timepartner.activities.base;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.shouheng.timepartner.adapters.TrashAdapter;
import me.shouheng.timepartner.database.dao.loader.TrashLoader;
import me.shouheng.timepartner.models.Model;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.widget.fastscroller.FastScroller;

public class TrashActivity extends AppCompatActivity implements TrashAdapter.OnItemSelectedListener{

    private TrashAdapter adapter;

    private TrashLoader trashDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        trashDAO = TrashLoader.getInstance(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrashAdapter();
        adapter.setTrashEntities(trashDAO.get());
        adapter.setOnItemSelectedListener(this);
        recyclerView.setAdapter(adapter);

        FastScroller fastScroller = (FastScroller) findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
    protected void onDestroy() {
        if (trashDAO != null) {
            trashDAO.close();
        }
        super.onDestroy();
    }

    @Override
    public void onItemSelected(View view, final Model baseModel) {
        PopupMenu popupM = new PopupMenu(this, view);
        popupM.inflate(R.menu.trash_pop_menu);
        popupM.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.recover:
                        trashDAO.recover(baseModel);
                        break;
                    case R.id.finally_delete:
                        trashDAO.del(baseModel);
                        break;
                }
                adapter.setTrashEntities(trashDAO.get());
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        popupM.show();
    }
}
