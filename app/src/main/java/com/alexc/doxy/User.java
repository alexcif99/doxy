package com.alexc.doxy;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class User {
    private int id;
    private String nombre;
    private String apellido;
    private String username;
    private String email;
    private String contraseña;
    private Boolean isChecked;
    private Bitmap profileBitmap;

    // Constructor
    public User(int id, String nombre, String apellido, String username, String email, Boolean isChecked) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.email = email;
        this.isChecked = isChecked;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Bitmap getRoundedImageProfile() {
        if (profileBitmap == null || profileBitmap.getWidth() == 0 || profileBitmap.getHeight() == 0) {
            return null;
        }

        // Calcular el radio del círculo para hacer la imagen redonda
        int radius = Math.min(profileBitmap.getWidth(), profileBitmap.getHeight()) / 2;

        // Crear un Bitmap para la imagen de perfil redonda
        Bitmap roundedBitmap = Bitmap.createBitmap(profileBitmap.getWidth(), profileBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        // Crear un shader para aplicar la imagen al círculo
        BitmapShader shader = new BitmapShader(profileBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        // Crear un objeto Paint para configurar el shader
        Paint paint = new Paint();
        paint.setShader(shader);

        // Crear un objeto Canvas para dibujar la imagen de perfil redonda
        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawRoundRect(new RectF(0, 0, profileBitmap.getWidth(), profileBitmap.getHeight()), radius, radius, paint);

        return roundedBitmap;
    }
}
