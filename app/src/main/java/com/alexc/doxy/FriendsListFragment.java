package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.FRIEND_FRIEND_ID;
import static com.alexc.doxy.DatabaseHelper.FRIEND_ID;
import static com.alexc.doxy.DatabaseHelper.USER_ID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendsListFragment extends Fragment implements UserListFriendsAdapter.OnUserClickListener {

    private RecyclerView recyclerViewFriends;
    private UserListFriendsAdapter listFriendsAdapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<User> friendsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());
        int userId = Integer.parseInt(getUserIdFromSharedPreferences());

        Cursor cursor = databaseHelper.getFriends(userId);
        friendsList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Integer id = cursor.getInt(cursor.getColumnIndex(FRIEND_FRIEND_ID));
                User friend = databaseHelper.getUser(id);
                friendsList.add(friend);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView de amigos
        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        listFriendsAdapter = new UserListFriendsAdapter(friendsList, this);
        recyclerViewFriends.setAdapter(listFriendsAdapter);

        return view;
    }

    private void showRemoveFriendDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar amigo");
        builder.setMessage("¿Estás seguro que deseas eliminar a " + user.getUsername() + " de tu lista de amigos?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeFriend(user);
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }

    private void removeFriend(User user) {
        int currentUserId = Integer.parseInt(getUserIdFromSharedPreferences());
        int friendId = user.getId();
        databaseHelper.removeFriend(currentUserId, friendId);

        // Obtener nuevamente la lista de amigos del usuario actual
        Cursor cursor = databaseHelper.getFriends(currentUserId);
        ArrayList<User> friends = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(FRIEND_ID));
                User friend = databaseHelper.getUser(id);
                friends.add(friend);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Actualizar la lista de amigos y notificar al adapter
        listFriendsAdapter.setUserList(friends);
    }

    @Override
    public void onFriendClick(User user) {
        showRemoveFriendDialog(user);
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String stringUserId = sharedPreferences.getString("userId", "");
        return stringUserId;
    }

    public void addFriend(User user) {
        friendsList.add(user);
        listFriendsAdapter.notifyDataSetChanged();
    }
}
