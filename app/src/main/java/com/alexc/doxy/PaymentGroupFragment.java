package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.PAYMENT_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_DESCRIPTION;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_ID;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_TITLE;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_USER_OWNER_ID;

import static java.sql.DriverManager.println;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PaymentGroupFragment extends Fragment {
    private TextView textViewTitle;
    private TextView textViewDescription;
    private FloatingActionButton buttonAddPayment;
    private FloatingActionButton goToDetails;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private PaymentAdapter adapter;

    private int paymentGroupId;

    public PaymentGroupFragment() {
        // Constructor vacío requerido
    }

    public static PaymentGroupFragment newInstance(int paymentGroupId, String title, String description) {
        PaymentGroupFragment fragment = new PaymentGroupFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_group, container, false);

        textViewTitle = view.findViewById(R.id.textViewPaymentGroupTitle);
        textViewDescription = view.findViewById(R.id.textViewPaymentGroupDescription);
        buttonAddPayment = view.findViewById(R.id.goToCreatePayment);
        goToDetails = view.findViewById(R.id.goToDetails);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String description = args.getString("description");
            textViewTitle.setText(title);
            textViewDescription.setText(description);
        }

        // Obtener los datos del payment group según el ID
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        databaseHelper = new DatabaseHelper(this.getActivity());
        Cursor cursor = databaseHelper.getPayments(paymentGroupId);

        ArrayList<Payment> payments = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(PAYMENT_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(PAYMENT_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(PAYMENT_DESCRIPTION));
                @SuppressLint("Range") Double amount = cursor.getDouble(cursor.getColumnIndex(PAYMENT_AMOUNT));
                @SuppressLint("Range") Integer owner = cursor.getInt(cursor.getColumnIndex(PAYMENT_USER_OWNER_ID));

                Payment payment = new Payment(id, title, description, amount, owner);
                payments.add(payment);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewPayments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PaymentAdapter(payments);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PaymentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Payment payment) {
                println(Double.toString(payment.getAmount()));
                PaymentFragment fragment = PaymentFragment.newInstance(payment.getId(), payment.getTitle(), payment.getDescription(), payment.getAmount());
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        buttonAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = getArguments();
                Integer paymentGroupId = args.getInt("paymentGroupId");
                CreatePaymentFragment createPaymentFragment = CreatePaymentFragment.newInstance(paymentGroupId);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, createPaymentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        goToDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = getArguments();
                Integer paymentGroupId = args.getInt("paymentGroupId");
                String title = args.getString("title");
                String description = args.getString("description");
                PaymentGroupDetailsFragment paymentGroupDetailsFragment = PaymentGroupDetailsFragment.newInstance(paymentGroupId, title, description);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, paymentGroupDetailsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }


    private void loadPaymentGroupData() {

    }
}

