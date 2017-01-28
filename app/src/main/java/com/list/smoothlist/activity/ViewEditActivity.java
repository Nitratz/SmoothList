package com.list.smoothlist.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.list.smoothlist.R;
import com.list.smoothlist.model.ToDo;
import com.list.smoothlist.receiver.NotificationPublisher;
import com.onurciner.toastox.ToastOX;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ViewEditActivity extends AppCompatActivity implements View.OnClickListener {

    private DatePickerDialog mDateDialog;
    private TimePickerDialog mTimeDialog;
    private Calendar mCalendar;
    private Calendar mSelectedCal;
    private SimpleDateFormat mFormat;

    private ImageView mPreview;
    private EditText mDesc;
    private EditText mTitle;
    private Spinner mSpinner;
    private Switch mSwitch;
    private TextView mDone;
    private ImageView mImageDone;
    private EditText mDate;
    private EditText mTime;
    private Button mFinishEdit;

    private ToDo mTodo;
    private Bitmap mImage;
    private String mPreviousDate;
    private String mPreviousTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_edit_activity);
        Bundle b = getIntent().getExtras();
        mTodo = b.getParcelable("todo");
        mSelectedCal = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        mFormat = new SimpleDateFormat(getString(R.string.format_date));

        mPreview = (ImageView) findViewById(R.id.preview);
        mSwitch = (Switch) findViewById(R.id.switch_done);
        mDone = (TextView) findViewById(R.id.text_switch);
        mImageDone = (ImageView) findViewById(R.id.image_done);
        mSpinner = (Spinner) findViewById(R.id.spinner_level);
        mTitle = (EditText) findViewById(R.id.title);
        mDesc = (EditText) findViewById(R.id.desc);
        mDate = (EditText) findViewById(R.id.date_picker);
        mTime = (EditText) findViewById(R.id.time_picker);
        mFinishEdit = (Button) findViewById(R.id.edit_finished);

        mPreview.setOnClickListener(this);
        mFinishEdit.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mImageDone.setBackground(ContextCompat.getDrawable(this, R.drawable.checked_done));
                mDone.setText(getString(R.string.done));
            } else {
                mImageDone.setBackground(ContextCompat.getDrawable(this, R.drawable.todo));
                mDone.setText(getString(R.string.todo));
            }
        });

        if (mTodo != null)
            setDataFromToDo();
    }

    private void setDataFromToDo() {
        Bitmap image = mTodo.getBanner();
        if (image != null) {
            mPreview.setBackground(new BitmapDrawable(getResources(), image));
            mPreview.setVisibility(View.VISIBLE);
        }
        if (mTodo.isDone() == 1) {
            mSwitch.setChecked(true);
            mImageDone.setBackground(ContextCompat.getDrawable(this, R.drawable.checked_done));
            mDone.setText(getString(R.string.done));
        } else {
            mSwitch.setChecked(false);
            mImageDone.setBackground(ContextCompat.getDrawable(this, R.drawable.todo));
            mDone.setText(getString(R.string.todo));
        }
        mTitle.setText(mTodo.getTitle());
        mDesc.setText(mTodo.getDesc());
        mSpinner.setSelection(mTodo.getLevelNb());

        parseDateTime();
    }

    private void parseDateTime() {

        if (mTodo.getDate() != null)
            mPreviousDate = mTodo.getDate().substring(0, 10);
        if(mTodo.getDate() != null)
            mPreviousTime = mTodo.getDate().substring(11);
        mDate.setText(mPreviousDate);
        mTime.setText(mPreviousTime);
    }

    private void openDatePicker() {
        if (mDateDialog == null) {
            mDateDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                if (year < mCalendar.get(Calendar.YEAR) || month < mCalendar.get(Calendar.MONTH) ||
                        month == mCalendar.get(Calendar.MONTH) && dayOfMonth < mCalendar.get(Calendar.DAY_OF_MONTH)) {
                    ToastOX.error(this, getString(R.string.error_date));
                }
                else {
                    if (!Locale.getDefault().getLanguage().equals("fr"))
                        mDate.setText(year + "/" + ((month + 1) < 9 ? "0" + (month + 1) : month) + "/" + dayOfMonth);
                    else
                        mDate.setText(dayOfMonth + "/" + ((month + 1) < 9 ? "0" + (month + 1) : month) + "/" + year);
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
            mTimeDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                mTime.setText((hourOfDay < 9 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 9 ? "0" + minute : minute));
                mSelectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mSelectedCal.set(Calendar.MINUTE, minute);
            }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        }
        mTimeDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_picker:
                openDatePicker();
                break;
            case R.id.time_picker:
                openTimePicker();
                break;
            case R.id.edit_finished:
                editFinish();
                break;
            case R.id.preview:
                openGallery();
                break;
        }
    }

    private void editFinish() {
        fillData();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("todo", mTodo);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void fillData() {
        String date =  "";
        String time = "";

        mTodo.setTitle(mTitle.getText().toString());
        mTodo.setDesc(mDesc.getText().toString());
        mTodo.setDone((short) (mSwitch.isChecked() ? 1 : 0));
        mTodo.setLevelNb(mSpinner.getSelectedItemPosition());
        if (mImage != null && mImage != mTodo.getBanner())
            mTodo.setBanner(mImage);
        date = mDate.getText().toString();
        time = mTime.getText().toString();
        String dateTime = date + " " + time;
        if (mTodo.getDate() == null || mTodo.getDate().equals(dateTime))
            return;
        if (!date.equals(mPreviousDate) && !time.equals(mPreviousTime))
            mTodo.setDate(dateTime);
        else if (!date.equals(mPreviousDate))
            mTodo.setDate(date + " " + mPreviousTime);
        else if (!date.equals(mPreviousTime))
            mTodo.setDate(mPreviousDate + " " + time);
        NotificationPublisher.unScheduleNotification(this, mTodo);
        try {
            NotificationPublisher.scheduleNotification(this, mFormat.parse(mTodo.getDate()).getTime(), mTodo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent , 21);
    }

    private void resultGallery(Bitmap image) {
        image = Bitmap.createScaledBitmap(image, 600, 200, true);
        mPreview.setBackground(new BitmapDrawable(getResources(), image));
        mImage = image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 21:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap image = getDataFromResult(data);
                    if (image != null)
                        resultGallery(image);
                }
                break;
        }
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
