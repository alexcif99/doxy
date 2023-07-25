package com.alexc.doxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private DatabaseHelper databaseHelper;

    HomeFragment homeFragment = new HomeFragment();
    BalanceFragment balanceFragment = new BalanceFragment();
    FriendsFragment friendsFragment = new FriendsFragment();
    CreatePaymentGroupFragment createPaymentGroupFragment = new CreatePaymentGroupFragment();
    Fragment active = homeFragment;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        User u = databaseHelper.getUser(Integer.parseInt(userId));

        ProfileFragment profileFragment = ProfileFragment.newInstance(u.getId(), u.getNombre(), u.getApellido(), u.getUsername(), u.getEmail());

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
                        return true;
                    case R.id.nav_balance:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, balanceFragment).commit();
                        return true;
                    case R.id.nav_create_group:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, createPaymentGroupFragment).commit();
                        return true;
                    case R.id.nav_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, profileFragment).commit();
                        return true;
                    case R.id.nav_friends:
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, friendsFragment).commit();
                        return true;
                }
                return false;
            }
        });

    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.nav_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
//                return true;
//            case R.id.nav_balance:
//                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, balanceFragment).commit();
//                return true;
//            case R.id.nav_create_group:
//                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, createPaymentGroupFragment).commit();
//                return true;
//            case R.id.nav_profile:
//                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, profileFragment).commit();
//                return true;
//        }
//        return false;
//    }
}

