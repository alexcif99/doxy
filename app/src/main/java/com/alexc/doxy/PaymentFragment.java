package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.PAYMENT_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_PAYMENT_GROUP_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_PAYMENT_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_USER_ID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentFragment extends Fragment implements UserDebtorAdapter.OnUserDebtorClickListener {
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewAmount;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerview;
    private RecyclerView recyclerviewYaPagados;
    private UserDebtorAdapter adapter;
    private UserDebtorAdapter adapterYaPagados;
    private Integer paymentId;
    private ArrayList<UserDebtor> users_debtors;

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

            textViewTitle.setText(title);
            textViewDescription.setText(description);
            textViewAmount.setText(Double.toString(amount) + " €");
        }

        databaseHelper = new DatabaseHelper(this.getActivity());
       
        Cursor cursorDeben = databaseHelper.getUsersFromP(paymentId);
        users_debtors  = new ArrayList<>();
        if (cursorDeben != null && cursorDeben.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursorDeben.getInt(cursorDeben.getColumnIndex(REL_USER_P_ID));
                @SuppressLint("Range") int user_id = cursorDeben.getInt(cursorDeben.getColumnIndex(REL_USER_P_USER_ID));
                @SuppressLint("Range") int payment_id = cursorDeben.getInt(cursorDeben.getColumnIndex(REL_USER_P_PAYMENT_ID));
                @SuppressLint("Range") double amount = cursorDeben.getDouble(cursorDeben.getColumnIndex(REL_USER_P_AMOUNT));

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
                UserDebtor user_debtor = new UserDebtor(id, user_id, payment_id, user.getUsername(), amount);
                users_debtors.add(user_debtor);
            } while (cursorDeben.moveToNext());
        }
        if (cursorDeben != null) {
            cursorDeben.close();
        }

        // Configurar el RecyclerView
        recyclerview = view.findViewById(R.id.recyclerViewRelUsersP);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserDebtorAdapter(users_debtors, this);
        adapter.setOnUserDebtorClickListener(this);
        recyclerview.setAdapter(adapter);

//        Cursor cursorYaPagados = databaseHelper.getUsersYaPagadosFromP(paymentId);
//        ArrayList<UserDebtor> users_ya_pagados  = new ArrayList<>();
//        if (cursorYaPagados != null && cursorYaPagados.moveToFirst()) {
//            do {
//                @SuppressLint("Range") int id = cursorYaPagados.getInt(cursorYaPagados.getColumnIndex(REL_USER_P_ID));
//                @SuppressLint("Range") int user_id = cursorYaPagados.getInt(cursorYaPagados.getColumnIndex(REL_USER_P_USER_ID));
//                @SuppressLint("Range") int payment_id = cursorYaPagados.getInt(cursorYaPagados.getColumnIndex(REL_USER_P_PAYMENT_ID));
//                @SuppressLint("Range") double amount = cursorYaPagados.getDouble(cursorYaPagados.getColumnIndex(REL_USER_P_AMOUNT));
//
//                // Arrodonim amount, no interessen molts decimals
//                if (amount % 1 == 0) {
//                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
//                    String formattedAmount = decimalFormat.format(amount);
//                    formattedAmount = formattedAmount.replace(",", ".");
//                    amount = Double.parseDouble(formattedAmount);
//                } else {
//                    amount = Math.round(amount * 100.0) / 100.0;
//                }
//
//                User user = databaseHelper.getUser(user_id);
//                UserDebtor user_debtor = new UserDebtor(id, user_id, payment_id, user.getUsername(), amount);
//                users_ya_pagados.add(user_debtor);
//            } while (cursorYaPagados.moveToNext());
//        }
//        if (cursorYaPagados != null) {
//            cursorYaPagados.close();
//        }
//
//        // Configurar el RecyclerView
//        recyclerviewYaPagados = view.findViewById(R.id.recyclerViewPaidPayments);
//        recyclerviewYaPagados.setLayoutManager(new LinearLayoutManager(getActivity()));
//        adapterYaPagados = new UserDebtorAdapter(users_ya_pagados, null);
//        recyclerviewYaPagados.setAdapter(adapterYaPagados);

        return view;
    }

    private void loadPaymentGroupData() {

    }

    @Override
    public void onUserDebtorClick(UserDebtor userDebtor) {
        // Aquí muestras el cuadro de diálogo o realizas la acción necesaria con el usuario seleccionado
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        User u = databaseHelper.getUser(Integer.parseInt(userId));

        if (userDebtor.getUser_id() != u.getId()){
            Toast.makeText(getActivity(), "No puedes marcar como pagado un pago que no es tuyo", Toast.LENGTH_SHORT).show();
        }
        else {
            showConfirmationDialog(userDebtor);

        }
    }

    private void showConfirmationDialog(UserDebtor userDebtor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Marcar como pagado");
        builder.setMessage("¿Estás seguro que deseas marcar como pagado el pago de " + userDebtor.getAmount() + "€ a " + userDebtor.getUsername());
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makePayment(userDebtor);
            }
        });
        builder.setNegativeButton("No", null);
        builder.create().show();
    }

    @SuppressLint("Range")
    private void makePayment(UserDebtor userDebtor) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        int paymentGroupId = databaseHelper.getPaymentGroupFromPaymentId(userDebtor.getPayment_id());
        Log.d("Payment id = ", Double.toString(userDebtor.getPayment_id()));
        Payment payment = databaseHelper.getPayment(userDebtor.getPayment_id());

        // todo: posar isPaid a 1 al rel user payment i recalgular els deutes?? no se si funcionaria pero ens estalviareim feina


        // Actualitzem amount usuari pagador
        Double old_amount_owner = databaseHelper.getDoubleAmountRelUserPG(paymentGroupId, payment.getOwnerUserId());
        Double amountToRecive = databaseHelper.getAmountFromRelUserP(payment.getOwnerUserId(), paymentId);
//        Log.d("Old amount popietari: ", Double.toString(old_amount_owner));
        databaseHelper.subtractAmountRelUserPG(paymentGroupId, payment.getOwnerUserId(), amountToRecive, old_amount_owner);
        // Actualitzem amount usuari deutor
        Double my_old_amount = databaseHelper.getDoubleAmountRelUserPG(paymentGroupId, user_id);
        Double amountToPay = databaseHelper.getAmountFromRelUserP(payment.getOwnerUserId(), paymentId);
//        Log.d("My old amount: ", Double.toString(my_old_amount));
        databaseHelper.addAmountRelUserPG(paymentGroupId, user_id, amountToPay, my_old_amount);

        // Marquem com a pagat
        databaseHelper.setAsPaidRelUserP(user_id, paymentId);

//         Recalculem les transactions
        CreatePaymentFragment createPaymentFragment = new CreatePaymentFragment();
        createPaymentFragment.balancePayments(paymentGroupId, databaseHelper);
//            } while (cursor.moveToNext());
//        }
//        if (cursor != null) {
//            cursor.close();
//        }

        Toast.makeText(getActivity(), "Marcado como pagado correctamente", Toast.LENGTH_SHORT).show();
    }
}
