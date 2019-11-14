package com.example.helloworld.service;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.helloworld.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<String> list;

    public ListViewAdapter(Activity activity, ArrayList<String> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.chat, viewGroup, false);
        }
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(list.get(i));
        textView.setBackgroundColor(Color.BLUE);
        textView.setGravity(Gravity.RIGHT);
        return view;
    }

    public void addData(String dataStr){
        list.add(dataStr);
        notifyDataSetChanged();
    }
}
