package com.ashlikun.animmenu;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by lilei on 2016/11/6.
 */

public class AnimMenu extends ViewGroup {
    public static final String AM_ROOT_TAG = "am_root";
    public static final int expandDirectTop = 0;        // top
    public static final int expandDirectDown = 1;        // down
    public static final int expandDirectLeft = 2;        // left
    public static final int expandDirectRight = 3;        // right

    private Context context;
    private int expandDirect = 0;
    private int circleRadius;
    private int dimens;
    private int badgeSize;
    private int badgeColor;
    private long duration;
    private int normalColor;
    private int pressColor;
    private int menuIcon;
    private int menuOnIcon;
    private boolean autoOpen = true;

    private int childViewCount;             // button count
    private boolean isOpen = false;

    private OnMenuItemClickListener itemClickListener;


    public AnimMenu(Context context) {
        this(context, null);
    }

    public AnimMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < childViewCount; i++) {
            AnimMenuItem items = (AnimMenuItem) getChildAt(i);

            switch (expandDirect) {

                case expandDirectTop:
                    l = 0;
                    t = (circleRadius * 2 + dimens) * (childViewCount - 1) + circleRadius * 2;
                    if (isRootItem(items)) {
                        t -= dimens;
                    }
                    r = circleRadius * 3;
                    b = t + circleRadius * 2 + dimens;
                    break;

                case expandDirectDown:
                    l = 0;
                    t = dimens * (-1);
                    if (isRootItem(items)) {
                        t += dimens;
                    }
                    r = circleRadius * 3;
                    b = t + circleRadius * 2 + dimens;
                    break;

                case expandDirectLeft:
                    l = (circleRadius * 2 + dimens) * (childViewCount - 1) + circleRadius * 2;
                    if (isRootItem(items)) {
                        l -= dimens;
                    }
                    t = 0;
                    r = l + circleRadius * 2 + dimens;
                    b = circleRadius * 3;
                    break;

                case expandDirectRight:
                    l = dimens * (-1);
                    if (isRootItem(items)) {
                        l += dimens;
                    }
                    t = 0;
                    r = l + circleRadius * 2 + dimens;
                    b = circleRadius * 3;
                    break;
            }
            items.layout(l, t, r, b);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        if (expandDirect == expandDirectTop || expandDirect == expandDirectDown) {
            width = circleRadius * 3;
            height = (childViewCount - 1) * (circleRadius * 2 + dimens) + circleRadius * 2 * 2;
            if (childViewCount > 0 && !((AnimMenuItem) getChildAt(0)).isSwitchButton()) {
                height += circleRadius / 4;
            }
        } else {
            width = (childViewCount - 1) * (circleRadius * 2 + dimens) + circleRadius * 2 * 2;
            height = circleRadius * 3;
            if (childViewCount > 0 && !((AnimMenuItem) getChildAt(0)).isSwitchButton()) {
                width += circleRadius / 4;
            }
        }
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        //    setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0), resolveSizeAndState(height, heightMeasureSpec, 0));
        setMeasuredDimension(width, height);
    }

    private void init(Context context, AttributeSet attrs) {

        // set xml property
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimMenu);
        circleRadius = (int) typedArray.getDimension(R.styleable.AnimMenu_am_circleRadius, dip2px(20));
        dimens = (int) typedArray.getDimension(R.styleable.AnimMenu_am_dimens, dip2px(10));
        badgeSize = (int) typedArray.getDimension(R.styleable.AnimMenu_am_badgeSize, 0);
        badgeColor = typedArray.getColor(R.styleable.AnimMenu_am_badgeColor, 0xffff6d42);

        duration = typedArray.getInteger(R.styleable.AnimMenu_am_animDuration, 1600);
        expandDirect = typedArray.getInteger(R.styleable.AnimMenu_am_expandDirect, 0);
        normalColor = typedArray.getColor(R.styleable.AnimMenu_am_buttonNormalColor, Color.WHITE);
        pressColor = typedArray.getColor(R.styleable.AnimMenu_am_buttonPressColor, Color.WHITE);
        menuIcon = typedArray.getResourceId(R.styleable.AnimMenu_am_icon, -1);
        menuOnIcon = typedArray.getResourceId(R.styleable.AnimMenu_am_onIcon, menuIcon);
        autoOpen = typedArray.getBoolean(R.styleable.AnimMenu_am_autoOpen, autoOpen);
        childViewCount = 0;
        // add root button
        if (menuIcon != -1) {
            addView(getDefaultItem()
                    .tag(AM_ROOT_TAG)
                    .isAutoOpen(autoOpen)
                    .iconId(menuIcon)
                    .iconOnId(menuOnIcon)
                    .iconOnId(menuOnIcon)
                    .isSwitchButton());
        }
    }


    /**
     * buttonItem open animation
     */
    private void buttonItemOpenAnimation(int index, final AnimMenuItem view) {
        if (isRootItem(view)) {
            view.startFactorAnimation(duration / 6, 0, 1);
        } else {
            ViewPropertyAnimator propertyAnimator = view.animate().alpha(1).
                    setInterpolator(new OvershootInterpolator()).setDuration(duration / 3);

            switch (expandDirect) {
                case expandDirectTop:
                    propertyAnimator.y((circleRadius * 2 + dimens) * (childViewCount - 1 - index) + circleRadius * 2);
                    break;
                case expandDirectDown:
                    propertyAnimator.y(circleRadius * 2 * index + dimens * (index - 1));
                    break;
                case expandDirectLeft:
                    propertyAnimator.x((circleRadius * 2 + dimens) * (childViewCount - 1 - index) + circleRadius * 2);
                    break;
                case expandDirectRight:
                    propertyAnimator.x(circleRadius * 2 * index + dimens * (index - 1));
                    break;
            }
            if (isOpen) {
                view.setVisibility(View.VISIBLE);
            }
            propertyAnimator.start();

            // start factor animation
            view.startFactorAnimation(duration / 6, 0, -1);
        }
        view.setOpen(isOpen);
    }

    /**
     * buttonItem close animation
     */
    private void buttonItemCloseAnimation(final AnimMenuItem view) {
        if (isRootItem(view)) {
            view.startFactorAnimation(duration / 6, 0, -1);
        } else {
            ViewPropertyAnimator propertyAnimator = view.animate().alpha(0).setDuration(duration / 3);

            switch (expandDirect) {
                case expandDirectTop:
                    propertyAnimator.y((circleRadius * 2 + dimens) * (childViewCount - 1) + circleRadius * 2);
                    break;
                case expandDirectDown:
                    propertyAnimator.y(dimens * (-1));
                    break;
                case expandDirectLeft:
                    propertyAnimator.x((circleRadius * 2 + dimens) * (childViewCount - 1) + circleRadius * 2);
                    break;
                case expandDirectRight:
                    propertyAnimator.x(dimens * (-1));
                    break;
            }

            propertyAnimator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isOpen) {
                        view.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            propertyAnimator.start();
        }
        view.setOpen(isOpen);
    }


    /*********************** public *********************/
    /****************************************************/
    public boolean isRootItem(AnimMenuItem item) {
        return item.getItemTag().equals(AM_ROOT_TAG) || item.isSwitchButton();
    }

    /**
     * open menu
     */
    public void openMenu() {
        if (!isOpen) {
            isOpen = true;
            for (int i = 0; i < childViewCount; i++) {
                buttonItemOpenAnimation(i, (AnimMenuItem) getChildAt(i));
            }
        }
    }

    public void openMenuNoCheckOpen() {
        isOpen = true;
        for (int i = 0; i < childViewCount; i++) {
            buttonItemOpenAnimation(i, (AnimMenuItem) getChildAt(i));
        }
    }

    /**
     * close menu
     */
    public void closeMenu() {
        if (isOpen) {
            isOpen = false;
            for (int i = 0; i < childViewCount; i++) {
                buttonItemCloseAnimation((AnimMenuItem) getChildAt(i));
            }
        }
    }

    public void closeMenuNoCheckOpen() {
        isOpen = false;
        for (int i = 0; i < childViewCount; i++) {
            buttonItemCloseAnimation((AnimMenuItem) getChildAt(i));
        }
    }


    /**
     * add item button
     *
     * @param drawableIcon button icon drawable
     */
    public void addView(int drawableIcon) {
        addView(getDefaultItem()
                .iconId(drawableIcon));
    }

    public void addView(int drawableIcon, String tag) {
        addView(getDefaultItem()
                .iconId(drawableIcon)
                .tag(tag));
    }

    public void addView(int drawableIcon, int normalColor, int pressColor) {
        addView(getDefaultItem()
                .iconId(drawableIcon)
                .normalColor(normalColor)
                .pressColor(pressColor));
    }

    public void addView(AnimMenuItem.Builder builder) {
        if (builder.isSwitchButton) {
            addView(builder.build(), 0);
        } else {
            addView(builder.build());
        }

        childViewCount++;
    }

    public AnimMenuItem.Builder getDefaultItem() {
        return new AnimMenuItem.Builder(context)
                .index(childViewCount)
                .circleRadius(circleRadius)
                .dimens(dimens)
                .isAutoOpen(autoOpen)
                .expandDirect(expandDirect)
                .normalColor(normalColor)
                .pressColor(pressColor)
                .badgeColor(badgeColor)
                .badgeSize(badgeSize);
    }

    /**
     * check the menu is or not open
     *
     * @return isOpen
     */
    public boolean isOpen() {
        return isOpen;
    }


    /**
     * set item click listener
     */
    public void setItemClickListener(OnMenuItemClickListener itemClickListener) {
        if (itemClickListener == null) {
            return;
        }
        this.itemClickListener = itemClickListener;
        for (int i = 0; i < childViewCount; i++) {
            AnimMenuItem view = (AnimMenuItem) getChildAt(i);
            view.setItemClickListener(itemClickListener);
        }
    }

    public void setItemAnimListener(OnMenuItemClickListener.OnMenuItemAnimListener itemAnimListener) {
        if (itemAnimListener == null) {
            return;
        }
        for (int i = 0; i < childViewCount; i++) {
            AnimMenuItem view = (AnimMenuItem) getChildAt(i);
            view.setItemAnimListener(itemAnimListener);
        }
    }

    public int dip2px(int dip) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
