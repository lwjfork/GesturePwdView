package com.lwjfork.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by lwj on 2018/12/27.
 * lwjfork@gmail.com
 * 手势密码指示器
 */
public class GestureLockViewIndicator extends BaseGestureLockView {

    private ArrayList<String> selectListIndex = new ArrayList<>();

    public GestureLockViewIndicator(Context context) {
        super(context, null);
    }

    public GestureLockViewIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureLockViewIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public GestureLockViewIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.init(context, attrs, defStyleAttr, defStyleRes);
        if (isInEditMode()) {
            selectListIndex = new ArrayList<>(Arrays.asList("1,4,9".split(",")));
        }
        refreshDot();
    }


    /**
     * 请求重新绘制
     *
     * @param paramString 手势密码字符序列
     */
    public void setPath(String paramString) {
        int length = paramString.length();
        selectListIndex = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            selectListIndex.add(paramString.substring(i, i + 1));
        }
        refreshDot();
    }

    public void refreshDot() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            GestureLockPointView childView = (GestureLockPointView) getChildAt(i);
            if (selectListIndex != null && selectListIndex.contains("" + i)) {
                childView.setSelectState(SelectedState.STATE_RIGHT);
            } else {
                childView.setSelectState(SelectedState.STATE_NONE);
            }
        }
    }
}