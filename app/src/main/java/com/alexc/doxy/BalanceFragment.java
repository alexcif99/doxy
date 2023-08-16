package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_USER_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_PAYMENT_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_PAYMENT_GROUP_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_USERTO_PAY_ID;
import static com.alexc.doxy.DatabaseHelper.TRANSACTION_USER_ID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BalanceFragment extends Fragment {

    private TextView textViewTitleDebes;
    private TextView textViewTitleTeDeben;
    private RecyclerView recyclerviewDebes;
    private RecyclerView recyclerviewTeDeben;
    private TransactionAdapter adapterDebes;
    private TransactionAdapter adapterTeDeben;
    private DatabaseHelper databaseHelper;

    public BalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        textViewTitleDebes = view.findViewById(R.id.textViewDebes);
        textViewTitleTeDeben = view.findViewById(R.id.textViewTeDeben);

        databaseHelper = new DatabaseHelper(this.getActivity());

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        // Debes -----------------------------------------------------------------------------------

        Cursor cursorDebes = databaseHelper.getDebesTransactions(user_id);

        Log.d("mida query", Integer.toString(cursorDebes.getCount()));

        ArrayList<Transaction> debtsTransaction  = new ArrayList<>();
        if (cursorDebes != null && cursorDebes.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursorDebes.getInt(cursorDebes.getColumnIndex(TRANSACTION_ID));
                @SuppressLint("Range") int user_to_pay_id = cursorDebes.getInt(cursorDebes.getColumnIndex(TRANSACTION_USERTO_PAY_ID));
                @SuppressLint("Range") int payment_group_id = cursorDebes.getInt(cursorDebes.getColumnIndex(TRANSACTION_PAYMENT_GROUP_ID));
                @SuppressLint("Range") double amount = cursorDebes.getDouble(cursorDebes.getColumnIndex(TRANSACTION_AMOUNT));

                // Arrodonim amount, no interessen molts decimals
                if (amount % 1 == 0) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    String formattedAmount = decimalFormat.format(amount);
                    formattedAmount = formattedAmount.replace(",", ".");
                    amount = Double.parseDouble(formattedAmount);
                } else {
                    amount = Math.round(amount * 100.0) / 100.0;
                }

                User user = databaseHelper.getUser(user_id);
                User user_to_pay = databaseHelper.getUser(user_to_pay_id);
                PaymentGroup paymentGroup = databaseHelper.getPaymentGroupObj(payment_group_id);

                Transaction transaction = new Transaction(id, paymentGroup, user, user_to_pay, amount);
                debtsTransaction.add(transaction);
            } while (cursorDebes.moveToNext());
        }
        if (cursorDebes != null) {
            cursorDebes.close();
        }

        recyclerviewDebes = view.findViewById(R.id.recyclerViewDebes);
        recyclerviewDebes.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterDebes = new TransactionAdapter(debtsTransaction);
        adapterDebes.setOnTransactionClickListener(new TransactionAdapter.OnTransactionClickListener() {
            @Override
            public void onTransactionClick(Transaction transaction) {
                showConfirmationDialogForTransaction(transaction);
            }
        });
        recyclerviewDebes.setAdapter(adapterDebes);

        // Te deben --------------------------------------------------------------------------------

        Cursor cursorTeDeben = databaseHelper.getTedebenTransactions(user_id);

        Log.d("mida query", Integer.toString(cursorTeDeben.getCount()));

        ArrayList<Transaction> creditsTransaction  = new ArrayList<>();
        if (cursorTeDeben != null && cursorTeDeben.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursorTeDeben.getInt(cursorTeDeben.getColumnIndex(TRANSACTION_ID));
                @SuppressLint("Range") int debtor_user_id = cursorTeDeben.getInt(cursorTeDeben.getColumnIndex(TRANSACTION_USER_ID));
                @SuppressLint("Range") int payment_group_id = cursorTeDeben.getInt(cursorTeDeben.getColumnIndex(TRANSACTION_PAYMENT_GROUP_ID));
                @SuppressLint("Range") double amount = cursorTeDeben.getDouble(cursorTeDeben.getColumnIndex(TRANSACTION_AMOUNT));

                // Arrodonim amount, no interessen molts decimals
                if (amount % 1 == 0) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    String formattedAmount = decimalFormat.format(amount);
                    formattedAmount = formattedAmount.replace(",", ".");
                    amount = Double.parseDouble(formattedAmount);
                } else {
                    amount = Math.round(amount * 100.0) / 100.0;
                }

                User user = databaseHelper.getUser(user_id);
                User debtor_user = databaseHelper.getUser(debtor_user_id);
                PaymentGroup paymentGroup = databaseHelper.getPaymentGroupObj(payment_group_id);

                Transaction transaction = new Transaction(id, paymentGroup, user, debtor_user, amount);

                Log.d("Transaction: ", Integer.toString(transaction.getId()));

                creditsTransaction.add(transaction);
            } while (cursorTeDeben.moveToNext());
        }
        if (cursorTeDeben != null) {
            cursorTeDeben.close();
        }

        recyclerviewTeDeben = view.findViewById(R.id.recyclerViewTeDeben);
        recyclerviewTeDeben.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterTeDeben = new TransactionAdapter(creditsTransaction);
        recyclerviewTeDeben.setAdapter(adapterTeDeben);
        return view;
    }

    public void showConfirmationDialogForTransaction(Transaction transaction){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Marcar como pagado");
        builder.setMessage("¿Quieres liquidar tu deuda de " +
                transaction.getAmount() + "€ con " + transaction.getUser_to_pay().getUsername());
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makePayment(transaction);
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }

    public void makePayment(Transaction transaction){


        Toast.makeText(getActivity(), "Deudas del grupo" + transaction.getPayment_group().getTitle() +
                " con " + transaction.getUser_to_pay().getUsername() + " saldadas correctamente",
                Toast.LENGTH_SHORT).show();
    }
}