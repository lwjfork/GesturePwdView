package com.lwjfork.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by lwj on 2019/1/3.
 * lwjfork@gmail.com
 * 手势密码
 */
public class GestureLockView extends BaseGestureLockView {


    private Paint paint;
    private Paint errorPaint;
    private Paint currentPaint; // 当前画笔

    private int startX;
    private int startY;
    private int preIndex = -1; // 前一个点的索引
    private ArrayList<Integer> resultCode = new ArrayList<>();
    private Canvas canvas;// 画布
    private Bitmap bitmap;// 位图
    boolean drawEnable = true;
    private ArrayList<Integer> oldCode = new ArrayList<>();
    private boolean isVerify;

    private boolean isTouch = false;

    public GestureLockView(Context context) {
        super(context);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.init(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    private void inEditMode() {
        if (isInEditMode()) {
            resultCode = new ArrayList<>();
            for (int i = 0; i < rowNums; i++) {
                for (int j = 0; j < columnNums; j++) {
                    if (i == 0 || i == j || i == rowNums - 1) {
                        int index = getPointIndex(i, j);
                        resultCode.add(index);
                        setChildState(index, SelectedState.STATE_RIGHT);
                    }
                }
            }
            drawPathLine();
        }
    }


    private void initPaint() {
        paint = new Paint();
        errorPaint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWidth);
        errorPaint.setStyle(Paint.Style.STROKE);
        errorPaint.setAntiAlias(true);
        errorPaint.setColor(lineErrorColor);
        errorPaint.setStrokeWidth(lineWidth);
        currentPaint = paint;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (rowNums * columnNums == 0) {  // 没有一个点
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        int suggestWidth = getSuggestWidth(widthMeasureSpec);
        int suggestHeight = getSuggestHeight(heightMeasureSpec);
        setMeasuredDimension(suggestWidth, suggestHeight);
        bitmap = Bitmap.createBitmap(suggestWidth, suggestHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!drawEnable) {
            return true;
        }
        int eventAction = event.getAction();
        switch (eventAction) {
            case MotionEvent.ACTION_DOWN: // 按下
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE: // move
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:  // up
                actionUp(event);
                break;

            case MotionEvent.ACTION_CANCEL:
                actionUp(event);
                break;
            default:
                break;
        }
        return true;
    }

    private void actionDown(MotionEvent event) {
        isTouch = true;
        startX = (int) event.getX();
        startY = (int) event.getY();
        preIndex = indexOfPosition(startX, startY);
        if (preIndex >= 0) { // 当前处于第几个点处
            pointViews.get(preIndex).setSelectState(SelectedState.STATE_RIGHT);
            resultCode.add(preIndex);
        }
        invalidate();
    }

    private void actionMove(MotionEvent event) {
        isTouch = true;
        drawPathLine();
        int x = (int) event.getX();
        int y = (int) event.getY();
        int index = indexOfPosition(x, y);
        if (preIndex >= 0) {
            Rect preRect = pointPosition.get(preIndex);
            if (index >= 0) { // 两个点需要拼接
                Rect currentRect = pointPosition.get(index);
                canvas.drawLine(preRect.centerX(), preRect.centerY(), currentRect.centerX(), currentRect.centerY(), currentPaint);
                ArrayList<Point> indexs = new ArrayList<>();
                if (autoSelectMiddle) {
                    indexs = getSameLinePoint(preIndex, index);
                }
                if (preIndex != index) {
                    if (indexs != null && indexs.size() > 0) {
                        for (Point point : indexs) {
                            int pointIndex = getPointIndex(point.x, point.y);
                            if (pointViews.get(pointIndex).getSelectState() != SelectedState.STATE_RIGHT) {
                                setChildState(pointIndex, SelectedState.STATE_RIGHT);
                                resultCode.add(pointIndex);
                            }
                        }
                    }
                    resultCode.add(index);
                    setChildState(index, SelectedState.STATE_RIGHT);
                }
                preIndex = index;
            } else {
                canvas.drawLine(preRect.centerX(), preRect.centerY(), x, y, currentPaint);
            }
        } else {  // 第一次按下时，没有处于任何一个点的位置
            preIndex = index;
            if (preIndex >= 0) { // 当前处于第几个点处
                setChildState(preIndex, SelectedState.STATE_RIGHT);
                resultCode.add(preIndex);
            } else {
                // nothing to do
            }
        }
        invalidate();
    }

    /**
     * 画线
     */
    private void drawPathLine() {
        clearScreen();
        int count = resultCode.size();
        for (int i = 0; i < count; i++) {
            Rect firstRect = pointPosition.get(resultCode.get(i));
            int j = i + 1;
            if (j < count) {
                Rect secondRect = pointPosition.get(resultCode.get(j));
                canvas.drawLine(firstRect.centerX(), firstRect.centerY(), secondRect.centerX(), secondRect.centerY(), currentPaint);
            }
        }
    }

    private void clearScreen() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    private void resetChildState(@SelectedState int state) {
        for (GestureLockPointView pointView : pointViews) {
            pointView.setSelectState(state);
        }
    }

    public void setChildState(int childIndex, @SelectedState int state) {
        pointViews.get(childIndex).setSelectState(state);
    }


    private boolean isSame(ArrayList<Integer> inputCode, ArrayList<Integer> oldCode) {
        if (inputCode.size() != oldCode.size()) {
            return false;
        }
        int size = inputCode.size();
        for (int i = 0; i < size; i++) {
            if (inputCode.get(i) != oldCode.get(i)) {
                return false;
            }
        }
        return true;
    }

    private void actionUp(MotionEvent event) {
        isTouch = false;
        if (isVerify) { // 验证情况下  ， 错误时需要绘制 错误信息
            if (isSame(resultCode, oldCode)) {
                drawPathLine();
                reset();
                if (onGestureCallBackListener != null) {
                    onGestureCallBackListener.onCheckedSuccess(resultCode, onGetCodeAdapter.convertCode2Obj(resultCode));
                }

            } else {
                for (Integer integer : resultCode) {
                    setChildState(integer, SelectedState.STATE_ERROR);
                }
                currentPaint = errorPaint;
                drawPathLine();
                if (onGestureCallBackListener != null) {
                    onGestureCallBackListener.onCheckedFail(resultCode, onGetCodeAdapter.convertCode2Obj(resultCode));
                }
                reset();
            }

        } else {  // 输入情况下
            drawPathLine();

            if (onGestureCallBackListener != null) {
                onGestureCallBackListener.onGestureCodeInput(resultCode, onGetCodeAdapter.convertCode2Obj((ArrayList<Integer>) resultCode.clone()));
            }
            reset();
        }
    }


    /**
     * 设置以前的路径
     */
    @SuppressWarnings("unchecked")
    public <T> void setOldPath(T old) {
        oldCode = onGetCodeAdapter.convertObj2Code(old);
        isVerify = true;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, null);
        // fix 1  重绘时，线消失了，
        // fix 2  触摸重绘时，线绘制没有跟随触摸位置绘制，
        if (resultCode.size() > 0 && !isTouch) {
            drawPathLine();
        }
        super.dispatchDraw(canvas);
        inEditMode();
    }


    private void reset() {
        drawEnable = false;
        setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preIndex = -1;
                resultCode.clear();
                currentPaint = paint;
                resetChildState(SelectedState.STATE_NONE);
                clearScreen();
                drawEnable = true;
                setEnabled(true);
            }
        }, delayClearTime);

    }

    /**
     * 清空所有数据及状态
     */
    public void clearAllState() {
        preIndex = -1;
        resultCode.clear();
        oldCode.clear();
        isVerify = false;
        currentPaint = paint;
        resetChildState(SelectedState.STATE_NONE);
        clearScreen();
        drawEnable = true;
        setEnabled(true);
    }

    OnGestureCallBackListener onGestureCallBackListener;

    public void setOnGestureCallBackListener(OnGestureCallBackListener onGestureCallBackListener) {
        this.onGestureCallBackListener = onGestureCallBackListener;
    }

    public interface OnGestureCallBackListener<V> {

        /**
         * 用户设置/输入了手势密码
         */
        default void onGestureCodeInput(ArrayList<Integer> code, V inputCode) {

        }

        /**
         * 代表用户绘制的密码与传入的密码相同
         */
        default void onCheckedSuccess(ArrayList<Integer> code, V rightCode) {

        }

        /**
         * 代表用户绘制的密码与传入的密码不相同
         */
        default void onCheckedFail(ArrayList<Integer> code, V errorCode) {

        }
    }


    public interface OnCodeConvertAdapter<T, V> {

        ArrayList<Integer> convertObj2Code(T oldCode);

        V convertCode2Obj(ArrayList<Integer> code);


    }


    private class DefaultCodeConvertAdapter implements OnCodeConvertAdapter<ArrayList<Integer>, ArrayList<Integer>> {
        @Override
        public ArrayList<Integer> convertObj2Code(ArrayList<Integer> code) {
            return code;
        }

        @Override
        public ArrayList<Integer> convertCode2Obj(ArrayList code) {
            return code;
        }
    }


    private OnCodeConvertAdapter onGetCodeAdapter = new DefaultCodeConvertAdapter();

    public void setOnCodeConvertAdapter(OnCodeConvertAdapter onGetCodeAdapter) {
        this.onGetCodeAdapter = onGetCodeAdapter;
    }

    private void log(String string, Object... args) {
        Log.e("GestureLockView", String.format(string, args));
    }
}
