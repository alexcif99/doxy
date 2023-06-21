package com.alexc.doxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class CreatePaymentFragment extends Fragment {

    private EditText editTextTitlePayment;
    private EditText editTextDescriptionPayment;
    private EditText editTextAmountPayment;
    private Button buttonCreatePayment;

    private DatabaseHelper databaseHelper;

    public CreatePaymentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_payment, container, false);

        editTextTitlePayment = view.findViewById(R.id.editTextTitlePayment);
        editTextDescriptionPayment = view.findViewById(R.id.editTextDescriptionPayment);
        editTextAmountPayment = view.findViewById(R.id.editTextAmountPayment);
        buttonCreatePayment = view.findViewById(R.id.buttonCreatePayment);

//        paymentGroupTableHelper = new PaymentGroupTableHelper(getActivity());

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this.getActivity());

        buttonCreatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitlePayment.getText().toString().trim();
                String description = editTextDescriptionPayment.getText().toString().trim();
                String amount = editTextAmountPayment.getText().toString().trim();
                Double amount_double = Double.parseDouble(amount);

//                    todo: obtenir user actual id i passar per parametre
                // Ejemplo de obtener la ID del usuario desde SharedPreferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
                String string_user_id = sharedPreferences.getString("userId", "");
                Integer user_id = Integer.parseInt(string_user_id);

                if (validateInput(title, description, amount)) {
                    databaseHelper.addPayment(title, description, amount_double, user_id);
                    Toast.makeText(getActivity(), "Grupo de pago creado exitosamente", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getActivity(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean validateInput(String title, String description, String amount) {
        return !title.isEmpty() && !description.isEmpty() && !amount.isEmpty();
    }

    private void clearFields() {
        editTextTitlePayment.setText("");
        editTextDescriptionPayment.setText("");
        editTextAmountPayment.setText("");
    }
}
