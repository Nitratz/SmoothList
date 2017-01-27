package com.list.smoothlist.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.list.smoothlist.activity.MainActivity;
import com.list.smoothlist.R;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.model.ToDo;
import com.list.smoothlist.receiver.NotificationPublisher;
import com.onurciner.toastox.ToastOX;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewNoteDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private ArrayList<ToDo> mList;
    private ArrayList<ToDo> mFullList;
    private DatePickerDialog mDateDialog;
    private TimePickerDialog mTimeDialog;
    private Calendar mCalendar;
    private Calendar mSelectedCal;
    private SimpleDateFormat mFormat;
    private Bitmap mImage;

    private EditText mTitle;
    private ImageView mHeader;
    private EditText mDesc;
    private CheckBox mCheck;
    private ImageView mPreview;
    private Button mOk;
    private Button mCancel;
    private Spinner mSpinner;
    private EditText mDatePicker;
    private EditText mTimePicker;

    public NewNoteDialog(Context context, ArrayList<ToDo> list, ArrayList<ToDo> fullList) {
        super(context);
        mContext = context;
        mList = list;
        mFullList = fullList;
    }

    public void start() {
        setContentView(R.layout.dialog_new_note);
        setCancelable(false);
        mCalendar = Calendar.getInstance();
        mSelectedCal = Calendar.getInstance();
        mFormat = new SimpleDateFormat(mContext.getString(R.string.format_date));

        mHeader = (ImageView) findViewById(R.id.header);
        mOk = (Button) findViewById(R.id.ok);
        mDatePicker = (EditText) findViewById(R.id.date_picker);
        mTimePicker = (EditText) findViewById(R.id.time_picker);
        mCancel = (Button) findViewById(R.id.cancel);
        mTitle = (EditText) findViewById(R.id.title);
        mDesc = (EditText) findViewById(R.id.desc);
        mCheck = (CheckBox) findViewById(R.id.image_check);
        mPreview = (ImageView) findViewById(R.id.preview);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        mDatePicker.setOnClickListener(this);
        mTimePicker.setOnClickListener(this);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                openGallery();
            } else
                mPreview.setVisibility(View.INVISIBLE);
        });

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
                .setLevelNb(mSpinner.getSelectedItemPosition());
        if (!mDatePicker.getText().toString().equals(""))
            todo.setDate(mFormat.format(mSelectedCal.getTime()));
        if (mCheck.isChecked())
            todo.setBanner(mImage);
        mList.add(todo);
        mFullList.add(todo);
        DBManager.getInstance(mContext).insertNote(todo);
        if (!mDatePicker.getText().toString().equals(""))
            NotificationPublisher.scheduleNotification(mContext, mSelectedCal.getTime().getTime(), todo);
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

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((MainActivity) mContext).startActivityForResult(galleryIntent , 21);
    }

    public void resultGallery(Bitmap image) {
        image = Bitmap.createScaledBitmap(image, 600, 200, true);
        mPreview.setBackground(new BitmapDrawable(mContext.getResources(), image));
        mImage = image;
    }
}
