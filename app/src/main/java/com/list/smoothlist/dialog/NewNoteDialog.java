package com.list.smoothlist.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.list.smoothlist.R;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.model.ToDo;
import com.onurciner.toastox.ToastOX;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewNoteDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private ArrayList<ToDo> mList;
    private ArrayList<ToDo> mFullList;
    private Drawable[] mDrawables;
    private DatePickerDialog mDateDialog;
    private TimePickerDialog mTimeDialog;
    private Calendar mCalendar;
    private Calendar mSelectedCal;
    private SimpleDateFormat mFormat;

    private EditText mTitle;
    private ImageView mHeader;
    private EditText mDesc;
    private Button mOk;
    private Button mCancel;
    private Spinner mSpinner;
    private EditText mDatePicker;
    private EditText mTimePicker;

    public NewNoteDialog(Context context, ArrayList<ToDo> list, ArrayList<ToDo> fullList, Drawable[] drawables) {
        super(context);
        mDrawables = drawables;
        mContext = context;
        mList = list;
        mFullList = fullList;
    }

    public void start() {
        setContentView(R.layout.dialog_new_note);
        setCancelable(false);
        mCalendar = Calendar.getInstance();
        mSelectedCal = Calendar.getInstance();
        mFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        mHeader = (ImageView) findViewById(R.id.header);
        mOk = (Button) findViewById(R.id.ok);
        mDatePicker = (EditText) findViewById(R.id.date_picker);
        mTimePicker = (EditText) findViewById(R.id.time_picker);
        mCancel = (Button) findViewById(R.id.cancel);
        mTitle = (EditText) findViewById(R.id.title);
        mDesc = (EditText) findViewById(R.id.desc);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        mDatePicker.setOnClickListener(this);
        mTimePicker.setOnClickListener(this);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                addNewNote();
                break;
            case R.id.cancel:
                dismiss();
                break;
            case R.id.date_picker:
                openDatePicker();
                break;
            case R.id.time_picker:
                openTimePicker();
                break;
        }
    }

    private void addNewNote() {
        String title = mTitle.getText().toString();
        String desc = mDesc.getText().toString();
        ToDo todo;

        if (title.equals("") || desc.equals("")) {
            ToastOX.error(mContext, mContext.getString(R.string.error_fields));
            return;
        }
        todo = new ToDo();
        todo.setDesc(desc)
                .setTitle(title)
                .setLevelNb(mSpinner.getSelectedItemPosition())
                .setLevel(mDrawables[mSpinner.getSelectedItemPosition()]);
        if (!mDatePicker.getText().toString().equals(""))
            todo.setDate(mFormat.format(mSelectedCal.getTime()));
        mList.add(todo);
        mFullList.add(todo);
        DBManager.getInstance(mContext).insertNote(todo);
        dismiss();
    }

    private void openDatePicker() {
        if (mDateDialog == null) {
            mDateDialog = new DatePickerDialog(mContext, (view, year, month, dayOfMonth) -> {
                if (year < mCalendar.get(Calendar.YEAR) || month < mCalendar.get(Calendar.MONTH) ||
                        month == mCalendar.get(Calendar.MONTH) && dayOfMonth < mCalendar.get(Calendar.DAY_OF_MONTH)) {
                    ToastOX.error(mContext, mContext.getString(R.string.error_date));
                }
                else {
                    if (!Locale.getDefault().getLanguage().equals("fr"))
                        mDatePicker.setText(year + "/" + ((month + 1) < 9 ? "0" + (month + 1) : month) + "/" + dayOfMonth);
                    else
                        mDatePicker.setText(dayOfMonth + "/" + ((month + 1) < 9 ? "0" + (month + 1) : month) + "/" + year);
                    mSelectedCal.set(Calendar.YEAR, year);
                    mSelectedCal.set(Calendar.MONTH, month);
                    mSelectedCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mSelectedCal.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1);
                }
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        mDateDialog.show();
    }

    private void openTimePicker() {
        if (mTimeDialog == null) {
            mTimeDialog = new TimePickerDialog(mContext, (view, hourOfDay, minute) -> {
                mTimePicker.setText((hourOfDay < 9 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 9 ? "0" + minute : minute));
                mSelectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mSelectedCal.set(Calendar.MINUTE, minute);
            }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        }
        mTimeDialog.show();
    }
}
