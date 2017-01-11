package com.agaghd.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15150 on 2017/1/10.
 */
public class GamgFlabbyBird extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;

    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;

    /**
     * 用于绘制的线程
     */
    private Thread t;

    /**
     * 线程的控制开关
     */
    private boolean isRunning;

    /**
     * 当前View的尺寸
     */
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();

    /**
     * 背景
     */
    private Bitmap mBg;

    /**
     * 关鸟的事O(∩_∩)O
     */
    private Bird mBird;
    private Bitmap mBirdBitmap;

    /**
     * 地板
     */
    private Floor mFloor;
    private Bitmap mFloorBg;

    private Paint mPaint;

    private int mSpeed;


    /**
     * 管道相关
     */
    private Bitmap mPipeTop;
    private Bitmap mPipeBottom;
    private RectF mPipeRect;
    private int mPipeWidth;

    /**
     * 管道的宽度60dp
     */
    private static final int PIPE_WIDTH = 60;

    /**
     * 管道不止一个，所以要有一个列表管理生成的管道
     */
    private List<Pipe> mPipes = new ArrayList<>();

    /**
     * 要移除的管道
     */
    private List<Pipe> mNeedRemovePipe = new ArrayList<>();


    /**
     * 分数
     */
    private final int[] mNums = new int[]{R.drawable.n0, R.drawable.n1,
            R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8, R.drawable.n9};

    private Bitmap[] mNumBitmap;

    private int mGrade = 0;

    private int mRemovedPipe = 0;

    /**
     * 单个数字的高度的1/15
     */
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15f;

    /**
     * 单个数字的宽度
     */
    private int mSingleGradeWidth;

    /**
     * 单个数字的高度
     */
    private int mSingleGradeHeight;

    /**
     * 单个数字的范围
     */
    private RectF mSingleNumRectf;

    /**
     * 游戏的三种状态
     */
    private enum GameStatus {
        WATTING, RUNNING, STOP;
    }

    /**
     * 记录游戏的状态
     */
    private GameStatus mStatus = GameStatus.WATTING;

    /**
     * 触摸上升的高度，负值
     */
    private static final int Touch_UP_SIZE = -12;

    /**
     * 将上升的距离转换为PX，这里多储存一个变量，变量在run中计算
     */
    private final int mBirdUpDis = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Touch_UP_SIZE, getResources().getDisplayMetrics());

    private int mTmpBirdDis;

    private final int mAutoDownSpeed = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics());

    /**
     * 管道的横向距离为300dp
     */
    private final int PIPE_DIS_BETWEEN_TWO = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

    /**
     * 记录移动的距离，达到300dp则生成一个管道
     */
    private int mTmpMoveDistance;

    /**
     * 单参数的构造方法调用两参数的构造方法，第二个参数我们传空
     *
     * @param context 上下文
     */
    public GamgFlabbyBird(Context context) {
//        super(context);
        this(context, null);
    }

    /**
     * 核心构造方法，必须要，Xml
     *
     * @param context 上下文
     * @param attrs   属性
     */
    public GamgFlabbyBird(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);

        //设置画布，背景透明
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        //设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        initBitmaps();

        //初始化速度
        mSpeed = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Mode.GameMode, context.getResources().getDisplayMetrics());
        //计算管道宽度
        mPipeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PIPE_WIDTH, context.getResources().getDisplayMetrics());
    }


    public GamgFlabbyBird(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化图片
     */
    private void initBitmaps() {
        mBg = loadImageByResId(R.drawable.bg1);
        mBirdBitmap = loadImageByResId(R.drawable.b1);
        mFloorBg = loadImageByResId(R.drawable.floor_bg2);
        mPipeTop = loadImageByResId(R.drawable.g2);
        mPipeBottom = loadImageByResId(R.drawable.g1);

        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNums.length; i++) {
            mNumBitmap[i] = loadImageByResId(mNums[i]);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //TODO Auto-generated method stub
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            logic();
            draw();
            long end = System.currentTimeMillis();

            try {
                if (end - start < 30) {
                    Thread.sleep(30 - end + start);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    private void draw() {
        try {
            //获得canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                //draw
                drawBg();
                drawBird();
                drawPipes();
                drawFloor();
                drawGrades();

                //更新我们地板绘制的x坐标
//                mFloor.setX(mFloor.getX() - mSpeed);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawBitmap(mBg, null, mGamePanelRect, null);
    }

    /**
     * 画个鸟啊
     */
    private void drawBird() {
        mBird.draw(mCanvas);
    }

    /**
     * 画个管啊
     */
    private void drawPipes() {
        for (Pipe pipe : mPipes) {
//            pipe.setX(pipe.getX() - mSpeed);
            pipe.draw(mCanvas, mPipeRect);
        }
    }

    /**
     * 画个地皮啊
     */
    private void drawFloor() {
        mFloor.draw(mCanvas, mPaint);
    }

    private void drawGrades() {
        String grade = mGrade + "";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth / 2 - grade.length() * mSingleGradeWidth / 2, 1f / 8 * mHeight);
        //draw single num one by one
        for (int i = 0; i < grade.length(); i++) {
            String numStr = grade.substring(i, i + 1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num], null, mSingleNumRectf, null);
            mCanvas.translate(mSingleGradeWidth, 0);
        }
        mCanvas.restore();
    }

    /**
     * 初始化尺寸相关
     *
     * @param w    新宽
     * @param h    新高
     * @param oldw 旧宽
     * @param oldh 旧高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        mGamePanelRect.set(0, 0, w, h);

        //初始化Bird
        mBird = new Bird(getContext(), mWidth, mHeight, mBirdBitmap);

        //初始化管道范围
        mPipeRect = new RectF(0, 0, mPipeWidth, mHeight);
        Pipe pipe = new Pipe(getContext(), w, h, mPipeTop, mPipeBottom);
        mPipes.add(pipe);

        //初始化地板
        mFloor = new Floor(mWidth, mHeight, mFloorBg);

        //初始化分数
        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectf = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);
    }

    /**
     * 根据ID加载图片
     *
     * @param resId 资源id
     * @return 图片
     */
    private Bitmap loadImageByResId(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 处理一些逻辑上的计算
     */
    private void logic() {
        switch (mStatus) {
            case RUNNING:

                //要清零，不然越加越多
                mGrade = 0;

                //更新我们地板绘制的x坐标，地板移动
                mFloor.setX(mFloor.getX() - mSpeed);


                //管道移动
                for (Pipe pipe : mPipes) {
                    if (pipe.getX() < -mPipeWidth) {
                        mNeedRemovePipe.add(pipe);
                        mRemovedPipe++;
                        continue;
                    }
                    pipe.setX(pipe.getX() - mSpeed);
                }
                //移除管道
                mPipes.removeAll(mNeedRemovePipe);

                //管道
                mTmpMoveDistance += mSpeed;
                //生成一个管道
                if (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO) {
                    Pipe pipe = new Pipe(getContext(), getWidth(), getHeight(), mPipeTop, mPipeBottom);
                    mPipes.add(pipe);
                    mTmpMoveDistance = 0;
                }

                //鸟默认下落，点击时瞬间上升
                mTmpBirdDis += mAutoDownSpeed;
                mBird.setY(mBird.getY() + mTmpBirdDis);

                //计算分数
                mGrade += mRemovedPipe;
                for (Pipe pipe : mPipes) {
                    if (pipe.getX() + mPipeWidth < mBird.getX()) {
                        mGrade++;
                    }
                }
                checkGameOver();

                break;

            case STOP:
                //如果鸟还在空中，先让他掉下来
                if (mBird.getY() < mFloor.getY() - mBird.getmWidth()) {
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setY(mBird.getY() + mTmpBirdDis);
                } else {
                    mStatus = GameStatus.WATTING;
                    initPos();
                }
                break;
            default:
                break;

        }
    }

    /**
     * 重置鸟的未知等数据
     */
    private void initPos() {
        mPipes.clear();
        mNeedRemovePipe.clear();
        //重置鸟的位置
        mBird.setY(mHeight * 7 / 12);
        //重置下落速度
        mTmpBirdDis = 0;
        //重置分数等
        mGrade = 0;
        mRemovedPipe = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            switch (mStatus) {
                case WATTING:
                    mStatus = GameStatus.RUNNING;
                    break;
                case RUNNING:
                    mTmpBirdDis = mBirdUpDis;
                    break;
            }
        }
        return true;
    }

    /**
     * 检查是否gg
     */
    private void checkGameOver() {
        //如果触碰地板，gg
        if (mBird.getY() > mFloor.getY() - mBird.getmHeight()) {
            mStatus = GameStatus.STOP;
        }
        //如果触碰管道
        for (Pipe wall : mPipes) {
            //已经穿过的
            if (wall.getX() + mPipeWidth < mBird.getX()) {
                continue;
            }
            if (wall.touchBird(mBird)) {
                mStatus = GameStatus.STOP;
            }
        }
    }
}
