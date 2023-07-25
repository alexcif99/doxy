package com.alexc.doxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserToAddPgAdapter extends RecyclerView.Adapter<UserToAddPgAdapter.ViewHolder>{

    private List<User> userList;

    // Constructor
    public UserToAddPgAdapter(List<User> userList) {
        this.userList = userList;
    }

    // ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTextView;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            checkBox = itemView.findViewById(R.id.checkBox);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    User user = userList.get(position);
                    user.setChecked(isChecked);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user_from_create_pg, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.checkBox.setChecked(user.isChecked());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
