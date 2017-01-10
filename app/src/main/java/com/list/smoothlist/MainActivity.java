package com.list.smoothlist;

import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.list.smoothlist.adapter.RecyclerAdapter;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.dialog.NewNoteDialog;
import com.list.smoothlist.model.ToDo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ToDo> mList;
    private ArrayList<ToDo> mFilterList;

    private RecyclerView mRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout mCoord;
    private FloatingActionButton mFab;
    private NewNoteDialog mDialog;
    private Drawable[] mDrawables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawables = new Drawable[3];
        setListData();

        DBManager.getInstance(this).getWritableDatabase();
        mCoord = (CoordinatorLayout) findViewById(R.id.coord);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mFab = (FloatingActionButton) findViewById(R.id.floating_button);

        mRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerAdapter(this, mFilterList, mCoord);
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
        mDrawables[0] = ContextCompat.getDrawable(this, R.drawable.normal);
        mDrawables[1] = ContextCompat.getDrawable(this, R.drawable.important);
        mDrawables[2] = ContextCompat.getDrawable(this, R.drawable.vimportant);
        mList = DBManager.getInstance(this).getAllNotes();
        for (ToDo item : mList) {
            item.setLevel(mDrawables[item.getLevelNb()]);
        }
        mFilterList = new ArrayList<>(mList);
    }

    private void runDialog() {
        if (mDialog == null)
            mDialog = new NewNoteDialog(this, mFilterList, mDrawables);
        mDialog.setOnDismissListener(dialog -> {
            mAdapter.notifyDataSetChanged();
        });
        mDialog.start();
    }
}
