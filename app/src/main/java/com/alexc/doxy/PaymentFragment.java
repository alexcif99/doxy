package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.PAYMENT_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_DESCRIPTION;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_ID;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_TITLE;
import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_USER_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_PAYMENT_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_USER_ID;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentFragment extends Fragment {
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewAmount;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerview;
    private UserDebtorAdapter adapter;
    private Integer paymentId;

    public PaymentFragment() {
        // Constructor vacío requerido
    }

    public static PaymentFragment newInstance(int paymentId, String title, String description, Double amount) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putInt("paymentId", paymentId);
        args.putString("title", title);
        args.putString("description", description);
        args.putDouble("amount", amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paymentId = getArguments().getInt("paymentId");
        }
    }


    // todo: es podria fer que si l'usuari actiu és el mateix que el owner del payment, el pugui editar
    // todo: nomes e sposar un if i un else suposo

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        textViewTitle = view.findViewById(R.id.payment_title);
        textViewDescription = view.findViewById(R.id.payment_description);
        textViewAmount = view.findViewById(R.id.payment_amount);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String description = args.getString("description");
            double amount = args.getDouble("amount");

//            todo: obtenir els dos ultlims caractes del string i si son ".0" eliminalos del string
//            if Double.toString(amount)

            textViewTitle.setText(title);
            textViewDescription.setText(description);
            textViewAmount.setText(Double.toString(amount) + " €");
        }

        databaseHelper = new DatabaseHelper(this.getActivity());
        // todo: modificar la funcio pq torni UserDebtor
        Log.d("", paymentId.toString());
        Cursor cursor = databaseHelper.getUsersFromP(paymentId);
        ArrayList<UserDebtor> users_debtors  = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(REL_USER_P_ID));
                @SuppressLint("Range") int user_id = cursor.getInt(cursor.getColumnIndex(REL_USER_P_USER_ID));
                @SuppressLint("Range") int payment_id = cursor.getInt(cursor.getColumnIndex(REL_USER_P_PAYMENT_ID));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(REL_USER_P_AMOUNT));

                // Arrodonim amount, no interessen molts decimals
                if(amount % 1 == 0) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    amount = Double.parseDouble(decimalFormat.format(amount));
                }
                else {
                    amount = Math.round(amount * 100.0) / 100.0;
                }

                User user = databaseHelper.getUser(user_id);
                UserDebtor user_debtor = new UserDebtor(id, user_id, payment_id, user.getUsername(), amount);
                users_debtors.add(user_debtor);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerview = view.findViewById(R.id.recyclerViewRelUsersP);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserDebtorAdapter(users_debtors);
        recyclerview.setAdapter(adapter);

        return view;
    }


    private void loadPaymentGroupData() {

    }
}
