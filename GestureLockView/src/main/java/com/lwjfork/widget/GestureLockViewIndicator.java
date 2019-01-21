package com.lwjfork.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;


/**
 * Created by lwj on 2018/12/27.
 * lwjfork@gmail.com
 * 手势密码指示器
 */
public class GestureLockViewIndicator extends BaseGestureLockView {

    private ArrayList<Integer> selectListIndex = new ArrayList<>();

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
        inEditMode();
        refreshDot();
    }


    private void inEditMode() {
        if (isInEditMode()) {
            selectListIndex = new ArrayList<>();
            for (int i = 0; i < rowNums; i++) {
                for (int j = 0; j < columnNums; j++) {
                    if (i == 0 || i == j || i == rowNums - 1) {
                        int index = getPointIndex(i, j);
                        selectListIndex.add(index);
                    }
                }
            }
        }
    }


    /**
     * 请求重新绘制
     *
     * @param paramString 手势密码字符序列
     */
    @SuppressWarnings("unchecked")
    public <T> void setPath(T paramString) {
        selectListIndex = onDecodeAdapter.decodePath(paramString);
        refreshDot();
    }

    public void refreshDot() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            GestureLockPointView childView = (GestureLockPointView) getChildAt(i);
            if (selectListIndex != null && selectListIndex.contains( i)) {
                childView.setSelectState(SelectedState.STATE_RIGHT);
            } else {
                childView.setSelectState(SelectedState.STATE_NONE);
            }
        }
    }

    public void setOnDecodeAdapter(OnDecodeAdapter onDecodeAdapter) {
        this.onDecodeAdapter = onDecodeAdapter;
    }

    OnDecodeAdapter onDecodeAdapter = new DefaultDecodeAdapter();

    private class DefaultDecodeAdapter implements OnDecodeAdapter<ArrayList<Integer>> {

        @Override
        public ArrayList<Integer> decodePath(ArrayList<Integer> object) {
            return object;
        }
    }


    public interface OnDecodeAdapter<T> {

        ArrayList<Integer> decodePath(T object);

    }
}
