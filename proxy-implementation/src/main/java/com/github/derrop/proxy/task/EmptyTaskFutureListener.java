package com.github.derrop.proxy.task;

import com.github.derrop.proxy.api.task.TaskFutureListener;

public class EmptyTaskFutureListener {

    public static final TaskFutureListener<Boolean> BOOL_INSTANCE = new TaskFutureListener<Boolean>() {
    };

}
