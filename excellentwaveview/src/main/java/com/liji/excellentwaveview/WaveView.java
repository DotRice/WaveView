package com.liji.excellentwaveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：liji on 2016/1/15 10:14
 * 邮箱：lijiwork@sina.com
 */
public class WaveView extends View {


    /**
     * 水流波动控件 更多详解见博客
     *
     * @author chenjing
     *
     */
    /**
     * View宽度
     */
    private int mViewWidth;

    /**
     * View高度
     */
    private int mViewHeight;

    /**
     * 水位线
     */
    private float mLevelLine;

    /**
     * 波浪起伏幅度
     */
    private float mWaveHeight = 200;

    /**
     * 波长
     */
    private float mWaveWidth = 400;

    /**
     * 被隐藏的最左边的波形
     */
    private float mLeftSide;
    /**
     * 移动的距离
     */
    private float mMoveLen;

    /**
     * 水波平移速度
     */
    public static final float SPEED = 5f;

    /**
     * 二阶贝塞尔曲线
     */
    private List<Point> mPointsList;

    private Paint mPaint;

    /**
     * 进度文字画笔
     */
    private Paint mTextPaint;

    /**
     * 边界画笔
     */
    private Paint mPaintCir;
    private Paint mPaintLine;

    /**
     * 波纹路径
     */
    private Path mWavePath;
    private Path mWaveStrokePath;

    private Timer timer;

    private MyTimerTask mTask;

    private Context context;

    private boolean isMeasured = false;
    private int progress = 0;
    private int waveColor;
    private int waveStrokeWidth;
    private int waveProgressTextColor;
    private int waveProgressTextSize;
    private boolean isShowWaveTextProgress = false;

    private boolean isSecondDraw = true;


    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // 记录平移总位移
            mMoveLen += SPEED;

            mLeftSide += SPEED;
            // 波形平移
            for (int i = 0; i < mPointsList.size(); i++) {
                mPointsList.get(i).setX(mPointsList.get(i).getX() + SPEED);
                switch (i % 4) {
                    case 0:
                    case 2:
                        mPointsList.get(i).setY(mLevelLine);
                        break;
                    case 1:
                        mPointsList.get(i).setY(mLevelLine + mWaveHeight);
                        break;
                    case 3:
                        mPointsList.get(i).setY(mLevelLine - mWaveHeight);
                        break;
                }
            }
            if (mMoveLen >= mWaveWidth) {
                // 波形平移超过一个完整波形后复位
                mMoveLen = 0;
                resetPoints();
            }
            invalidate();
        }

    };

    /**
     * 所有点的x坐标都还原到初始状态，也就是一个周期前的状态
     */
    private void resetPoints() {
        mLeftSide = -mWaveWidth;
        for (int i = 0; i < mPointsList.size(); i++) {
            mPointsList.get(i).setX(i * mWaveWidth / 4 - mWaveWidth);
        }
    }

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttr(context, attrs, defStyle);
        init(context);
    }

    /**
     * 获取自定义的属性值
     *
     * @param attrs
     */
    private void getAttr(Context context, AttributeSet attrs, int defStyle) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView, defStyle, 0);

        waveColor = a.getColor(R.styleable.WaveView_wave_wavecolor, Color.BLUE);
        waveProgressTextColor = a.getColor(R.styleable.WaveView_wave_progress_text_color, Color.WHITE);
        isShowWaveTextProgress = a.getBoolean(R.styleable.WaveView_wave_progress_text_show, false);
        waveProgressTextSize = DensityUtil.dip2px(context, 18);
        waveStrokeWidth = DensityUtil.dip2px(context, 5);
        a.recycle();

    }

    private void init(Context context) {
        this.context = context;
        mPointsList = new ArrayList<Point>();
        timer = new Timer();


        //波纹，下面填充蓝色
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(waveColor);

        mTextPaint = new Paint();
        mTextPaint.setColor(waveProgressTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(waveProgressTextSize);

        mPaintCir = new Paint();
        mPaintCir.setStrokeWidth(waveStrokeWidth);
        mPaintCir.setStyle(Paint.Style.STROKE);
        mPaintCir.setAntiAlias(true);
        mPaintCir.setColor(waveColor);

        mPaintLine = new Paint();
        mPaintLine.setStrokeWidth(waveStrokeWidth);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(Color.BLUE);


        mWavePath = new Path();
        mWaveStrokePath = new Path();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    public void start() {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    /******
     * 设置充电进度
     *********/
    public void setProgress(int chargePercent) {
        if (chargePercent < 0 || chargePercent > 100) {
            progress = 100;
        } else {
            // 水位百分比,progress越大水位线越低
            progress = 100 - (chargePercent);
        }
        mLevelLine = mViewHeight;
        mLevelLine = mLevelLine * progress / 100;
        if (mLevelLine < 0)
            mLevelLine = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasured) {
            mViewHeight = getMeasuredHeight();
            mViewWidth = getMeasuredWidth();
            //宽和高的差在40以内视为宽高数据获得正确，否则视为错误
            if (mViewWidth - mViewHeight < 40 && !(mViewWidth < mViewHeight)) {
                isMeasured = true;
                // 水位线从最底下开始上升
                mLevelLine = mViewHeight;
                {//计算水位线
                    mLevelLine = mLevelLine * progress / 100;
                    if (mLevelLine < 0)
                        mLevelLine = 0;
                }
                // 根据View宽度计算波形峰值
                mWaveHeight = mViewWidth / 20f;
                // 波长等于四倍View宽度也就是View中只能看到四分之一个波形，这样可以使起伏更明显
                mWaveWidth = mViewWidth;
                // 左边隐藏的距离预留一个波形
                mLeftSide = -mWaveWidth;
                // 这里计算在可见的View宽度中能容纳几个波形，注意n上取整
                int n = (int) Math.round(mViewWidth / mWaveWidth + 0.5);
                // n个波形需要4n+1个点，但是我们要预留一个波形在左边隐藏区域，所以需要4n+5个点
                for (int i = 0; i < (4 * n + 5); i++) {
                    // 从P0开始初始化到P4n+4，总共4n+5个点
                    float x = i * mWaveWidth / 4 - mWaveWidth;
                    float y = 0;
                    switch (i % 4) {
                        case 0:
                        case 2:
                            // 零点位于水位线上
                            y = mLevelLine;
                            break;
                        case 1:
                            // 往下波动的控制点
                            y = mLevelLine + mWaveHeight;
                            break;
                        case 3:
                            // 往上波动的控制点
                            y = mLevelLine - mWaveHeight;
                            break;
                    }
                    mPointsList.add(new Point(x, y));
                }
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mWavePath.reset();
        int i = 0;
        mWavePath.moveTo(mPointsList.get(0).getX(), mPointsList.get(0).getY());
        for (; i < mPointsList.size() - 2; i = i + 2) {
            mWavePath.quadTo(mPointsList.get(i + 1).getX(), mPointsList.get(i + 1).getY(),
                    mPointsList.get(i + 2).getX(), mPointsList.get(i + 2).getY());
        }
        mWavePath.lineTo(mPointsList.get(i).getX(), mViewHeight);
        mWavePath.lineTo(mLeftSide, mViewHeight);
        mWavePath.close();

        // mPaint的Style是FILL，会填充整个Path区域
        canvas.drawPath(mWavePath, mPaint);

        if (isShowWaveTextProgress) {
            // 绘制百分比
            canvas.drawText("" + ((int) ((1 - mLevelLine / mViewHeight) * 100)) + "%", mViewWidth / 2, mLevelLine
                    + mWaveHeight + (mViewHeight - mLevelLine - mWaveHeight) / 2, mTextPaint);
        }

        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaintCir);
    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    class Point {
        private float x;

        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
            Log.d("WaveViewDemo", "( " + x + " , " + y + " )");
        }

    }

}

