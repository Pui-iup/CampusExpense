package com.example.campusstage2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.campusstage2.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Budget {
    private DatabaseHelper dbHelper;
    private Integer id;
    private int amount;
    private int remaining;
    private Integer categoryId;
    private Integer userId;
    private String startDate;
    private String endDate;
    private String categoryName;

    public Budget(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public Budget(Integer id, int amount, Integer categoryId, Integer userId, String startDate, String endDate) {
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void insertBudget(int amount, Integer categoryId, Integer userId, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("remaining", amount);
        values.put("category_id", categoryId);
        values.put("user_id", userId);
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        db.insert("budgets", null, values);
        db.close();
    }

    public void deleteBudget(int budgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("budgets", "id = ?", new String[]{String.valueOf(budgetId)});
        db.close();
    }

    public List<Budget> getBudgetsByUserId(int userId) {
        List<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT b.id, b.amount, b.remaining, b.category_id, b.user_id, b.start_date, b.end_date, c.name AS category_name " +
                "FROM budgets b LEFT JOIN categories c ON b.category_id = c.id " +
                "WHERE b.user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                int remaining = cursor.getInt(cursor.getColumnIndexOrThrow("remaining"));
                Integer categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                Integer userIdFromCursor = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

                Budget budget = new Budget(id, amount, categoryId, userIdFromCursor, startDate, endDate);
                budget.setCategoryName(categoryName);
                budget.setRemaining(remaining);
                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return budgets;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
