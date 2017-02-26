package org.androidtown.mylistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by kucis-03 on 2016-06-01.
 */
public class IconTextListAdapter extends BaseAdapter {
    private Context mContext;
    private List<IconTextItem> mItems = new ArrayList<IconTextItem>();
    private final static Comparator<IconTextItem> myComparator = new Comparator<IconTextItem>() {
        private final Collator collator = Collator.getInstance();
        @Override
        public int compare(IconTextItem lhs, IconTextItem rhs) {
            StringTokenizer st1 = new StringTokenizer(lhs.getData(1));
            StringTokenizer st2 = new StringTokenizer(rhs.getData(1));

//            return collator.compare(st1.nextToken(), st2.nextToken());
            return Integer.parseInt(st2.nextToken()) - Integer.parseInt(st1.nextToken());
        }
    };

    public IconTextListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(IconTextItem it) {
        mItems.add(it);
    }

    public void clearItems() {
        mItems.clear();
        mItems = null;
        mItems = new ArrayList<IconTextItem>();
    }

    public void setListItems(List<IconTextItem> list) {
        mItems = list;
    }

    public void sort() {
        for(int i = 0; i < mItems.size() && i < 3; i++) {
            mItems.get(i).setIcon(null);
        }

        Collections.sort(mItems, myComparator);
    }

    public void setImage(Drawable[] mIcon) {
        IconTextItem tmp;

        for(int i = 0; i < mItems.size() && i < 3; i++) {
            tmp = mItems.get(i);
            tmp.setIcon(mIcon[i]);
        }
        tmp = null;
    }

//    public boolean areAllItemsSelectable() {
//        return false;
//    }

    public boolean isSelectable(int position) {
        try {
            return mItems.get(position).isSelectable();
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IconTextView itemView;

        if(convertView == null) {
            itemView = new IconTextView(mContext, mItems.get(position));
        } else {
            itemView = (IconTextView) convertView;

            itemView.setIcon(mItems.get(position).getIcon());
            itemView.setText(0, mItems.get(position).getData(0));
            itemView.setText(1, mItems.get(position).getData(1));
        }

        return itemView;
    }
}
