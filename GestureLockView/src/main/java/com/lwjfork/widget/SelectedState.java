package com.lwjfork.widget;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.lwjfork.widget.SelectedState.STATE_ERROR;
import static com.lwjfork.widget.SelectedState.STATE_NONE;
import static com.lwjfork.widget.SelectedState.STATE_RIGHT;


/**
 * Created by lwj on 2019/1/3.
 * lwjfork@gmail.com
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({STATE_NONE, STATE_ERROR, STATE_RIGHT})
public @interface SelectedState {
    int STATE_UNKNOWN = -1; // 未知状态
    int STATE_NONE = 0; //  没有选中
    int STATE_ERROR = 1; // 选中但是错误
    int STATE_RIGHT = 2; // 选中正确
}
