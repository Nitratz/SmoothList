package com.list.smoothlist.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.list.smoothlist.activity.MainActivity;
import com.list.smoothlist.R;
import com.list.smoothlist.activity.ViewEditActivity;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.model.ToDo;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnLongClickListener, View.OnClickListener {

    private ArrayList<ToDo> mFilterList;
    private ArrayList<ToDo> mFullList;
    private Drawable[] mDrawables;

    private Context mContext;
    private CoordinatorLayout mCoord;

    private int lastPosition;

    public RecyclerAdapter(Context context, ArrayList<ToDo> list, ArrayList<ToDo> fullList, CoordinatorLayout coord) {
        mFilterList = list;
        mFullList = fullList;
        mCoord = coord;
        mContext = context;
        mDrawables = new Drawable[3];
        mDrawables[0] = ContextCompat.getDrawable(mContext, R.drawable.normal);
        mDrawables[1] = ContextCompat.getDrawable(mContext, R.drawable.important);
        mDrawables[2] = ContextCompat.getDrawable(mContext, R.drawable.vimportant);
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_todo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ToDo todo = mFilterList.get(position);

        if (todo.getBanner() != null) {
            Bitmap image = BitmapFactory.decodeByteArray(todo.getBannerArray(), 0, todo.getBannerArray().length);
            holder.mHeader.setBackground(new BitmapDrawable(mContext.getResources(), image));
        }
        else
            holder.mHeader.setBackgroundColor(ContextCompat.getColor(mContext, R.color.card_header));
        if (todo.isDone() == 1)
            holder.isDone.setVisibility(View.VISIBLE);
        else
            holder.isDone.setVisibility(View.GONE);
        holder.mTitle.setText(todo.getTitle());
        holder.mDesc.setText(todo.getDesc());
        holder.mLevel.setBackground(mDrawables[todo.getLevelNb()]);
        if (todo.getDate() == null)
            holder.mDate.setText(mContext.getString(R.string.no_date));
        else
            holder.mDate.setText(todo.getDate());

        holder.mCard.setTag(position);
        holder.mEdit.setTag(position);
        holder.mEdit.setOnClickListener(this);
        holder.mCard.setOnLongClickListener(this);

        if (!todo.isFromDB())
            setAnimation(holder.mCard, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCard;
        private LinearLayout mHeader;
        private TextView mTitle;
        private TextView mDesc;
        private TextView mDate;
        private Button mEdit;
        private LinearLayout isDone;
        private ImageView mLevel;

        ViewHolder(View v) {
            super(v);

            mHeader = (LinearLayout) v.findViewById(R.id.header);
            mTitle = (TextView) v.findViewById(R.id.title);
            mLevel = (ImageView) v.findViewById(R.id.level);
            mDesc = (TextView) v.findViewById(R.id.desc);
            isDone = (LinearLayout) v.findViewById(R.id.is_done);
            mDate = (TextView) v.findViewById(R.id.date_field);
            mEdit = (Button) v.findViewById(R.id.edit);
            mCard = (CardView) v.findViewById(R.id.card);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = (Integer) v.getTag();
        ToDo todo = mFilterList.get(pos);
        Handler handler = new Handler();
        Runnable run = () -> {
            boolean ret = DBManager.getInstance(mContext).deleteNote(todo);
            Log.d("RecyclerDeleteNote", "IsItemDeleted ? " + ret);
        };

        handler.postDelayed(run, 2010);
        mFilterList.remove(pos);
        int fPos = removeInFullList(todo);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());

        Snackbar snackbar = Snackbar
                .make(mCoord, mContext.getString(R.string.delete_note), 2000)
                .setAction("Test", view -> {

                })
                .setAction(mContext.getString(R.string.cancel), view -> {
                    mFilterList.add(pos, todo);
                    mFullList.add(fPos, todo);
                    handler.removeCallbacks(run);
                    notifyDataSetChanged();
                    Snackbar snackbar1 = Snackbar.make(mCoord, mContext.getString(R.string.cancel_delete), 1000);
                    snackbar1.show();
                });
        snackbar.show();
        return true;
    }

    private int removeInFullList(ToDo _todo) {
        for (ToDo todo : mFullList) {
            if (todo.equals(_todo)) {
                int pos = mFullList.indexOf(todo);
                mFullList.remove(pos);
                return pos;
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer) v.getTag();
        ToDo todo = mFilterList.get(pos);

        Intent intent = new Intent(mContext, ViewEditActivity.class);
        intent.putExtra("todo", todo);
        ((MainActivity) mContext).startActivityForResult(intent, 42);
        ((MainActivity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
