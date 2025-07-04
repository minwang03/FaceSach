package com.example.facesach.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.facesach.R;
import com.example.facesach.model.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    List<Message> messageList;
    int currentUserId;

    public MessageAdapter(List<Message> messageList, int currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messageList.get(position);
        return msg.getSender_id() == currentUserId ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messageList.get(position);
        String messageContent = (msg.getMessage() != null && !msg.getMessage().isEmpty()) ? msg.getMessage() : "(Không có nội dung)";

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).txtMessage.setText(messageContent);
        } else {
            String senderName = msg.getName() != null ? msg.getName() : "Người gửi";
            ((ReceivedMessageViewHolder) holder).txtMessage.setText(messageContent);
            ((ReceivedMessageViewHolder) holder).txtSender.setText(senderName);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtSender;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtSender = itemView.findViewById(R.id.txtSender);
        }
    }
}

