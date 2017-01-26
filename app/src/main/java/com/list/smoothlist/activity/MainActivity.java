package com.list.smoothlist.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
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

import com.list.smoothlist.R;
import com.list.smoothlist.adapter.RecyclerAdapter;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.dialog.NewNoteDialog;
import com.list.smoothlist.model.ToDo;
import com.onurciner.toastox.ToastOX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
                if (dy > 0 && mFab.isShown())
                    mFab.hide();
                else
                    mFab.show();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
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
            case R.id.search_date:
                filterByDate();
                break;
            case R.id.search_importance:
                filterByImportance();
                break;
            case R.id.search_title:
                filterByTitle();
                break;
        }
        return true;
    }

    private void filterByDate() {
        Collections.sort(mFilterList, (n1, n2) -> {
            if (n1.getDate() == null || n2.getDate() == null)
                return 0;
            return n1.getDate().compareTo(n2.getDate());
        });
        mAdapter.notifyDataSetChanged();
    }

    private void filterByImportance() {
        Collections.sort(mFilterList, (n1, n2) -> {
            if (n1.getLevelNb() > n2.getLevelNb())
                return -1;
            else if (n1.getLevelNb() == n2.getLevelNb())
                return 0;
            else
                return 1;
        });
        mAdapter.notifyDataSetChanged();
    }

    private void filterByTitle() {
        Collections.sort(mFilterList, (n1, n2) -> n1.getTitle().compareTo(n2.getTitle()));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 42:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle b = data.getExtras();
                    ToDo todo = b.getParcelable("todo");
                    updateItem(todo);
                }
                break;
            case 21:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap image = getDataFromResult(data);
                    if (image != null)
                        mDialog.resultGallery(image);
                }
                break;
        }
    }

    private void updateItem(ToDo todo) {
        DBManager.getInstance(this).updateNote(todo);
        for (int i = 0; i < mFilterList.size(); i++) {
            if (mFilterList.get(i).getId() == todo.getId())
                mFilterList.set(i, todo);
        }
        for (int i = 0; i< mList.size(); i++) {
            if (mList.get(i).getId() == todo.getId())
                mList.set(i, todo);
        }
        mAdapter.notifyDataSetChanged();
    }

    private Bitmap getDataFromResult(Intent data) {
        if (data != null) {
            try {
                return MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
