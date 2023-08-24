package com.alexc.doxy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment {

    private TextView textViewUsername;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView profile_surname;
    private ImageView profileImage;
    private Button logOutButton;

    private DatabaseHelper databaseHelper;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public ProfileFragment() {
        // Constructor vacío requerido
    }

    public static ProfileFragment newInstance(Integer userId, String name, String surname, String username, String email) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putInt("userId", userId);
        args.putString("name", name);
        args.putString("surname", surname);
        args.putString("username", username);
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewUsername = view.findViewById(R.id.profile_username);
        textViewName = view.findViewById(R.id.profile_name);
        textViewEmail = view.findViewById(R.id.profile_email);
        profile_surname = view.findViewById(R.id.profile_surname);
        profileImage = view.findViewById(R.id.profile_image);
        logOutButton = view.findViewById(R.id.logout_button);

        // Crear una instancia de DatabaseHelper
        databaseHelper = new DatabaseHelper(this.getActivity());

        // Obtener datos del usuario desde los argumentos
        Integer userId = getArguments().getInt("userId");
        String name = getArguments().getString("name");
        String surname = getArguments().getString("surname");
        String username = getArguments().getString("username");
        String email = getArguments().getString("email");

        // Mostrar los datos en los TextViews
        textViewUsername.setText("Usuario: " + username);
        textViewName.setText(name);
        textViewEmail.setText("Email: " + email);
        profile_surname.setText(surname);

        // Cargar la imagen de perfil (si existe) o establecer el icono por defecto
        byte[] profileImageBytes = databaseHelper.getProfileImage(userId);
        if (profileImageBytes != null) {
            Bitmap profileBitmap = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
            Bitmap roundedBitmap = getRoundedBitmap(profileBitmap);
            profileImage.setImageBitmap(roundedBitmap);
        } else {
            profileImage.setImageResource(R.drawable.ic_profile);
        }


        // Inicializar el ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                                // Redimensionar la imagen para evitar tamaños grandes en la base de datos
                                Bitmap resizedBitmap = getResizedBitmap(bitmap, 200);
                                byte[] imageBytes = bitmapToByteArray(resizedBitmap);
                                databaseHelper.saveProfileImage(userId, imageBytes);
                                profileImage.setImageBitmap(getRoundedBitmap(resizedBitmap));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        // Manejar el clic en el botón para editar la imagen
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir el selector de imágenes para que el usuario elija una imagen de la galería
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(pickImageIntent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }

    // Método para redimensionar un bitmap manteniendo su relación de aspecto
    private Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;

        if (width > maxSize || height > maxSize) {
            if (width > height) {
                newWidth = maxSize;
                newHeight = (height * maxSize) / width;
            } else {
                newHeight = maxSize;
                newWidth = (width * maxSize) / height;
            }
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    // Método para convertir un bitmap en un arreglo de bytes
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    // Método para redondear la imagen y aplicar una máscara circular
    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Bitmap roundedBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);

        return roundedBitmap;
    }

}
