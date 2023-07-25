package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.FRIEND_ID;
import static com.alexc.doxy.DatabaseHelper.USER_EMAIL;
import static com.alexc.doxy.DatabaseHelper.USER_ID;
import static com.alexc.doxy.DatabaseHelper.USER_NAME;
import static com.alexc.doxy.DatabaseHelper.USER_SURNAME;
import static com.alexc.doxy.DatabaseHelper.USER_USERNAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import static java.sql.DriverManager.println;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreatePaymentGroupFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonCreateGroup;
    private RecyclerView recyclerview;
    private UserToAddPgAdapter adapter;

    private DatabaseHelper databaseHelper;

    public CreatePaymentGroupFragment() {
        // Constructor vacío requerido
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_payment_group, container, false);

        editTextTitle = rootView.findViewById(R.id.editTextTitle);
        editTextDescription = rootView.findViewById(R.id.editTextDescription);
        buttonCreateGroup = rootView.findViewById(R.id.buttonCreatePaymentGroup);

//        paymentGroupTableHelper = new PaymentGroupTableHelper(getActivity());

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this.getActivity());

        // Posem els usuaris al recyclerview
        Cursor cursor = databaseHelper.getFriends(Integer.parseInt(getUserIdFromSharedPreferences()));
        ArrayList<User> users  = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(FRIEND_ID));
//                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(USER_NAME));
//                @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex(USER_SURNAME));
//                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(USER_USERNAME));
//                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(USER_EMAIL));

                User user = databaseHelper.getUser(id);
                users.add(user);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerview = rootView.findViewById(R.id.recyclerViewUsersToAddPg);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserToAddPgAdapter(users);
        recyclerview.setAdapter(adapter);

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                ArrayList<User> checked_users = get_checked_users(users);

                Log.d("n users: ", Integer.toString(checked_users.size()));

                // Afegim l'usuari loguejat ja que si crea el grup, hi haura de ser
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
                String string_user_id = sharedPreferences.getString("userId", "");
                Integer user_id = Integer.parseInt(string_user_id);
                User loged_user = databaseHelper.getUser(user_id);
                checked_users.add(loged_user);

                // todo: el mateix a create payment fragment, i crear la taula relacional etc

                if (validateInput(title, description)) {
                    Long pg_id = databaseHelper.addPaymentGroup(title, description);
                    println("Payment grup creado: " + Long.toString(pg_id));
                    for(User checked_user : checked_users){
                        databaseHelper.addRelUserPg(pg_id, checked_user.getId(), 0.0);
                        checked_user.setChecked(Boolean.FALSE);
                    }
                    Toast.makeText(getActivity(), "Grupo de pago creado exitosamente", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getActivity(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private boolean validateInput(String title, String description) {
        return !title.isEmpty() && !description.isEmpty();
    }

    private void clearFields() {
        editTextTitle.setText("");
        editTextDescription.setText("");
    }

    private ArrayList<User> get_checked_users(ArrayList<User> users) {
        ArrayList<User> checked_users = new ArrayList<>();
        for(User user : users){
            if (user.isChecked()) {
                checked_users.add(user);
            }
        }
        return checked_users;
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String stringUserId = sharedPreferences.getString("userId", "");
        return stringUserId;
    }
}
