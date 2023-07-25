package com.alexc.doxy;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView textViewUsername;
    private TextView textViewEmail;

    private DatabaseHelper databaseHelper;

    public ProfileFragment() {
        // Constructor vacío requerido
    }

    public static ProfileFragment newInstance(Integer userId, String name, String surname, String username, String email) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putInt("userId", userId);
        args.putString("name", name);
        args.putString("surname", surname);
        args.putString("username", username);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewUsername = view.findViewById(R.id.profile_name);
        textViewEmail = view.findViewById(R.id.profile_email);

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this.getActivity());

//        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
//        String user_id = sharedPreferences.getString("userId", "");
//        System.out.println("USER ID: " + user_id);

        Integer userId = getArguments().getInt("userId");
        String name = getArguments().getString("name");
        String surname = getArguments().getString("surname");
        String username = getArguments().getString("username");
        String email = getArguments().getString("email");

        // Mostrar los datos en los TextViews
        textViewUsername.setText(username);
        textViewEmail.setText(email);

        return view;
    }
}
