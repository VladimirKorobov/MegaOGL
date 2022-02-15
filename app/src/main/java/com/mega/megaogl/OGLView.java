package com.mega.megaogl;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;

import java.util.Calendar;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class OGLView extends GLSurfaceView {
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;

    final public PointF speedUp = new PointF();
    final public PointF speedDown = new PointF();
    final PointF center = new PointF();
    public int width;
    public int height;
    float controlRadius;
    private RemoteControl remoteControl;

    int buttonNumber;
    boolean startLeftClick = false;
    boolean startRightClick = false;

    int pointer0Id = INVALID_POINTER_ID;
    int pointer1Id = INVALID_POINTER_ID;

    float pointer0X;
    float pointer0Y;
    float pointer1X;
    float pointer1Y;

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            switch (buttonNumber) {
                case RemoteControl.SPEED_UP:
                    remoteControl.speedInc();
                    break;
                case RemoteControl.SPEED_DOWN:
                    remoteControl.speedDec();
                    break;
                case RemoteControl.DIR_LEFT:
                    remoteControl.speedRightLeft(-0.1f);
                    break;
                case RemoteControl.DIR_RIGHT:
                    remoteControl.speedRightLeft(0.1f);
                    break;
                default:
                    break;
            }
            handler.postDelayed(this, 200);
        }
    };

    public OGLView(Context context) {
        super(context);
    }

    public void setRemoteControl(RemoteControl control) {
        remoteControl = control;
        remoteControl.view = this;
    }

    private boolean inside(float x, float y, PointF point) {
        return ((point.x - x) * (point.x - x) + (point.y - y) * (point.y - y)) <=
                controlRadius * controlRadius;
    }

    private int insideButton(float x, float y) {
        float[] f = remoteControl.buttonViewCoord;
        for(int i = 0; i < f.length; i += 2) {
            if((f[i] - x) * (f[i] - x) + (f[i + 1] - y) * (f[i + 1] - y) <=
            remoteControl.buttonViewSize * remoteControl.buttonViewSize)
            return i / 2;
        }
        return - 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent m) {

        int action = m.getActionMasked();

        float x0 = m.getX();
        float y0 = m.getY();

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                pointer0Id = m.getPointerId(0);
                pointer0X = x0;
                pointer0Y = y0;
                buttonNumber = insideButton(x0, y0);
                if(buttonNumber > -1) {
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    handler.postDelayed(mLongPressed, 1000);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if(pointer1Id == INVALID_POINTER_ID)
                {
                    int index = m.getActionIndex();
                    pointer1Id = m.getPointerId(index);
                    pointer1X = m.getX(index);
                    pointer1Y = m.getY(index);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(pointer0Id != INVALID_POINTER_ID) {
                    float dx0 = x0 - pointer0X;
                    float dy0 = y0 - pointer0Y;

                    if (pointer1Id != INVALID_POINTER_ID) {
                        int index = m.findPointerIndex(pointer1Id);
                        if (index >= 0) {
                            float x1 = m.getX(index);
                            float y1 = m.getY(index);

                            float dx1 = x1 - pointer1X;
                            float dy1 = y1 - pointer1Y;

                            if (dy1 * dy0 > 0) {// up or down
                                speedUp.y = speedDown.y = (y1 + y0) / 2;
                            } else {
                                float dy = (dy0 - dy1) / 2;
                                if (inside(x0, y0, speedUp) && inside(x1, y1, speedDown)) {
                                    ;
                                } else if (inside(x0, y0, speedDown) && inside(x1, y1, speedUp)) {
                                    dy = -dy;
                                }

                                speedUp.y = center.y + dy;
                                speedDown.y = center.y - dy;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if(clickDuration < MAX_CLICK_DURATION) {
                    switch (buttonNumber) {
                        case RemoteControl.SPEED_UP:
                            remoteControl.speedInc();
                            break;
                        case RemoteControl.SPEED_DOWN:
                            remoteControl.speedDec();
                            break;
                        case RemoteControl.DIR_LEFT:
                            remoteControl.speedRightLeft(-0.1f);
                            break;
                        case RemoteControl.DIR_RIGHT:
                            remoteControl.speedRightLeft(0.1f);
                            break;
                        default:
                            break;
                    }
                }
                handler.removeCallbacks(mLongPressed);
                buttonNumber = -1;
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld)
    {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        width = xNew;
        height = yNew;
        center.x = xNew / 2;
        center.y = yNew / 2;
        remoteControl.sizeChanged(width, height);

        speedUp.x = remoteControl.buttonCoord[0];
        speedUp.y = remoteControl.buttonCoord[1];

        speedDown.x = remoteControl.buttonCoord[2];
        speedDown.y = remoteControl.buttonCoord[3];

        controlRadius = Math.min(xNew, yNew) * 0.2f;
    }
}
