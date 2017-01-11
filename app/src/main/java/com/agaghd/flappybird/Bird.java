package com.agaghd.flappybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.renderscript.Sampler;
import android.util.TypedValue;

/**
 * Created by 15150 on 2017/1/10.
 */
public class Bird {

    /**
     * 鸟在屏幕高度的2/3位置
     */
    private static final float RADIO_POS_HEIGHT = 7 / 12f;

    /**
     * 鸟的宽度30dp
     */
    private static final int BIRD_SIZE = 30;

    /**
     * 鸟的横坐标
     */
    private int x;

    /**
     * 鸟的纵坐标
     */
    private int y;

    /**
     * 鸟的宽度
     */
    private int mWidth;

    /**
     * 鸟的高度
     */
    private int mHeight;

    /**
     * 鸟的Bitmap
     */
    private Bitmap bitmap;

    /**
     * 鸟的绘制范围
     */
    private RectF rect = new RectF();

    public Bird(Context context, int gameWidth, int gameHeight, Bitmap bitmap) {
        this.bitmap = bitmap;
        //鸟的位置
        x = gameWidth / 2 - bitmap.getWidth() / 2;
        y = (int) (gameHeight * RADIO_POS_HEIGHT);

        //计算鸟的宽度和高度
        mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BIRD_SIZE, context.getResources().getDisplayMetrics());
        mHeight = (int) (mWidth * 1.0f / bitmap.getWidth() * bitmap.getHeight());
    }

    /**
     * 绘制自己
     *
     * @param canvas 画布
     */
    public void draw(Canvas canvas) {
        rect.set(x, y, x + mWidth, y + mHeight);
        canvas.drawBitmap(bitmap, null, rect, null);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getmWidth() {
        return mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }
}
