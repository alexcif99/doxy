package com.alexc.doxy;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    HomeFragment homeFragment = new HomeFragment();
    BalanceFragment balanceFragment = new BalanceFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    CreatePaymentGroupFragment createPaymentGroupFragment = new CreatePaymentGroupFragment();
    Fragment active = homeFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

