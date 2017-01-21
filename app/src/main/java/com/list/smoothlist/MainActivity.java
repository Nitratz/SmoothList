package com.list.smoothlist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.list.smoothlist.adapter.RecyclerAdapter;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.dialog.NewNoteDialog;
import com.list.smoothlist.model.ToDo;
import com.onurciner.toastox.ToastOX;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ToDo> mList;
    private ArrayList<ToDo> mFilterList;

    private Toolbar mToolbar;
    private RecyclerView mRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout mCoord;
    private FloatingActionButton mFab;
    private NewNoteDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        setListData();

        DBManager.getInstance(this).getWritableDatabase();
        mCoord = (CoordinatorLayout) findViewById(R.id.coord);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mFab = (FloatingActionButton) findViewById(R.id.floating_button);

        mRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerAdapter(this, mFilterList, mList, mCoord);
        mRecycler.setAdapter(mAdapter);

        mFab.setOnClickListener(this);
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 || dy < 0 && mFab.isShown())
                    mFab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    mFab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_button:
                runDialog();
                break;
        }
    }

    private void filterRecyclerView(String filter){
        filter = filter.toLowerCase();
        mFilterList.clear();
        if (filter.length() == 0) {
            mFilterList.addAll(mList);
        } else {
            for (ToDo note : mList) {
                if (note.getTitle().toLowerCase().contains(filter) ||
                        note.getDesc().toLowerCase().contains(filter)) {
                    mFilterList.add(note);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void setListData() {
        mList = DBManager.getInstance(this).getAllNotes();
        mFilterList = new ArrayList<>(mList);
    }

    private void runDialog() {
        if (mDialog == null)
            mDialog = new NewNoteDialog(this, mFilterList, mList);
        mDialog.setOnDismissListener(dialog -> {
            /*new AlertDialog.Builder(this).setTitle("Question")
                    .setMessage("Do you want to create another task?")
                    .setPositiveButton(android.R.string.yes, (dialogy, which) -> {
                        mDialog.start();
                        dialogy.cancel();
                    })
                    .setNegativeButton(android.R.string.no, (dialogn, which) -> {
                        dialog.cancel();
                    }).show();*/
            mAdapter.notifyDataSetChanged();
        });
        mDialog.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //filterRecyclerView(query);
                if(!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                filterRecyclerView(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                filterRecyclerView("");
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42) {
            if (resultCode == Activity.RESULT_OK) {
                ToastOX.ok(this, "Swaaaaaaaaaag");
            }
        }
    }
}
