package com.alexc.doxy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserListFriendsAdapter extends RecyclerView.Adapter<UserListFriendsAdapter.ViewHolder> {

    private List<User> userList;
    private OnUserClickListener itemClickListener;

    public interface OnUserClickListener  {
        void onFriendClick(User user);
    }

    public UserListFriendsAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.itemClickListener = listener;
    }

    public void setOnItemClickListener(OnUserClickListener listener) {
        this.itemClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameSimpleUserTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameSimpleUserTextView = itemView.findViewById(R.id.usernameSimpleUserTextView);

            usernameSimpleUserTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                        itemClickListener.onFriendClick(userList.get(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_simple_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListFriendsAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameSimpleUserTextView.setText(user.getUsername());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onFriendClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

}

