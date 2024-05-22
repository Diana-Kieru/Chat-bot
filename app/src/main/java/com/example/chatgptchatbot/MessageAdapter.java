package com.example.chatgptchatbot;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new MyViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Reset visibility for all views
        holder.leftChatView.setVisibility(View.GONE);
        holder.rightChatView.setVisibility(View.GONE);
        holder.leftTextView.setVisibility(View.GONE);
        holder.rightTextView.setVisibility(View.GONE);
        holder.leftImageView.setVisibility(View.GONE);
        holder.rightImageView.setVisibility(View.GONE);

        // Configure the views based on the sender
        if (message.getSentBy().equals(Message.SENT_BY_ME)) {
            holder.rightChatView.setVisibility(View.VISIBLE);
            if (message.getMessage() != null && !message.getMessage().isEmpty()) {
                holder.rightTextView.setVisibility(View.VISIBLE);
                holder.rightTextView.setText(message.getMessage());
            }
            if (message.getImage() != null) {
                holder.rightImageView.setVisibility(View.VISIBLE);
                holder.rightImageView.setImageBitmap(message.getImage());
            }
        } else {
            holder.leftChatView.setVisibility(View.VISIBLE);
            if (message.getMessage() != null && !message.getMessage().isEmpty()) {
                holder.leftTextView.setVisibility(View.VISIBLE);
                holder.leftTextView.setText(message.getMessage());
            }
            if (message.getImage() != null) {
                holder.leftImageView.setVisibility(View.VISIBLE);
                holder.leftImageView.setImageBitmap(message.getImage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftTextView, rightTextView;
        ImageView leftImageView, rightImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
            leftImageView = itemView.findViewById(R.id.left_chat_image_view);
            rightImageView = itemView.findViewById(R.id.right_chat_image_view);
        }
    }
}
