package com.ashlikun.animmenu;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

/**
 * Created by lilei on 2016/11/6.
 */

public class AnimMenuItem extends View implements ValueAnimator.AnimatorUpdateListener {


    private Paint mPaint;
    private TextPaint mTextPaint;
    private Path mPath;
    private Matrix matrix;
    private float strokeScale;
    private float factor;
    private float offSet;
    private int extra;
    private boolean isPress;
    private boolean isOpen;
    Rect mRect = null;
    Builder builder;
    private OnMenuItemClickListener itemClickListener;
    private OnMenuItemClickListener.OnMenuItemAnimListener itemAnimListener;
    int xA = 0, xB = 0, xC = 0, xD = 0, yA = 0, yB = 0, yC = 0, yD = 0;

    private AnimMenuItem(Builder builder) {
        super(builder.context);
        this.builder = builder;
        if (!builder.isSwitchButton) {
            setAlpha(0);
            setVisibility(View.GONE);
        }
        init();
    }


    private void init() {
        mPaint = new Paint();
        matrix = new Matrix();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(builder.badgeColor);
        mTextPaint.setTextSize(builder.badgeSize);
        mTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        strokeScale = 1 - builder.strokeWidth / (builder.circleRadius * 2f);
        mPath = new Path();
        extra = (int) (builder.circleRadius * 2 * factor / 5);
        offSet = builder.circleRadius * 2 / 3.6f;
        factor = 0;
        isPress = false;
        isOpen = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        changPath();
        initBackPaint();
        canvas.drawPath(mPath, mPaint);
        if (builder.isDrawStroke) {
            initStrokePaint();
            matrix.setScale(strokeScale, strokeScale,
                    mRect.centerX(), mRect.centerY());
            canvas.save();
            canvas.setMatrix(matrix);
            canvas.drawPath(mPath, mPaint);
            canvas.restore();
        }

        // bitmap
        mPaint.setFilterBitmap(true);
        if (builder.mBitmap != null) {
            canvas.drawBitmap(builder.isSwitchButton && isOpen ? builder.mOnBitmap : builder.mBitmap, null, mRect, mPaint);
        }
        if (builder.badge != null && builder.badge.length() > 0) {
            drawText(canvas);
        }
        super.onDraw(canvas);
    }

    private void drawText(Canvas canvas) {
        //文字的4根线，baseLine为0，top为负值， bottom为正数
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (fontMetrics.top - fontMetrics.bottom) / 2;
        Point base = calcNewPoint(xA, yA, mRect.centerX(), mRect.centerY(), 50);
        canvas.drawText(builder.badge, base.x - getTextWidth() / 2, base.y - baseline, mTextPaint);
    }

    public int getTextWidth() {
        int w = 0;
        int len = builder.badge.length();
        float[] widths = new float[len];
        mTextPaint.getTextWidths(builder.badge, widths);
        for (int j = 0; j < len; j++) {
            w += (int) Math.ceil(widths[j]);
        }
        return w;
    }

    private Point calcNewPoint(int x, int y, int cx, int cy, float angle) {
        //角度算出弧度
        float hudu = (float) ((angle * Math.PI) / 180);
        float cosv = (float) Math.cos(hudu);
        float sinv = (float) Math.sin(hudu);
        // 三角函数计算
        float newX = ((x - cx) * cosv - (y - cy) * sinv + cx);
        float newY = ((x - cx) * sinv + (y - cy) * cosv + cy);
        return new Point((int) newX, (int) newY);
    }

    private void changPath() {
        extra = (int) (builder.circleRadius * 2 * factor / 5);

        switch (builder.expandDirect) {
            case AnimMenu.expandDirectTop:
                if (builder.isSwitchButton) {
                    mRect = new Rect(builder.circleRadius, builder.circleRadius / 2 + builder.dimens - extra,
                            builder.circleRadius * 2, builder.circleRadius * 3 / 2 + builder.dimens);
                } else {
                    mRect = new Rect(builder.circleRadius, builder.circleRadius / 2,
                            builder.circleRadius * 2, builder.circleRadius * 3 / 2 + extra);
                }
                break;
            case AnimMenu.expandDirectDown:
                if (builder.isSwitchButton) {
                    mRect = new Rect(builder.circleRadius, builder.circleRadius / 2, builder.circleRadius * 2,
                            builder.circleRadius * 3 / 2 + extra);
                } else {
                    mRect = new Rect(builder.circleRadius, builder.circleRadius / 2 + builder.dimens - extra,
                            builder.circleRadius * 2, builder.circleRadius * 3 / 2 + builder.dimens);
                }
                break;
            case AnimMenu.expandDirectLeft:
                if (builder.isSwitchButton) {
                    mRect = new Rect(builder.circleRadius / 2 + builder.dimens - extra, builder.circleRadius,
                            builder.circleRadius * 3 / 2 + builder.dimens, builder.circleRadius * 2);
                } else {
                    mRect = new Rect(builder.circleRadius / 2, builder.circleRadius, builder.circleRadius * 3 / 2 + extra,
                            builder.circleRadius * 2);
                }
                break;
            case AnimMenu.expandDirectRight:
                if (builder.isSwitchButton) {
                    mRect = new Rect(builder.circleRadius / 2, builder.circleRadius, builder.circleRadius * 3 / 2 + extra,
                            builder.circleRadius * 2);
                } else {
                    mRect = new Rect(builder.circleRadius / 2 + builder.dimens - extra, builder.circleRadius,
                            builder.circleRadius * 3 / 2 + builder.dimens, builder.circleRadius * 2);
                }
                break;
        }
        switch (builder.expandDirect) {
            case AnimMenu.expandDirectTop:
                xA = builder.circleRadius * 3 / 2;
                xB = builder.circleRadius * 5 / 2 - extra;
                xC = builder.circleRadius * 3 / 2;
                xD = builder.circleRadius / 2 + extra;
                yA = 0;
                yB = builder.circleRadius;
                yC = builder.circleRadius * 2 + extra;
                yD = builder.circleRadius;

                if (builder.isSwitchButton) {
                    yA += builder.dimens - extra;
                    yB += builder.dimens;
                    yC += builder.dimens - extra;
                    yD += builder.dimens;
                }
                break;
            case AnimMenu.expandDirectDown:
                xA = builder.circleRadius * 3 / 2;
                xB = builder.circleRadius * 5 / 2 - extra;
                xC = builder.circleRadius * 3 / 2;
                xD = builder.circleRadius / 2 + extra;
                yA = 0 - extra + builder.dimens;
                yB = builder.circleRadius + builder.dimens;
                yC = builder.circleRadius * 2 + builder.dimens;
                yD = builder.circleRadius + builder.dimens;

                if (builder.isSwitchButton) {
                    yA -= builder.dimens - extra;
                    yB -= builder.dimens;
                    yC -= builder.dimens - extra;
                    yD -= builder.dimens;
                }
                break;
            case AnimMenu.expandDirectLeft:
                xA = builder.circleRadius;
                xB = builder.circleRadius * 2 + extra;
                xC = builder.circleRadius;
                xD = 0;
                yA = builder.circleRadius / 2 + extra;
                yB = builder.circleRadius * 3 / 2;
                yC = builder.circleRadius * 5 / 2 - extra;
                yD = builder.circleRadius * 3 / 2;

                if (builder.isSwitchButton) {
                    xA += builder.dimens;
                    xB += builder.dimens - extra;
                    xC += builder.dimens;
                    xD += builder.dimens - extra;
                }
                break;
            case AnimMenu.expandDirectRight:
                xA = builder.circleRadius + builder.dimens;
                xB = builder.circleRadius * 2 + builder.dimens;
                xC = builder.circleRadius + builder.dimens;
                xD = 0 - extra + builder.dimens;
                yA = builder.circleRadius / 2 + extra;
                yB = builder.circleRadius * 3 / 2;
                yC = builder.circleRadius * 5 / 2 - extra;
                yD = builder.circleRadius * 3 / 2;

                if (builder.isSwitchButton) {
                    xA -= builder.dimens;
                    xB -= builder.dimens - extra;
                    xC -= builder.dimens;
                    xD -= builder.dimens - extra;
                }
                break;
        }
        mPath.reset();
        offSet = builder.circleRadius * 2 / 3.6f;
        mPath.moveTo(xA, yA);
        mPath.cubicTo(xA + offSet, yA, xB, yB - offSet, xB, yB);
        mPath.cubicTo(xB, yB + offSet, xC + offSet, yC, xC, yC);
        mPath.cubicTo(xC - offSet, yC, xD, yD + offSet, xD, yD);
        mPath.cubicTo(xD, yD - offSet, xA - offSet, yA, xA, yA);
    }

    private void initBackPaint() {
        if (isPress) {
            mPaint.setColor(builder.pressColor);
        } else {
            mPaint.setColor(builder.normalColor);
        }
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void initStrokePaint() {
        mPaint.setColor(builder.strokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(builder.strokeWidth);
    }


    public void startSpringAnimation(long time, float startValue, float endValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new FloatEvaluator(time, startValue, endValue), startValue, endValue);
        valueAnimator.addUpdateListener(this);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isOpen) {
                    AnimMenuItem parentEnd = (AnimMenuItem) ((AnimMenu) getParent()).getChildAt(((AnimMenu) getParent()).getChildCount() - 1);
                    if (parentEnd == AnimMenuItem.this && itemAnimListener != null) {
                        itemAnimListener.onAnimationEnd(isOpen);
                    }
                } else {
                    if (builder.isSwitchButton && itemAnimListener != null) {
                        itemAnimListener.onAnimationEnd(isOpen);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.start();
    }


    public void startFactorAnimation(final long time, final float startValue, final float endValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startValue, endValue);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startSpringAnimation(time * 5, endValue, startValue);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.addUpdateListener(this);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setDuration(time);
        valueAnimator.start();
    }


    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        factor = (float) animation.getAnimatedValue();
        invalidate();
    }

    // click listener

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPress = true;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:
                if (!isPress) {
                    return true;
                }
                invalidate();
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(builder.index, builder.tag);
                }
                if (builder.isAutoOpen) {
                    if (builder.isSwitchButton && !isOpen) {
                        ((AnimMenu) getParent()).openMenu();
                    } else if ((builder.isSwitchButton && isOpen) || !builder.isSwitchButton) {
                        ((AnimMenu) getParent()).closeMenu();
                    }
                }
                isPress = false;
                return true;
        }


        return super.onTouchEvent(event);
    }

    public void setItemClickListener(OnMenuItemClickListener itemClickListener) {
        if (itemClickListener == null) {
            return;
        }
        this.itemClickListener = itemClickListener;
    }

    public void setItemAnimListener(OnMenuItemClickListener.OnMenuItemAnimListener itemAnimListener) {
        if (itemAnimListener == null) {
            return;
        }
        this.itemAnimListener = itemAnimListener;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setBitmapIcon(@DrawableRes int menuIcon, @DrawableRes int menuOnIcon) {
        if (menuIcon == -1) {
            return;
        }
        builder.mBitmap = ((BitmapDrawable) getResources().getDrawable(menuIcon)).getBitmap();
        builder.mOnBitmap = ((BitmapDrawable) getResources().getDrawable(menuOnIcon)).getBitmap();
    }


    //Context context, int index, boolean isAutoTouch, String tag, int circleRadius, int dimens, int expandDirect, int iconId, int normalColor, int pressColor


    public String getItemTag() {
        return builder.tag;
    }

    public boolean isSwitchButton() {
        return builder.isSwitchButton;
    }

    public static final class Builder {
        private Context context;
        private String tag;
        //角标
        private String badge;
        private int index;
        private int circleRadius;
        private float strokeWidth;
        private int strokeColor = Color.RED;
        private boolean isDrawStroke;
        private int dimens;
        private int expandDirect;
        private Bitmap mBitmap;
        private Bitmap mOnBitmap;
        protected boolean isSwitchButton;
        private int normalColor;
        private int pressColor;
        private int iconId;
        private int iconOnId;
        //是否自动处理touch事件
        private boolean isAutoOpen = true;
        private int badgeColor;
        private float badgeSize;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder tag(String val) {
            tag = val;
            return this;
        }

        public Builder badge(String val) {
            badge = val;
            return this;
        }

        public Builder badgeColor(int val) {
            badgeColor = val;
            return this;
        }

        public Builder badgeSize(int val) {
            badgeSize = val;
            return this;
        }

        public Builder index(int val) {
            index = val;
            return this;
        }

        public Builder circleRadius(int val) {
            circleRadius = val;
            return this;
        }

        public Builder dimens(int val) {
            dimens = val;
            return this;
        }

        public Builder expandDirect(int val) {
            expandDirect = val;
            return this;
        }

        public Builder bitmap(Bitmap val) {
            mBitmap = val;
            return this;
        }

        public Builder onBitmap(Bitmap val) {
            mOnBitmap = val;
            return this;
        }

        public Builder isSwitchButton() {
            isSwitchButton = true;
            return this;
        }

        public Builder normalColor(int val) {
            normalColor = val;
            return this;
        }

        public Builder pressColor(int val) {
            pressColor = val;
            return this;
        }

        public Builder iconId(int val) {
            iconId = val;
            return this;
        }

        public Builder iconOnId(int val) {
            iconOnId = val;
            return this;
        }

        public Builder strokeWidth(float val) {
            strokeWidth = val;
            isDrawStroke = true;
            return this;
        }

        public Builder strokeColor(int val) {
            strokeColor = val;
            isDrawStroke = true;
            return this;
        }

        public AnimMenuItem build() {
            if (iconId > 0) {
                try {
                    mBitmap = ((BitmapDrawable) context.getResources().getDrawable(iconId)).getBitmap();
                } catch (Exception e) {
                    mBitmap = getBitmapFromVectorDrawable(context, iconId);
                }
            }
            if (isSwitchButton) {
                if (iconOnId > 0) {
                    try {
                        mOnBitmap = ((BitmapDrawable) context.getResources().getDrawable(iconOnId)).getBitmap();
                    } catch (Exception e) {
                        mOnBitmap = getBitmapFromVectorDrawable(context, iconOnId);
                    }
                } else {
                    mOnBitmap = mBitmap;
                }
            }
            if (tag == null) {
                tag = String.valueOf(index);
            }
            if (badgeSize <= 0) {
                badgeSize = circleRadius / 1.7f;
            }
            return new AnimMenuItem(this);
        }

        public Builder isAutoOpen(boolean val) {
            isAutoOpen = val;
            return this;
        }

        /**
         * vector to bitmap
         */
        private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = (DrawableCompat.wrap(drawable)).mutate();
            }

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }
    }
}
