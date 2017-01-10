package com.list.smoothlist.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.list.smoothlist.R;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.model.ToDo;
import com.onurciner.toastox.ToastOX;

import java.util.ArrayList;

public class NewNoteDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private ArrayList<ToDo> mList;
    private Drawable[] mDrawables;

    private EditText mTitle;
    private EditText mDesc;
    private Button mOk;
    private Button mCancel;
    private Spinner mSpinner;

    public NewNoteDialog(Context context, ArrayList<ToDo> list, Drawable[] drawables) {
        super(context);
        mDrawables = drawables;
        mContext = context;
        mList = list;
    }

    public void start() {
        setContentView(R.layout.dialog_new_note);
        setCancelable(false);

        mOk = (Button) findViewById(R.id.ok);
        mCancel = (Button) findViewById(R.id.cancel);
        mTitle = (EditText) findViewById(R.id.title);
        mDesc = (EditText) findViewById(R.id.desc);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                addNewNote();
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private void addNewNote() {
        String title = mTitle.getText().toString();
        String desc = mDesc.getText().toString();
        ToDo todo;

        if (title.equals("") || desc.equals("")) {
            ToastOX.error(mContext, "Veuillez remplir les champs ci-dessus");
        }
        todo = new ToDo();
        todo.setDesc(desc);
        todo.setTitle(title);
        todo.setLevelNb(mSpinner.getSelectedItemPosition());
        todo.setLevel(mDrawables[mSpinner.getSelectedItemPosition()]);

        mList.add(todo);
        DBManager.getInstance(mContext).insertNote(todo);
    }
}
