package com.example.facesach.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facesach.R;
import com.example.facesach.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvComment, tvDate;
        ImageView ivAvatar;
        RatingBar ratingBarComment;


        public CommentViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ratingBarComment = itemView.findViewById(R.id.ratingBarComment);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvUserName.setText(comment.getUser_name());
        holder.tvComment.setText(comment.getComment());
        holder.tvDate.setText(comment.getCreated_at());
        holder.ratingBarComment.setRating(comment.getRating());
        Glide.with(holder.itemView.getContext()).load(comment.getUser_avatar()).placeholder(R.drawable.ic_avatar_placeholder).into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
