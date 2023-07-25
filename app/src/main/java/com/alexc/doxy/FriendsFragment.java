package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.FRIEND_ID;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_AMOUNT;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Configurar el ViewPager y el TabLayout
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        FriendsPagerAdapter adapter = new FriendsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FriendsListFragment(), "Amigos");
        adapter.addFragment(new SearchFriendsFragment(), "Buscar amigos");
        viewPager.setAdapter(adapter);
    }
}



















    // Otras constantes y métodos
//
//    private RecyclerView recyclerViewFriends;
//    private UserSimpleAdapter userSimpleAdapter;
//
//    private DatabaseHelper databaseHelper;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_friends, container, false);
//
//        // Obtener el ID del usuario actual desde las preferencias compartidas
//        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
//        String string_user_id = sharedPreferences.getString("userId", "");
//        Integer user_id = Integer.parseInt(string_user_id);
//
//        // Obtener el cursor con la lista de amigos del usuario actual desde el DatabaseHelper
//        databaseHelper = new DatabaseHelper(this.getActivity());
//        Cursor cursor = databaseHelper.getFriends(user_id);
//
//        List<User> friendsList = new ArrayList<>();
//
//        // Verificar que el cursor no es nulo y tiene al menos un registro
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                @SuppressLint("Range") int friendId = cursor.getInt(cursor.getColumnIndex(FRIEND_ID));
//
//                // Crea un objeto User con los datos obtenidos y agrégalo a la lista
//                User friend = databaseHelper.getUser(friendId);
//                friendsList.add(friend);
//            } while (cursor.moveToNext());
//
//            cursor.close();
//        }
//
//        // Configurar el RecyclerView de amigos
//        recyclerViewFriends = view.findViewById(R.id.recyclerViewFriends);
//        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
//        userSimpleAdapter = new UserSimpleAdapter(friendsList);
//        recyclerViewFriends.setAdapter(userSimpleAdapter);
//
//        return view;
//    }
//}
