package com.lwjfork.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by lwj on 2019/1/3.
 * lwjfork@gmail.com
 * 手势密码 画点 基类
 */
public class BaseGestureLockView extends FrameLayout {


    private int defaultSpaceH_dp = 20; // 默认横向图案间距
    private int defaultSpaceV_dp = 20; // 默认竖向图案间距

    private int defaultRowNums = 3;//   默认行数
    private int defaultColumnNums = 3;//  默认列数


    protected int spaceH; // 横向图案间距
    protected int spaceV; // 竖向图案间距

    protected int rowNums;//   行数
    protected int columnNums;//  列数

    protected int dotHeight; // 点 高度
    protected int dotWidth; // 点 宽度


    protected Context context;
    protected DisplayMetrics metrics;

    protected Drawable selectDrawable;//选中 drawable
    protected Drawable normalDrawable;// 没选中drawable
    protected Drawable errorDrawable;// 选中错误drawable

    protected ArrayList<Rect> pointPosition;
    protected ArrayList<GestureLockPointView> pointViews;

    protected int lineColor; // 线的颜色
    protected int lineErrorColor; // 线的错误颜色
    protected float lineWidth; // 线的宽度
    protected long delayClearTime = 0L; //  多长时间以后清空

    protected boolean autoSelectMiddle = false;// 是否自动选中中间的点

    public BaseGestureLockView(Context context) {
        this(context, null);
    }

    public BaseGestureLockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseGestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public BaseGestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void parseBaseAttr(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GestureLockView, defStyleAttr, defStyleRes);
        rowNums = typedArray.getInteger(R.styleable.GestureLockView_rowNums, defaultRowNums);
        columnNums = typedArray.getInteger(R.styleable.GestureLockView_columnNums, defaultColumnNums);
        spaceH = typedArray.getDimensionPixelSize(R.styleable.GestureLockView_spaceH, dp2px(defaultSpaceH_dp));
        spaceV = typedArray.getDimensionPixelSize(R.styleable.GestureLockView_spaceV, dp2px(defaultSpaceV_dp));
        selectDrawable = typedArray.getDrawable(R.styleable.GestureLockView_selectDrawable);
        normalDrawable = typedArray.getDrawable(R.styleable.GestureLockView_normalDrawable);
        errorDrawable = typedArray.getDrawable(R.styleable.GestureLockView_errorDrawable);
        lineColor = typedArray.getColor(R.styleable.GestureLockView_lineColor, Color.BLACK);
        lineErrorColor = typedArray.getColor(R.styleable.GestureLockView_lineErrorColor, Color.RED);
        lineWidth = typedArray.getDimension(R.styleable.GestureLockView_lineWidth, dp2px(5f));
        autoSelectMiddle = typedArray.getBoolean(R.styleable.GestureLockView_autoSelectMiddle, false);
        delayClearTime = typedArray.getInteger(R.styleable.GestureLockView_delayClearTime, 0);
        if (selectDrawable != null && normalDrawable != null) {
            int selectDrawableWidth = selectDrawable.getIntrinsicWidth();
            int selectDrawableHeight = selectDrawable.getIntrinsicHeight();
            int normalDrawableWidth = normalDrawable.getIntrinsicWidth();
            int normalDrawableHeight = normalDrawable.getIntrinsicHeight();
            dotWidth = typedArray.getDimensionPixelSize(R.styleable.GestureLockView_dotWidth, Math.max(selectDrawableWidth, normalDrawableWidth));
            dotHeight = typedArray.getDimensionPixelSize(R.styleable.GestureLockView_dotHeight, Math.max(selectDrawableHeight, normalDrawableHeight));
            selectDrawable.setBounds(0, 0, dotWidth, dotHeight);
            normalDrawable.setBounds(0, 0, dotWidth, dotHeight);
            if (errorDrawable != null) {
                errorDrawable.setBounds(0, 0, dotWidth, dotHeight);
            }
        }
        typedArray.recycle();
        pointPosition = new ArrayList<>();
    }

    public void addChild() {
        pointViews = new ArrayList<>();
        for (int i = 0; i < rowNums; i++) {
            for (int i1 = 0; i1 < columnNums; i1++) {
                GestureLockPointView pointView = new GestureLockPointView(context);
                pointView.init(selectDrawable, normalDrawable, errorDrawable);
                pointViews.add(pointView);
                addView(pointView);
            }
        }
    }


    protected void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.context = context;
        this.metrics = context.getResources().getDisplayMetrics();
        parseBaseAttr(attrs, defStyleAttr, defStyleRes);
        addChild();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < rowNums; i++) {
            for (int j = 0; j < columnNums; j++) {
                int index = getPointIndex(i,j);
                GestureLockPointView pointView = (GestureLockPointView) getChildAt(index);
                Rect rect = pointPosition.get(index);
                pointView.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (rowNums * columnNums == 0) {  // 没有一个点
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(getSuggestWidth(widthMeasureSpec), getSuggestHeight(heightMeasureSpec));
    }

    public int getPointIndex(int rowIndex, int columnNumsIndex) {
        return rowIndex * columnNums + columnNumsIndex;
    }


    // 计算宽度
    protected int getSuggestWidth(int widthMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int contentSizeH = spaceH * (columnNums - 1) + columnNums * dotWidth;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int resultWidth;
        if (widthMode == MeasureSpec.AT_MOST) {
            resultWidth = paddingLeft + paddingRight + contentSizeH;
        } else if (widthMode == MeasureSpec.EXACTLY) { // 指定尺寸
            resultWidth = width;
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            resultWidth = paddingLeft + paddingRight + contentSizeH;
        } else {
            resultWidth = width;
        }
        int startIndexX = paddingLeft;
        if (resultWidth > paddingLeft + paddingRight + contentSizeH) {
            startIndexX = (resultWidth - contentSizeH) / 2;
        }
        for (int i = 0; i < rowNums; i++) {
            for (int j = 0; j < columnNums; j++) {
                Rect rect = new Rect();
                rect.left = startIndexX + (dotWidth + spaceH) * j;
                rect.right = rect.left + dotWidth;
                pointPosition.add(rect);
            }
        }
        return resultWidth;
    }

    // 计算高度
    protected int getSuggestHeight(int heightMeasureSpec) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int contentSizeV = spaceV * (rowNums - 1) + rowNums * dotHeight;
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int resultHeight;
        if (heightMode == MeasureSpec.AT_MOST) {
            resultHeight = paddingTop + paddingBottom + contentSizeV;
        } else if (heightMode == MeasureSpec.EXACTLY) { // 指定尺寸
            resultHeight = height;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            resultHeight = paddingTop + paddingBottom + contentSizeV;
        } else {
            resultHeight = height;
        }
        int startIndexY = paddingTop;
        if (resultHeight > paddingTop + paddingBottom + contentSizeV) {
            startIndexY = (resultHeight - contentSizeV) / 2;
        }
        for (int i = 0; i < rowNums; i++) {
            for (int j = 0; j < columnNums; j++) {
                Rect rect = pointPosition.get(getPointIndex(i, j));
                rect.top = startIndexY + (dotHeight + spaceV) * i;
                rect.bottom = rect.top + dotHeight;
            }
        }
        return resultHeight;
    }


    public int dp2px(float dp) {
        return (int) (dp * metrics.density + 0.5f);
    }


    public void setSpaceH(int spaceH) {
        this.spaceH = spaceH;
        invalidate();
    }

    public void setSpaceV(int spaceV) {
        this.spaceV = spaceV;
        invalidate();
    }

    public void setRowNums(int rowNums) {
        this.rowNums = rowNums;
        removeAllViews();
        pointPosition.clear();
        pointViews.clear();
        invalidate();
    }

    public void setColumnNums(int columnNums) {
        this.columnNums = columnNums;
        removeAllViews();
        pointPosition.clear();
        pointViews.clear();
        invalidate();
    }

    public void setDotHeight(int dotHeight) {
        this.dotHeight = dotHeight;
        invalidate();
    }

    public void setDotWidth(int dotWidth) {
        this.dotWidth = dotWidth;
        invalidate();
    }

    public void setSelectDrawable(Drawable selectDrawable) {
        this.selectDrawable = selectDrawable;
        int selectDrawableWidth = selectDrawable.getIntrinsicWidth();
        int selectDrawableHeight = selectDrawable.getIntrinsicHeight();
        dotWidth = selectDrawableWidth;
        dotHeight = selectDrawableHeight;
        selectDrawable.setBounds(0, 0, dotWidth, dotHeight);
        invalidate();
    }

    public void setNormalDrawable(Drawable normalDrawable) {
        this.normalDrawable = normalDrawable;
        int normalDrawableWidth = normalDrawable.getIntrinsicWidth();
        int normalDrawableHeight = normalDrawable.getIntrinsicHeight();
        dotWidth = normalDrawableWidth;
        dotHeight = normalDrawableHeight;
        normalDrawable.setBounds(0, 0, dotWidth, dotHeight);
        invalidate();
    }

    public void setErrorDrawable(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        errorDrawable.setBounds(0, 0, dotWidth, dotHeight);
        invalidate();
    }

    /**
     * 根据索引计算当前索引点 的行与列
     *
     * @param index
     * @return
     */
    protected final Point getRowAndColumnIndex(int index) {
        int rowIndex = index / columnNums; // 第几行
        int columnIndex = index - (columnNums * rowIndex); // 第几列
        return new Point(rowIndex, columnIndex);
    }


    protected final boolean isSameLine(int preRowIndex, int preColumnIndex, int currentRowIndex, int currentColumnIndex) {
        if ((preRowIndex - currentRowIndex) * (preColumnIndex - currentColumnIndex) == 0) {  // 同一行 or 同一列
            return true;
        }
        return Math.abs(preRowIndex - currentRowIndex) == Math.abs(preColumnIndex - currentColumnIndex); // 同一斜线
    }

    /**
     * 判断两个点是否是在同一条直线、斜线
     *
     * @param preIndex     第一个点索引
     * @param currentIndex 第二个点索引
     * @return 是否在同一条直线、斜线
     */
    protected final boolean isSameLine(int preIndex, int currentIndex) {
        Point prePoint = getRowAndColumnIndex(preIndex);
        Point currentPoint = getRowAndColumnIndex(currentIndex);
        return isSameLine(prePoint.x, prePoint.y, currentPoint.x, currentPoint.y); // 同一斜线
    }

    /**
     * 是否是同一个点
     *
     * @param preRowIndex
     * @param preColumnIndex
     * @param currentRowIndex
     * @param currentColumnIndex
     * @return
     */
    protected boolean isSamePoint(int preRowIndex, int preColumnIndex, int currentRowIndex, int currentColumnIndex) {
        return preRowIndex == currentRowIndex && preColumnIndex == currentColumnIndex;
    }


    protected ArrayList<Point> getSameLinePoint(int preIndex, int currentIndex) {
        Point prePoint = getRowAndColumnIndex(preIndex);
        Point currentPoint = getRowAndColumnIndex(currentIndex);
        return getSameLinePoint(prePoint.x, prePoint.y, currentPoint.x, currentPoint.y);
    }

    protected ArrayList<Point> getSameLinePoint(int preRowIndex, int preColumnIndex, int currentRowIndex, int currentColumnIndex) {
        if (!isSameLine(preRowIndex, preColumnIndex, currentRowIndex, currentColumnIndex)) { // 不是同一条线
            return new ArrayList<>();
        }
        if (isSamePoint(preRowIndex, preColumnIndex, currentRowIndex, currentColumnIndex)) {  // 同一个点
            return new ArrayList<>();
        }
        ArrayList<Point> result = new ArrayList<>();
        if (preRowIndex == currentRowIndex) { // 同一行
            int radius = (currentColumnIndex - preColumnIndex) / Math.abs(preColumnIndex - currentColumnIndex);
            int columnIndex = preColumnIndex + radius;
            while (columnIndex != currentColumnIndex) {
                result.add(new Point(preRowIndex, columnIndex));
                columnIndex = columnIndex + radius;
            }
        } else if (preColumnIndex == currentColumnIndex) { // 同一列
            int radius = (currentRowIndex - preRowIndex) / Math.abs(currentRowIndex - preRowIndex);
            int rowIndex = preRowIndex + radius;
            while (rowIndex != currentRowIndex) {
                result.add(new Point(rowIndex, preColumnIndex));
                rowIndex = rowIndex + radius;
            }
        } else {   //  同一斜线
            int columnRadius = (currentColumnIndex - preColumnIndex) / Math.abs(preColumnIndex - currentColumnIndex);
            int rowRadius = (currentRowIndex - preRowIndex) / Math.abs(currentRowIndex - preRowIndex);
            int rowIndex = preRowIndex + rowRadius;
            int columnIndex = preColumnIndex + columnRadius;
            while (rowIndex != currentRowIndex && columnIndex != currentColumnIndex) {
                result.add(new Point(rowIndex, columnIndex));
                rowIndex = rowIndex + rowRadius;
                columnIndex = columnIndex + columnRadius;
            }
        }
        return result;
    }

    /**
     * 判断当前坐标在第几个点的区域内
     *
     * @param rowCoordinate    横坐标
     * @param columnCoordinate 纵坐标
     * @return 当前所处点的索引
     */
    protected final int indexOfPosition(int rowCoordinate, int columnCoordinate) {
        int length = pointPosition.size();
        for (int i = 0; i < length; i++) {
            Rect rect = pointPosition.get(i);
            if (rect.contains(rowCoordinate, columnCoordinate)) {
                if (pointViews.get(i).getSelectState() != SelectedState.STATE_RIGHT) { // 防止重复选中
                    return i;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }



}