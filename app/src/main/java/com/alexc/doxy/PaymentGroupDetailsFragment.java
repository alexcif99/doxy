package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.PAYMENT_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_DESCRIPTION;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_ID;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_TITLE;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_USER_OWNER_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PaymentGroupDetailsFragment extends Fragment {
    private TextView textViewTitle;
    private TextView textViewDescription;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private int paymentGroupId;
    private TransactionDetailsAdapter adapter;

    public PaymentGroupDetailsFragment(){

    }

    public static PaymentGroupDetailsFragment newInstance(int paymentGroupId, String title, String description) {
        PaymentGroupDetailsFragment fragment = new PaymentGroupDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("paymentGroupId", paymentGroupId);
        args.putString("title", title);
        args.putString("description", description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paymentGroupId = getArguments().getInt("paymentGroupId");
        }
    }

    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_pg, container, false);
        textViewTitle = view.findViewById(R.id.textViewPaymentGroupTitleDetails);
        textViewDescription = view.findViewById(R.id.textViewPaymentGroupDescriptionDetails);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String description = args.getString("description");
            textViewTitle.setText(title);
            textViewDescription.setText(description);
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        databaseHelper = new DatabaseHelper(this.getActivity());

        // todo: implementar getTransactions i obtenir resultats
        Cursor cursor = databaseHelper.getTransactions(paymentGroupId);

        ArrayList<Transaction> transactions = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(PAYMENT_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(PAYMENT_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(PAYMENT_DESCRIPTION));
                @SuppressLint("Range") Double amount = cursor.getDouble(cursor.getColumnIndex(PAYMENT_AMOUNT));
                @SuppressLint("Range") Integer owner = cursor.getInt(cursor.getColumnIndex(PAYMENT_USER_OWNER_ID));

                Transaction transaction = new Transaction(id, title, description, amount, owner);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewPayments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TransactionDetailsAdapter(transactions);
        recyclerView.setAdapter(adapter);





    }
