package com.list.smoothlist.callback;

import com.list.smoothlist.model.ToDo;

public interface OnTaskEvent {
    public void onTaskChanged(ToDo note);
}
