package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_USER_ID;
import static com.alexc.doxy.DatabaseHelper.USER_EMAIL;
import static com.alexc.doxy.DatabaseHelper.USER_ID;
import static com.alexc.doxy.DatabaseHelper.USER_NAME;
import static com.alexc.doxy.DatabaseHelper.USER_SURNAME;
import static com.alexc.doxy.DatabaseHelper.USER_USERNAME;

import static java.sql.DriverManager.println;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CreatePaymentFragment extends Fragment {

    private EditText editTextTitlePayment;
    private EditText editTextDescriptionPayment;
    private EditText editTextAmountPayment;
    private Button buttonCreatePayment;
    private RecyclerView recyclerview;
    private UserToAddPgAdapter adapter;
    private Integer currentUserId;

    private DatabaseHelper databaseHelper;

    public CreatePaymentFragment() {
    }

    public static CreatePaymentFragment newInstance(int paymentGroupId) {
        CreatePaymentFragment fragment = new CreatePaymentFragment();
        Bundle args = new Bundle();
        args.putInt("paymentGroupId", paymentGroupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_payment, container, false);

        editTextTitlePayment = view.findViewById(R.id.editTextTitlePayment);
        editTextDescriptionPayment = view.findViewById(R.id.editTextDescriptionPayment);
        editTextAmountPayment = view.findViewById(R.id.editTextAmountPayment);
        buttonCreatePayment = view.findViewById(R.id.buttonCreatePayment);

//        paymentGroupTableHelper = new PaymentGroupTableHelper(getActivity());

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this.getActivity());

        //Obtener la ID del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        currentUserId = Integer.parseInt(string_user_id);

        // Posem els usuaris al recyclerview
        Bundle bundle = getArguments();
        Integer pg_id = bundle.getInt("paymentGroupId");

        Cursor cursor = databaseHelper.getUsersFromPG(pg_id, currentUserId);
        ArrayList<User> users  = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(REL_USER_PG_USER_ID));

                User user = databaseHelper.getUser(id);
                users.add(user);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerview = view.findViewById(R.id.recyclerViewUsersToAddP);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserToAddPgAdapter(users);
        recyclerview.setAdapter(adapter);

        // Una vegada emplenat en recyclerview, tornem a posar el currentUserId, per que a efectes contables si que volemque hi sigui
        User currentUser = databaseHelper.getUser(currentUserId);
        users.add(currentUser);

        buttonCreatePayment.setOnClickListener(new View.OnClickListener() {
            // todo: validate input (aqui i a tots els inputs)
            @Override
            public void onClick(View v) {
                String title = editTextTitlePayment.getText().toString().trim();
                String description = editTextDescriptionPayment.getText().toString().trim();
                String amount = editTextAmountPayment.getText().toString().trim();
                Double amount_double = Double.parseDouble(amount);

                if (validateInput(title, description, amount)) {
                    long payment_id = databaseHelper.addPayment(title, description, amount_double, Boolean.FALSE, currentUserId, pg_id);
                    // todo: de moment dividim la quantitat total entre el nombre de participants, més endevant potser es fa que es pugui fixar un preu
                    Double amount_rel_user_p = amount_double/users.size();
                    for(User user:users){
                        databaseHelper.addRelUserP(user.getId(), payment_id, amount_rel_user_p, Boolean.FALSE);
                    }

                    Toast.makeText(getActivity(), "Grupo de pago creado exitosamente", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getActivity(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validateInput(String title, String description, String amount) {
        return !title.isEmpty() && !description.isEmpty() && !amount.isEmpty();
    }

    private void clearFields() {
        editTextTitlePayment.setText("");
        editTextDescriptionPayment.setText("");
        editTextAmountPayment.setText("");
    }
}
