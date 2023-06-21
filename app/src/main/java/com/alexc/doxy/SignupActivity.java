package com.alexc.doxy;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity{

    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonSignup;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.editTextNameSignup);
        editTextSurname = findViewById(R.id.editTextSurnameSignup);
        editTextUsername = findViewById(R.id.editTextUsernameSignup);
        editTextEmail = findViewById(R.id.editTextEmailSignup);
        editTextPassword = findViewById(R.id.editTextPasswordSignup);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPasswordSignup);
        buttonSignup = findViewById(R.id.buttonSignup);

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String surname = editTextSurname.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                // todo: comprovar que password i confirmPassword son iguals

                if (validateInput(email, password)) {
                    if (databaseHelper.isUserExists(email)) {
                        Toast.makeText(SignupActivity.this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        // todo: posar try chatch a aqust tipus de funcions??
                        databaseHelper.addUser(name, surname, username, email, password);
                        Toast.makeText(SignupActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad de registro después de crear la cuenta
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInput(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }
}
