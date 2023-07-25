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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private RecyclerView recyclerView;
    private PaymentGroupAdapter adapter;
    private DatabaseHelper databaseHelper;

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
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
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        databaseHelper = new DatabaseHelper(this.getActivity());

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        Integer user_id = Integer.parseInt(string_user_id);

        // Obtener una lista de ejemplo de grupos de pago
//        List<PaymentGroup> paymentGroups = getSamplePaymentGroups();
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

                // Realizar las acciones necesarias con el Payment Group, como mostrarlo en la interfaz de usuario
                // ...

            } while (cursor.moveToNext());
        }

// Cerrar el Cursor cuando hayas terminado de leer los datos
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
                // Acciones a realizar cuando se hace clic en un Payment Group
                // Por ejemplo, abrir el fragmento PaymentGroupFragment relacionado
                PaymentGroupFragment fragment = PaymentGroupFragment.newInstance(paymentGroup.getId(), paymentGroup.getTitle(), paymentGroup.getDescription());
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return view;
    }





//    private List<PaymentGroup> getSamplePaymentGroups() {
//        List<PaymentGroup> paymentGroups = new ArrayList<>();
//
//        // Agregar grupos de pago de ejemplo
//        paymentGroups.add(new PaymentGroup(1, "Grupo 1", "Descripción del grupo 1"));
//        paymentGroups.add(new PaymentGroup(2, "Grupo 2", "Descripción del grupo 2"));
//        paymentGroups.add(new PaymentGroup(3, "Grupo 3", "Descripción del grupo 3"));
//
//        return paymentGroups;
//    }
}