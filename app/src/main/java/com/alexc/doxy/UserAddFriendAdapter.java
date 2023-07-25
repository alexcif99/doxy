package com.alexc.doxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAddFriendAdapter extends RecyclerView.Adapter<UserAddFriendAdapter.ViewHolder> {

    private List<User> relUserPList;
    private OnUserClickListener  itemClickListener;

    public interface OnUserClickListener  {
        void onUserClick(User user);
    }

    public UserAddFriendAdapter(List<User> relUserPList, OnUserClickListener itemClickListener) {
        this.relUserPList = relUserPList;
        this.itemClickListener = itemClickListener;
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
                        itemClickListener.onUserClick(relUserPList.get(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_simple_user, parent, false);
        return new UserAddFriendAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAddFriendAdapter.ViewHolder holder, int position) {
        User user = relUserPList.get(position);
        holder.usernameSimpleUserTextView.setText(user.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onUserClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return relUserPList.size();
    }
}

