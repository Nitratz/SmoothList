package com.list.smoothlist.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.list.smoothlist.R;
import com.list.smoothlist.model.ToDo;

public class ViewEditActivity extends AppCompatActivity {

    private ImageView mHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_edit_activity);
        Bundle b = getIntent().getExtras();
        ToDo todo = b.getParcelable("todo");

        mHeader = (ImageView) findViewById(R.id.header);
        if (todo != null) {
            Bitmap image = todo.getBanner();
            if (image != null) {
                mHeader.setBackground(new BitmapDrawable(getResources(), image));
                mHeader.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "OK");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
