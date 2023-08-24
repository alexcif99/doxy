package com.alexc.doxy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
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
    private DatabaseHelper databaseHelper;

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
        private ImageView profileImageSimpleUserImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameSimpleUserTextView = itemView.findViewById(R.id.usernameSimpleUserTextView);
            profileImageSimpleUserImageView = itemView.findViewById(R.id.profileImageSimpleUserImageView);

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
        holder.profileImageSimpleUserImageView.setImageBitmap(user.getRoundedImageProfile());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onFriendClick(user);
                }
            }
        });
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Bitmap roundedBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);

        return roundedBitmap;
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

