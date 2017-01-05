package com.list.smoothlist.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.list.smoothlist.R;
import com.list.smoothlist.database.DBManager;
import com.list.smoothlist.model.ToDo;
import com.onurciner.toastox.ToastOX;
import com.onurciner.toastox.ToastOXDialog;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnLongClickListener {

    private ArrayList<ToDo> mTodoList;
    private Context mContext;
    private CoordinatorLayout mCoord;

    private int lastPosition;

    public RecyclerAdapter(Context context, ArrayList<ToDo> list, CoordinatorLayout coord) {
        mTodoList = list;
        mCoord = coord;
        mContext = context;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_todo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ToDo todo = mTodoList.get(position);

        holder.mTitle.setText(todo.getTitle());
        holder.mDesc.setText(todo.getDesc());
        holder.mLevel.setBackground(todo.getLevel());
        holder.mCard.setTag(position);
        holder.mCard.setOnLongClickListener(this);

        if (!todo.isFromDB())
            setAnimation(holder.mCard, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mTodoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCard;
        private TextView mTitle;
        private TextView mDesc;
        private ImageView mLevel;

        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.title);
            mLevel = (ImageView) v.findViewById(R.id.level);
            mDesc = (TextView) v.findViewById(R.id.desc);
            mCard = (CardView) v.findViewById(R.id.card);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = (Integer) v.getTag();
        ToDo todo = mTodoList.get(pos);
        Handler handler = new Handler();
        Runnable run = () -> {
            boolean ret = DBManager.getInstance(mContext).deleteNote(todo);
            Log.d("RecyclerDeleteNote", "IsItemDeleted ? " + ret);
        };

        handler.postDelayed(run, 3800);
        mTodoList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());

        Snackbar snackbar = Snackbar
                .make(mCoord, mContext.getString(R.string.delete_note), 3500)
                .setAction(mContext.getString(R.string.cancel), view -> {
                    mTodoList.add(pos, todo);
                    handler.removeCallbacks(run);
                    notifyDataSetChanged();
                    Snackbar snackbar1 = Snackbar.make(mCoord, mContext.getString(R.string.cancel_delete), 1000);
                    snackbar1.show();
                });
        snackbar.show();
        return true;
    }
}
