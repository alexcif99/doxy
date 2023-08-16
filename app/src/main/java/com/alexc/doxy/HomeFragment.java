package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.PAYMENT_GROUP_DESCRIPTION;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_GROUP_ID;
import static com.alexc.doxy.DatabaseHelper.PAYMENT_GROUP_TITLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PaymentGroupAdapter adapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton goToCreatePaymentGroup;
    private NavController navController;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        // Obtener una lista de ejemplo de grupos de pago
        Cursor cursor = databaseHelper.getPaymentGroups(user_id);

        // Verificar si el Cursor tiene datos
        ArrayList<PaymentGroup> paymentGroups = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Leer los datos de cada Payment Group del Cursor
                @SuppressLint("Range") int groupId = cursor.getInt(cursor.getColumnIndex(PAYMENT_GROUP_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(PAYMENT_GROUP_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(PAYMENT_GROUP_DESCRIPTION));

                Log.d("CursorExample", "Group ID: " + groupId);
                Log.d("CursorExample", "Title: " + title);
                Log.d("CursorExample", "Description: " + description);

                // Crear un objeto PaymentGroup con los datos leídos
                PaymentGroup paymentGroup = new PaymentGroup(groupId, title, description);
                paymentGroups.add(paymentGroup);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewPaymentGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PaymentGroupAdapter(paymentGroups);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PaymentGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PaymentGroup paymentGroup) {
                PaymentGroupFragment fragment = PaymentGroupFragment.newInstance(paymentGroup.getId(), paymentGroup.getTitle(), paymentGroup.getDescription());
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

//        navController = Navigation.findNavController(requireView());
        FloatingActionButton fabAddGroup = view.findViewById(R.id.goToCreatePaymentGroup);
//        fabAddGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Navegar al fragmento CreatePaymentGroupFragment
//                navController.navigate(R.id.createPaymentGroupFragment);
//            }
//        });
        fabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePaymentGroupFragment createPaymentGroupFragment = new CreatePaymentGroupFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, createPaymentGroupFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return view;
    }

}