package com.alexc.doxy;

import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_AMOUNT;
import static com.alexc.doxy.DatabaseHelper.REL_USER_PG_USER_ID;
import static com.alexc.doxy.DatabaseHelper.USER_EMAIL;
import static com.alexc.doxy.DatabaseHelper.USER_ID;
import static com.alexc.doxy.DatabaseHelper.USER_NAME;
import static com.alexc.doxy.DatabaseHelper.USER_SURNAME;
import static com.alexc.doxy.DatabaseHelper.USER_USERNAME;

import static java.sql.DriverManager.println;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kotlin.Triple;

public class CreatePaymentFragment extends Fragment {

    private EditText editTextTitlePayment;
    private EditText editTextDescriptionPayment;
    private EditText editTextAmountPayment;
    private Button buttonCreatePayment;
    private RecyclerView recyclerview;
    private UserToAddPgAdapter adapter;
    private Integer currentUserId;

    private DatabaseHelper databaseHelper;

    public CreatePaymentFragment() {
    }

    public static CreatePaymentFragment newInstance(int paymentGroupId) {
        CreatePaymentFragment fragment = new CreatePaymentFragment();
        Bundle args = new Bundle();
        args.putInt("paymentGroupId", paymentGroupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_payment, container, false);

        editTextTitlePayment = view.findViewById(R.id.editTextTitlePayment);
        editTextDescriptionPayment = view.findViewById(R.id.editTextDescriptionPayment);
        editTextAmountPayment = view.findViewById(R.id.editTextAmountPayment);
        buttonCreatePayment = view.findViewById(R.id.buttonCreatePayment);

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this.getActivity());

        //Obtener la ID del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DoxyPrefs", Context.MODE_PRIVATE);
        String string_user_id = sharedPreferences.getString("userId", "");
        currentUserId = Integer.parseInt(string_user_id);

        // Posem els usuaris al recyclerview
        Bundle bundle = getArguments();
        Integer pg_id = bundle.getInt("paymentGroupId");

        Cursor cursor = databaseHelper.getUsersFromPG(pg_id);
        ArrayList<User> users  = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(REL_USER_PG_USER_ID));
                if(id != currentUserId) {
                    User user = databaseHelper.getUser(id);
                    users.add(user);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        // Configurar el RecyclerView
        recyclerview = view.findViewById(R.id.recyclerViewUsersToAddP);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserToAddPgAdapter(users);
        recyclerview.setAdapter(adapter);


        buttonCreatePayment.setOnClickListener(new View.OnClickListener() {
            // todo: validate input (aqui i a tots els inputs)
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                String title = editTextTitlePayment.getText().toString().trim();
                String description = editTextDescriptionPayment.getText().toString().trim();
                String amount = editTextAmountPayment.getText().toString().trim();
                Double amount_double = Double.parseDouble(amount);

                // Una vegada emplenat en recyclerview, tornem a posar el currentUserId, per que a efectes contables si que volemque hi sigui
                User currentUser = databaseHelper.getUser(currentUserId);

                // todo: no es = users, es = checked_users, provar
                ArrayList<User> usersInPayment = get_checked_users(users);

                usersInPayment.add(currentUser);

                if (validateInput(title, description, amount)) {
                    long payment_id = databaseHelper.addPayment(title, description, amount_double, Boolean.FALSE, currentUserId, pg_id);
                    // todo: de moment dividim la quantitat total entre el nombre de participants, més endevant potser es fa que es pugui fixar un preu
                    Double amount_rel_user_p = amount_double/Double.parseDouble(String.valueOf(usersInPayment.size()));
                    for(User user:usersInPayment){
                        Double old_amount = 0.0;
                        if (user.getId() == Integer.parseInt(string_user_id)){
                            databaseHelper.addRelUserP(user.getId(), payment_id, amount_rel_user_p, Boolean.TRUE);
                            Cursor cursor_old_amount = databaseHelper.getAmountRelUserPG(pg_id, user.getId());
                            if (cursor_old_amount != null && cursor_old_amount.moveToFirst()) {
                                old_amount = cursor_old_amount.getDouble(cursor_old_amount.getColumnIndex(REL_USER_PG_AMOUNT));
                                cursor_old_amount.close();
                            }

                            Log.d("old_amount: ", Double.toString(old_amount));
                            Log.d("user_id: ", Double.toString(user.getId()));
                            databaseHelper.addAmountRelUserPG(pg_id, user.getId(), (amount_double - amount_rel_user_p), old_amount);
                        }
                        else {
                            databaseHelper.addRelUserP(user.getId(), payment_id, amount_rel_user_p, Boolean.FALSE);
                            Cursor cursor_old_amount = databaseHelper.getAmountRelUserPG(pg_id, user.getId());
                            if (cursor_old_amount != null && cursor_old_amount.moveToFirst()) {
                                old_amount = cursor_old_amount.getDouble(cursor_old_amount.getColumnIndex(REL_USER_PG_AMOUNT));
                                cursor_old_amount.close();
                            }

                            Log.d("old_amount: ", Double.toString(old_amount));
                            databaseHelper.subtractAmountRelUserPG(pg_id, user.getId(), amount_rel_user_p, old_amount);
                        }
                    }
                    // Calculem les transaccions, creem les necessaries i modifiquem les existents
                    balancePayments(pg_id, databaseHelper);


                    Toast.makeText(getActivity(), "Pago creado exitosamente", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getActivity(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void balancePayments(Integer pg_id, DatabaseHelper databaseHelper) {
        // Netejar tots els registres de transaccione per aquets grup
        databaseHelper.deleteAllTransactions(pg_id);

        // Obtener todos los registros de la tabla REL_USER_PG relacionados con el Payment Group actual
        // todo: no se si passar pg_id o no
        Cursor cursorPositiveAmounts = databaseHelper.getRelUserPGPositiveAmount(pg_id);
        Cursor cursorNegativeAmounts = databaseHelper.getRelUserPGNegativeAmount(pg_id);

        Log.d("debts", "debts: " + cursorPositiveAmounts);
        Log.d("credits", "credits: " + cursorNegativeAmounts);

        // Debts y Credits son diccionarios para almacenar las deudas y créditos de cada usuario.
        HashMap<Integer, Double> debts = new HashMap<>(); // {userId: totalDebt}
        HashMap<Integer, Double> credits = new HashMap<>(); // {userId: totalCredit}

        // Procesar registros con montos positivos (deudas de los usuarios)
        if (cursorPositiveAmounts != null && cursorPositiveAmounts.moveToFirst()) {
            do {
                @SuppressLint("Range") int userId = cursorPositiveAmounts.getInt(cursorPositiveAmounts.getColumnIndex(REL_USER_PG_USER_ID));
                @SuppressLint("Range") double amount = cursorPositiveAmounts.getDouble(cursorPositiveAmounts.getColumnIndex(REL_USER_PG_AMOUNT));

                // El usuario debe dinero a otros
                if (!credits.containsKey(userId)) {
                    credits.put(userId, 0.0);
                }
                credits.put(userId, credits.get(userId) + amount);
            } while (cursorPositiveAmounts.moveToNext());
            cursorPositiveAmounts.close();
        }

        // Procesar registros con montos negativos (créditos de los usuarios)
        if (cursorNegativeAmounts != null && cursorNegativeAmounts.moveToFirst()) {
            do {
                @SuppressLint("Range") int userId = cursorNegativeAmounts.getInt(cursorNegativeAmounts.getColumnIndex(REL_USER_PG_USER_ID));
                @SuppressLint("Range") double amount = cursorNegativeAmounts.getDouble(cursorNegativeAmounts.getColumnIndex(REL_USER_PG_AMOUNT));

                // El usuario es pagador
                if (!debts.containsKey(userId)) {
                    debts.put(userId, 0.0);
                }
                debts.put(userId, debts.get(userId) - amount);
            } while (cursorNegativeAmounts.moveToNext());
            cursorNegativeAmounts.close();
        }

        Log.d("debts", "debts: " + debts);
        Log.d("credits", "credits: " + credits);

        // Crear listas con las deudas positivas y los créditos positivos
        ArrayList<Pair<Integer, Double>> positiveDebts = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : debts.entrySet()) {
            int userId = entry.getKey();
            double debt = entry.getValue();
            if (debt > 0) {
                positiveDebts.add(new Pair<>(userId, debt));
            }
        }

        ArrayList<Pair<Integer, Double>> positiveCredits = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : credits.entrySet()) {
            int userId = entry.getKey();
            double credit = entry.getValue();
            if (credit > 0) {
                positiveCredits.add(new Pair<>(userId, credit));
            }
        }

        Log.d("positiveDebts", "positiveDebts: " + positiveDebts);
        Log.d("positiveCredits", "positiveCredits: " + positiveCredits);

        // Realizar el balance de pagos
        ArrayList<Triple<Integer, Integer, Double>> transactions = new ArrayList<>();
        while (!positiveDebts.isEmpty() && !positiveCredits.isEmpty()) {
            int debtorId = getHighestDebtUserId(positiveDebts);
            int creditorId = getHighestCreditUserId(positiveCredits);
            double debtorDebt = debts.get(debtorId);
            double creditorCredit = credits.get(creditorId);

            // Calcular la cantidad a pagar en esta transacción
            double amountToPay;
            if (debtorDebt > creditorCredit) {
                amountToPay = creditorCredit;
                debts.put(debtorId, debtorDebt - creditorCredit);
                Iterator<Pair<Integer, Double>> creditsIterator = positiveCredits.iterator();
                while (creditsIterator.hasNext()) {
                    Pair<Integer, Double> creditEntry = creditsIterator.next();
                    if (creditEntry.first == creditorId) {
                        double updatedCredit = creditEntry.second - amountToPay;
                        if (updatedCredit > 0) {
                            creditEntry = new Pair<>(creditorId, updatedCredit);
                            creditsIterator.remove();
                        } else {
                            creditsIterator.remove();
                        }
                        break;
                    }
                }
            } else {
                // todo: algo passa, la segona iteracio no la fa be
                amountToPay = debtorDebt;
                credits.put(creditorId, creditorCredit - debtorDebt);
                Iterator<Pair<Integer, Double>> debtsIterator = positiveDebts.iterator();
                while (debtsIterator.hasNext()) {
                    Pair<Integer, Double> debtEntry = debtsIterator.next();
                    if (debtEntry.first == debtorId) {
                        double updatedDebt = debtEntry.second - amountToPay;
                        if (updatedDebt > 0) {
                            debtEntry = new Pair<>(debtorId, updatedDebt);
                            debtsIterator.remove();
                        } else {
                            debtsIterator.remove();
                        }
                        break;
                    }
                }
            }

            // Agregar la transacción a la lista de transacciones
            transactions.add(new Triple<>(debtorId, creditorId, amountToPay));
            databaseHelper.addTransaction(pg_id, creditorId, debtorId, amountToPay);
        }
    }

    // Métodos auxiliares para obtener el usuario con la deuda más alta y el usuario con el crédito más alto
    private int getHighestDebtUserId(ArrayList<Pair<Integer, Double>> debts) {
        double highestDebt = 0;
        int highestDebtUserId = -1;
        for (Pair<Integer, Double> debtEntry : debts) {
            double debt = debtEntry.second;
            if (debt > highestDebt) {
                highestDebt = debt;
                highestDebtUserId = debtEntry.first;
            }
        }
        return highestDebtUserId;
    }

    private int getHighestCreditUserId(ArrayList<Pair<Integer, Double>> credits) {
        double highestCredit = 0;
        int highestCreditUserId = -1;
        for (Pair<Integer, Double> creditEntry : credits) {
            double credit = creditEntry.second;
            if (credit > highestCredit) {
                highestCredit = credit;
                highestCreditUserId = creditEntry.first;
            }
        }
        return highestCreditUserId;
    }

    private ArrayList<User> get_checked_users(ArrayList<User> users) {
        ArrayList<User> checked_users = new ArrayList<>();
        for(User user : users){
            if (user.isChecked()) {
                Log.d("checked: ", user.getUsername());
                checked_users.add(user);
            }
        }
        return checked_users;
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
