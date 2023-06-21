package com.alexc.doxy;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePaymentGroupFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonCreateGroup;

    private DatabaseHelper databaseHelper;

    public CreatePaymentGroupFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_payment_group, container, false);

        editTextTitle = rootView.findViewById(R.id.editTextTitle);
        editTextDescription = rootView.findViewById(R.id.editTextDescription);
        buttonCreateGroup = rootView.findViewById(R.id.buttonCreatePaymentGroup);

//        paymentGroupTableHelper = new PaymentGroupTableHelper(getActivity());

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this.getActivity());

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (validateInput(title, description)) {
                    databaseHelper.addPaymentGroup(title, description);
                    // todo: cridar databaseHelper.addUserPaymentRelation(payment_id, user_id)
                    Toast.makeText(getActivity(), "Grupo de pago creado exitosamente", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getActivity(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private boolean validateInput(String title, String description) {
        return !title.isEmpty() && !description.isEmpty();
    }

    private void clearFields() {
        editTextTitle.setText("");
        editTextDescription.setText("");
    }
}
