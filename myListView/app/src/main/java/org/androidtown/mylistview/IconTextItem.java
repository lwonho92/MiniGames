package org.androidtown.mylistview;

import android.graphics.drawable.Drawable;

/**
 * Created by kucis-03 on 2016-06-01.
 */
public class IconTextItem {
    public Drawable mIcon;                     //  Icon
    public String[] mData = new String[2];    //  Data array
    private boolean mSelectable = true;      //  True if this item is selectable

//    Initialize with icon and data array   @param icon, @param obj
    public IconTextItem(Drawable icon, String[] obj) {
        mIcon = icon;
        mData = obj;
    }
    //    Initialize with icon and data array   @param icon, @param obj1, @param obj2, @param obj3
    public IconTextItem(Drawable icon, String obj01, String obj02) {
        mIcon = icon;

        mData[0] = obj01;
        mData[1] = obj02;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }
    public Drawable getIcon() {
        return mIcon;
    }

    public void setData(String[] mData) {
        this.mData = mData;
    }
    public String[] getData() {
        return mData;
    }
    public String getData(int index) {
        if(mData == null || index >= mData.length || index < 0) {
            return null;
        }
        return mData[index];
    }

    public boolean isSelectable() {
        return mSelectable;
    }
    public void setSelectable(boolean mSelectable) {
        this.mSelectable = mSelectable;
    }


}
