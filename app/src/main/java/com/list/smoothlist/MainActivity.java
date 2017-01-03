package com.list.smoothlist;

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
import com.list.smoothlist.dialog.NewNoteDialog;
import com.list.smoothlist.model.ToDo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ToDo> mList;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        mList = new ArrayList<>();

        mCoord = (CoordinatorLayout) findViewById(R.id.coord);
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mFab = (FloatingActionButton) findViewById(R.id.floating_button);

        mRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerAdapter(this, mList, mCoord);
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

    private void generateData(ArrayList<ToDo> list) {
        for (int i = 0; i < 100; i++) {
            ToDo todo = new ToDo();
            todo.setLevel(ContextCompat.getDrawable(this, R.mipmap.light_bulb));
            todo.setDesc("Bonjour ca marche correctement du coup j'écris beaucoup lol xD mdrrrr\nBonjour ca marche correctement du coup j'écris beaucoup lol xD mdrrrr\nBonjour ca marche correctement du coup j'écris beaucoup lol xD mdrrrr\nBonjour ca marche correctement du coup j'écris beaucoup lol xD mdrrrr\nBonjour ca marche correctement du coup j'écris beaucoup lol xD mdrrrr\nBonjour ca marche correctement du coup j'écris beaucoup lol xD mdrrrr\n");
            todo.setTitle("Test de la cardView");
            list.add(todo);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_button:
                runDialog();
                break;
        }
    }

    private void runDialog() {
        if (mDialog == null)
            mDialog = new NewNoteDialog(this, mList);
        mDialog.setOnDismissListener(dialog -> {
            mAdapter.notifyDataSetChanged();
        });
        mDialog.start();
    }
}
