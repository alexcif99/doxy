package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.PAYMENT_GROUP_DESCRIPTION;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_GROUP_TITLE;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PaymentGroupFragment extends Fragment {
    private TextView textViewTitle;
    private TextView textViewDescription;
    private Button buttonAddPayment;
    private DatabaseHelper databaseHelper;

    private int paymentGroupId;

    public PaymentGroupFragment() {
        // Constructor vacío requerido
    }

    public static PaymentGroupFragment newInstance(int paymentGroupId) {
        PaymentGroupFragment fragment = new PaymentGroupFragment();
        Bundle args = new Bundle();
        args.putInt("paymentGroupId", paymentGroupId);
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
        buttonAddPayment = view.findViewById(R.id.buttonCreatePayment);

        // Obtener los datos del payment group según el ID
        databaseHelper = new DatabaseHelper(this.getActivity());
        Cursor cursor = databaseHelper.getPaymentGroup(paymentGroupId);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(PAYMENT_GROUP_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(PAYMENT_GROUP_DESCRIPTION));

            // Actualizar las vistas con los datos obtenidos
            textViewTitle.setText(title);
            textViewDescription.setText(description);
        }
        if (cursor != null) {
            cursor.close();
        }

//        // Configurar el RecyclerView
//        recyclerView = view.findViewById(R.id.recyclerViewPayments);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        adapter = new PaymentGroupAdapter(paymentGroups);
//        recyclerView.setAdapter(adapter);

        buttonAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePaymentFragment createPaymentFragment = new CreatePaymentFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, createPaymentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }


    private void loadPaymentGroupData() {

    }
}

