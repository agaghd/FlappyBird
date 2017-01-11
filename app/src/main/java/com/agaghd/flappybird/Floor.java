package com.agaghd.flappybird;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * Created by 15150 on 2017/1/11.
 */
public class Floor {

    /**
     * 地板位置游戏面板高度的4/5到底部
     */
    private static final float FLOOR_Y_POS_RADIO = 4 / 5f;//height of 4/5

    /**
     * x坐标
     */
    private int x;

    /**
     * y坐标
     */
    private int y;

    /**
     * 填充物
     */
    private BitmapShader mFloorShader;

    private int mGamewidth;

    private int mGameHeight;

    public Floor(int gameWidth, int gameHeight, Bitmap floorBg) {
        mGamewidth = gameWidth;
        mGameHeight = gameHeight;
        y = (int) (gameHeight * FLOOR_Y_POS_RADIO);
        mFloorShader = new BitmapShader(floorBg, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    }

    public void draw(Canvas mCanvas, Paint paint) {
        if (-x > mGamewidth) {
            x = x % mGamewidth;
        }
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        //移动到指定的位置
        mCanvas.translate(x, y);
        paint.setShader(mFloorShader);
        mCanvas.drawRect(x, 0, -x + mGamewidth, mGameHeight - y, paint);
        mCanvas.restore();
        paint.setShader(null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
}
