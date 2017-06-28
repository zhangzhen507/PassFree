package com.zzhen.app.passfree.activity;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzhen on 2017/6/14.
 */

public abstract class CommonTypeAdapter<T> extends BaseAdapter {

    private List<T> mData;
    private int mLayoutRes;


    public CommonTypeAdapter(){
    }

    public CommonTypeAdapter(int layoutRes){
        this.mData = new ArrayList<>();
        this.mLayoutRes = layoutRes;
    }

    public CommonTypeAdapter(List<T> data, int layoutRes){
        this.mData = data;
        this.mLayoutRes = layoutRes;
    }

    @Override
    public int getCount(){
        return mData != null ? mData.size(): 0;
    }

    @Override
    public T getItem(int position){
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.bind(parent.getContext(), convertView, parent, mLayoutRes
                , position);
        bindView(holder, getItem(position));
        return holder.getItemView();
    }

    public abstract void bindView(ViewHolder holder, T obj);

    public void add(T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }

    public void add(int position, T data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(position, data);
        notifyDataSetChanged();
    }

    public void remove(T data) {
        if (mData != null) {
            mData.remove(data);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public boolean isDataExist(T data){

        int index = mData.indexOf(data);
        if(-1 == index){
            return false;
        }
        return true;
    }

    public static class ViewHolder {
        private SparseArray<View> mViews;
        private View item;
        private int position;
        private Context context;

        private ViewHolder(Context context, ViewGroup parent, int layoutRes){
            mViews = new SparseArray<>();
            this.context = context;
            View convertView = LayoutInflater.from(context).inflate(layoutRes, parent, false);
            convertView.setTag(this);
            item = convertView;
        }

        //Bind ViewHolder to item
        public static ViewHolder bind(Context context, View convertView, ViewGroup parent, int layoutRes, int position){
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder(context, parent, layoutRes);
            }else{
                holder = (ViewHolder)convertView.getTag();
                holder.item = convertView;
            }
            holder.position = position;
            return holder;
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int id){
            T t = (T) mViews.get(id);
            if(t == null){
                t = (T)item.findViewById(id);
                mViews.put(id, t);
            }
            return t;
        }

        public View getItemView() {
            return item;
        }

        public int getItemPosition(){
            return position;
        }

        public ViewHolder setText(int id, CharSequence text) {
            View view = getView(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public ViewHolder setImageResource(int id, int drawableRes) {
            View view = getView(id);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(drawableRes);
            } else {
                view.setBackgroundResource(drawableRes);
            }
            return this;
        }

        public ViewHolder setOnClickListener(int id, View.OnClickListener listener) {
            getView(id).setOnClickListener(listener);
            return this;
        }

        public ViewHolder setVisibility(int id, int visible) {
            getView(id).setVisibility(visible);
            return this;
        }

        public ViewHolder setTag(int id, Object obj) {
            getView(id).setTag(obj);
            return this;
        }
    }
}
