package com.example.helloworld;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Map<String, String>> dataList;
    private Activity activity;
    private RecyclerView list_chat;

    public ChatAdapter(ArrayList<Map<String, String>> dataList, Activity activity, RecyclerView list_chat) {
        this.dataList = dataList;
        this.activity = activity;
        this.list_chat = list_chat;
    }

    public void addData(ArrayList<Map<String, String>> data) {
        dataList.addAll(dataList.size(), data);
        notifyItemRangeInserted(dataList.size(), data.size());
    }

    public void addData(Map<String, String> data) {
        dataList.add(0, data);
        notifyItemInserted(0);
        list_chat.scrollToPosition(0);
    }

    @Override
    public int getItemViewType(int position) {
        System.out.println("position=================="+position);
        return dataList.get(position).get("type")==null?0:Integer.parseInt(dataList.get(position).get("type"));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new MeTxtHolder(activity.getLayoutInflater().inflate(R.layout.item_chat_me_txt, parent, false));
        } else {
            return new OtTxtHolder(activity.getLayoutInflater().inflate(R.layout.item_chat_ot_txt, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String, String> item = dataList.get(position);
        if (holder instanceof MeTxtHolder) {
            ((MeTxtHolder) holder).view.setText(item.get("data"));
        }
        if (holder instanceof OtTxtHolder) {
            ((OtTxtHolder) holder).view.setText(item.get("data"));
        }
    }


    class MeTxtHolder extends RecyclerView.ViewHolder {
        private TextView view;

        MeTxtHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.text_chat_content_me);
        }
    }

    class OtTxtHolder extends RecyclerView.ViewHolder {
        private TextView view;

        OtTxtHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.text_chat_content_other);
        }
    }
}
