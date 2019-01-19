package com.lwjfork.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by lwj on 2019/1/3.
 * lwjfork@gmail.com
 * 手势密码--点 View
 */
@SuppressWarnings("all")
public class GestureLockPointView extends ImageView {

    @SelectedState
    public int selectState = SelectedState.STATE_UNKNOWN;
    protected Drawable selectDrawable;//选中 drawable
    protected Drawable normalDrawable;// 没选中drawable
    protected Drawable errorDrawable;// 选中错误drawable

    public GestureLockPointView(Context context) {
        super(context);
    }

    public GestureLockPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureLockPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public GestureLockPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Drawable selectDrawable, Drawable normalDrawable, Drawable errorDrawable) {
        this.selectDrawable = selectDrawable;
        this.normalDrawable = normalDrawable;
        this.errorDrawable = errorDrawable;
        setSelectState(SelectedState.STATE_NONE);
    }

    @SelectedState
    public int getSelectState() {
        return selectState;
    }

    public void setSelectState(@SelectedState int selectState) {
        if (this.selectState == selectState) {
            return;
        }
        this.selectState = selectState;
        switch (selectState) {
            case SelectedState.STATE_NONE:
                if (normalDrawable != null) {
                    setImageDrawable(normalDrawable);
                }
                break;
            case SelectedState.STATE_RIGHT:
                if (selectDrawable != null) {
                    setImageDrawable(selectDrawable);
                }
                break;
            case SelectedState.STATE_ERROR:
                if (errorDrawable != null) {
                    setImageDrawable(errorDrawable);
                }
                break;
        }
    }

    public Drawable getSelectDrawable() {
        return selectDrawable;
    }

    public void setSelectDrawable(Drawable selectDrawable) {
        this.selectDrawable = selectDrawable;
        invalidate();
    }

    public Drawable getNormalDrawable() {
        return normalDrawable;
    }

    public void setNormalDrawable(Drawable normalDrawable) {
        this.normalDrawable = normalDrawable;
        invalidate();
    }

    public Drawable getErrorDrawable() {
        return errorDrawable;
    }

    public void setErrorDrawable(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        invalidate();
    }
}
