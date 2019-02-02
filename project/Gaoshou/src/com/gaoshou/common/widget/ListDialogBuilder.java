package com.gaoshou.common.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.gaoshou.android.R;
import com.gaoshou.common.component.BasicDialog;
import com.gaoshou.common.component.BasicDialog.Builder;

/**
 * 使用方法: 1.创建Builder 对象，传入数组以及点击事件(默认Adapter 为 ArrayAdapter<String>)
 * 2.调用create方法创建Dialog
 */
public class ListDialogBuilder<T> extends BasicDialog.Builder {
    private List<T> listData;
    private ListAdapter adapter;
    private OnItemClickListener mOnItemClickListener;

    public ListDialogBuilder(Context context, List<T> listData, OnItemClickListener mOnItemClickListener) {
        this(context, listData, mOnItemClickListener, null);
    }

    public ListDialogBuilder(Context context, List<T> listData, OnItemClickListener mOnItemClickListener, String title) {
        super(context);
        this.listData = listData;
        this.mOnItemClickListener = mOnItemClickListener;
        this.title = title;
    }

    public List<T> getListData() {
        return listData;
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    public ListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
    }

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public BasicDialog create() {
        init();
        super.create();
        return dialog;
    }

    private void init() {
        contentView = (ListView) LayoutInflater.from(context).inflate(R.layout.list_dialog_value, null);
        setContentView(contentView);

        if (adapter == null) {
            adapter = new ArrayAdapter<T>(context, R.layout.item_experty_or_sort_list, listData);
        }

        if (contentView instanceof ListView) {
            ((ListView) contentView).setAdapter(adapter);
            if (mOnItemClickListener != null) {
                ((ListView) contentView).setOnItemClickListener(mOnItemClickListener);
            }
        }

    }
}
