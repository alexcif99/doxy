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

