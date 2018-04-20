package com.ashlikun.animmenu;

/**
 * Created by lilei on 2016/11/10.
 */

public interface OnMenuItemClickListener {

    public void onItemClick(int index, String tag);

    public interface OnMenuItemAnimListener {
        public void onAnimationEnd(boolean isOpen);
    }

}
