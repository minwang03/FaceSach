package com.example.facesach.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;

    public UserAdapter(List<User> users) {
        this.userList = users;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserEmail;
        ImageView imgUserAvatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtUserName.setText(user.getName());
        holder.txtUserEmail.setText(user.getEmail());

        String avatarUrl = user.getAvatar();
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            holder.imgUserAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(holder.imgUserAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
