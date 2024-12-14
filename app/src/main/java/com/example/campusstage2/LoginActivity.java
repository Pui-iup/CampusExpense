package com.example.campusstage2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusstage2.model.Users;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = new Auth(getBaseContext());
        if (auth.getUserId() > 0) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.loginButton);
        Users users = new Users(getBaseContext());
        SQLiteDatabase db = users.dbHelper.getReadableDatabase();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = usernameInput.getText().toString();
                String inputPassword = passwordInput.getText().toString();

                if (auth.isAccountLocked(inputUsername)) {
                    long lockoutEndTime = auth.getLockoutEndTime(inputUsername);
                    long remainingTime = (lockoutEndTime - System.currentTimeMillis()) / 1000;
                    Toast.makeText(getBaseContext(), "Account is locked. Try again in " + remainingTime + " seconds.", Toast.LENGTH_LONG).show();
                    return;
                }

                Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                        new String[]{inputUsername, HashUtil.hashPassword(inputPassword)});
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                    @SuppressLint("Range") String phone = cursor.getString(cursor.getColumnIndex("phone"));
                    @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                    @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex("username"));
                    auth.saveUser(id, name, phone, email, username);
                    auth.resetLoginAttempts(inputUsername);
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    v.getContext().startActivity(intent);
                    finish();
                } else {
                    int loginAttempts = auth.incrementLoginAttempts(inputUsername);
                    if (loginAttempts >= Auth.MAX_LOGIN_ATTEMPTS) {
                        auth.lockAccount(inputUsername);
                        Toast.makeText(getBaseContext(), "Account locked due to too many failed login attempts. Try again later.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Invalid username/password. Attempts left: " + (Auth.MAX_LOGIN_ATTEMPTS - loginAttempts), Toast.LENGTH_LONG).show();
                    }
                }
                cursor.close();
            }
        });

        TextView registerLink = findViewById(R.id.register_link);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }
}
