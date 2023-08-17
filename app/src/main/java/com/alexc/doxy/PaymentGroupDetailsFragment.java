package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.PAYMENT_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_DESCRIPTION;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_ID;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_TITLE;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_USER_OWNER_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_PAYMENT_GROUP_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_USERTO_PAY_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_USER_ID;

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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentGroupDetailsFragment extends Fragment {
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewPaymentGroupDeudasDetails;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private int paymentGroupId;
    private TransactionDetailsAdapter adapter;

    public PaymentGroupDetailsFragment() {

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
        textViewPaymentGroupDeudasDetails = view.findViewById(R.id.textViewPaymentGroupDeudasDetails);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String description = args.getString("description");
            textViewTitle.setText(title);
            textViewDescription.setText(description);
            textViewPaymentGroupDeudasDetails.setText("Todas las deudas pendientes");
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        databaseHelper = new DatabaseHelper(this.getActivity());

        // todo: implementar getTransactions i obtenir resultats
        Cursor cursor = databaseHelper.getTransactionsFromPG(paymentGroupId);

        ArrayList<Transaction> transactions = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(TRANSACTION_ID));
                @SuppressLint("Range") Integer payment_group_id = cursor.getInt(cursor.getColumnIndex(TRANSACTION_PAYMENT_GROUP_ID));
                @SuppressLint("Range") Integer trans_user_id = cursor.getInt(cursor.getColumnIndex(TRANSACTION_USER_ID));
                @SuppressLint("Range") Integer user_to_pay_id = cursor.getInt(cursor.getColumnIndex(TRANSACTION_USERTO_PAY_ID));
                @SuppressLint("Range") Double amount = cursor.getDouble(cursor.getColumnIndex(TRANSACTION_AMOUNT));

                double amount_to_show = amount;
                if (amount % 1 == 0) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    String formattedAmount = decimalFormat.format(amount);
                    formattedAmount = formattedAmount.replace(",", ".");
                    amount_to_show = Double.parseDouble(formattedAmount);
                } else {
                    amount_to_show = Math.round(amount * 100.0) / 100.0;
                }

                PaymentGroup paymentGroup = databaseHelper.getPaymentGroupObj(payment_group_id);
                User user = databaseHelper.getUser(trans_user_id);
                User user_to_pay = databaseHelper.getUser(user_to_pay_id);
                Transaction transaction = new Transaction(id, paymentGroup, user, user_to_pay, amount, amount_to_show);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TransactionDetailsAdapter(transactions);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
