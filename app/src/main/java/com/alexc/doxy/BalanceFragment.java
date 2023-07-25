package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_USER_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_ID;
import static com.alexc.doxy.DatabaseHelper.REL_USER_P_PAYMENT_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BalanceFragment extends Fragment {

    private TextView textViewTitleDebes;
    private TextView textViewTitleTeDeben;
    private RecyclerView recyclerviewDebes;
    private RecyclerView recyclerviewTeDeben;
    private UserDebtorAdapter adapter;
    private DatabaseHelper databaseHelper;

    public BalanceFragment() {
        // Required empty public constructor
    }

//    public static BalanceFragment newInstance(String param1, String param2) {
//        BalanceFragment fragment = new BalanceFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        textViewTitleDebes = view.findViewById(R.id.textViewDebes);
        textViewTitleTeDeben = view.findViewById(R.id.textViewTeDeben);
        recyclerviewDebes = view.findViewById(R.id.recyclerViewDebes);
        recyclerviewDebes = view.findViewById(R.id.recyclerViewTeDeben);

        databaseHelper = new DatabaseHelper(this.getActivity());

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        // Debes -----------------------------------------------------------------------------------

        Cursor cursorDebes = databaseHelper.getUsersDebes(user_id);

        Log.d("mida query", Integer.toString(cursorDebes.getCount()));

        ArrayList<UserDebtor> users_debtors  = new ArrayList<>();
        if (cursorDebes != null && cursorDebes.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursorDebes.getInt(cursorDebes.getColumnIndex(REL_USER_P_ID));
//                @SuppressLint("Range") int payment_user_id = cursorDebes.getInt(cursorDebes.getColumnIndex(REL_USER_P_USER_ID));
                @SuppressLint("Range") int payment_id = cursorDebes.getInt(cursorDebes.getColumnIndex(REL_USER_P_PAYMENT_ID));
                @SuppressLint("Range") double amount = cursorDebes.getDouble(cursorDebes.getColumnIndex(REL_USER_P_AMOUNT));

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
            } while (cursorDebes.moveToNext());
        }
        if (cursorDebes != null) {
            cursorDebes.close();
        }

        adapter = new UserDebtorAdapter(users_debtors);
        recyclerviewDebes.setAdapter(adapter);

        // Te deben --------------------------------------------------------------------------------

//        Cursor cursorTeDeben = databaseHelper.getUsersTeDeben(user_id);
//        ArrayList<UserDebtor> users_te_deben  = new ArrayList<>();
//        if (cursorTeDeben != null && cursorTeDeben.moveToFirst()) {
//            do {
//                @SuppressLint("Range") int id = cursorTeDeben.getInt(cursorTeDeben.getColumnIndex(REL_USER_PG_ID));
//                @SuppressLint("Range") int payment_user_id = cursorTeDeben.getInt(cursorTeDeben.getColumnIndex(REL_USER_PG_USER_ID));
//                @SuppressLint("Range") int payment_id = cursorTeDeben.getInt(cursorTeDeben.getColumnIndex(REL_USER_P_PAYMENT_ID));
//                @SuppressLint("Range") double amount = cursorTeDeben.getDouble(cursorTeDeben.getColumnIndex(REL_USER_P_AMOUNT));
//
//                // Arrodonim amount, no interessen molts decimals
//                DecimalFormat decimalFormat = new DecimalFormat("#.00");
//                double roundedAmount = Double.parseDouble(decimalFormat.format(amount));
//
//                User user = databaseHelper.getUser(user_id);
//                UserDebtor user_te_debe = new UserDebtor(id, user_id, payment_id, user.getUsername(), roundedAmount);
//                users_te_deben.add(user_te_debe);
//            } while (cursorTeDeben.moveToNext());
//        }
//        if (cursorTeDeben != null) {
//            cursorTeDeben.close();
//        }
//
//        adapter = new UserDebtorAdapter(users_te_deben);
//        recyclerviewTeDeben.setAdapter(adapter);
//
        return view;
    }
}