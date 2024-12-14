package com.example.campusstage2;

import android.content.Context;
import android.content.SharedPreferences;

public class Auth {
    public static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final long LOCKOUT_DURATION = 300000; // 5 phút (300,000 ms)

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Auth(Context context) {
        sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUser(int id, String name, String phone, String email, String username) {
        editor.putInt("id", id);
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("username", username);
        editor.apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt("id", -1);
    }

    public String getName() {
        return sharedPreferences.getString("name", null);
    }

    public String getPhone() {
        return sharedPreferences.getString("phone", null);
    }

    public String getEmail() {
        return sharedPreferences.getString("email", null);
    }

    public String getUsername() {
        return sharedPreferences.getString("username", null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isAccountLocked() {
        long lockoutEndTime = sharedPreferences.getLong("lockout_end_time", 0);
        return lockoutEndTime > System.currentTimeMillis();
    }

    public long getLockoutEndTime() {
        return sharedPreferences.getLong("lockout_end_time", 0);
    }

    public void resetLoginAttempts() {
        editor.putInt("login_attempts", 0);
        editor.apply();
    }

    public int incrementLoginAttempts() {
        int loginAttempts = sharedPreferences.getInt("login_attempts", 0) + 1;
        editor.putInt("login_attempts", loginAttempts);
        editor.apply();
        return loginAttempts;
    }

    public void lockAccount() {
        long lockoutEndTime = System.currentTimeMillis() + LOCKOUT_DURATION;
        editor.putLong("lockout_end_time", lockoutEndTime);
        editor.apply();
    }

    public boolean login(String username, String password) {
        if (isAccountLocked()) {
            long lockoutEndTime = sharedPreferences.getLong("lockout_end_time", 0);
            long remainingTime = (lockoutEndTime - System.currentTimeMillis()) / 1000;
            System.out.println("Account is locked. Try again in " + remainingTime + " seconds.");
            return false;
        }

        // Kiểm tra thông tin đăng nhập của người dùng (logic này cần được triển khai)
        boolean isLoginSuccessful = checkCredentials(username, password);

        if (isLoginSuccessful) {
            resetLoginAttempts();
            return true;
        } else {
            int loginAttempts = incrementLoginAttempts();

            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                lockAccount();
                System.out.println("Account locked due to too many failed login attempts. Try again later.");
            }
            return false;
        }
    }

    private boolean checkCredentials(String username, String password) {
        // Logic kiểm tra thông tin đăng nhập (tùy chỉnh theo nhu cầu của bạn)
        return "correct_username".equals(username) && "correct_password".equals(password);
    }
}
