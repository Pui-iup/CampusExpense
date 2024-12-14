package com.example.campusstage2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusstage2.DatabaseHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class Expense {
    private DatabaseHelper dbHelper;
    private Integer id;
    private int amount;
    private Integer categoryId;
    private Integer userId;
    private String date;
    private String note;
    private String categoryName;
    private Category category;
    private Context context;
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public Expense(Integer id, int amount, Integer categoryId, Integer userId, String date) {
        this.setId(id);
        this.setAmount(amount);
        this.setCategoryId(categoryId);
        this.setUserId(userId);
        this.setDate(date);
    }

    public void insertExpense(int amount, Integer categoryId, Integer userId,
                              String date, String note) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("category_id", categoryId);
        values.put("user_id", userId);
        values.put("date", date);
        values.put("note", note);

        long expenseId = db.insert("expense", null, values); // Chỉ gọi insert một lần
        updateBudgetRemaining(amount, categoryId, userId, date);
        Log.d("Expense", "Inserted expense rowId=" + expenseId + ", amount=" + amount + ", categoryId=" + categoryId + ", userId=" + userId + ", date=" + date);
        db.close(); // Đóng cơ sở dữ liệu sau khi thêm
    }



    public void deteleExpense(int expenseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("expense", "id = ?", new String[]{String.valueOf(expenseId)});
    }
    private void updateBudgetRemaining(int expenseAmount, Integer categoryId, Integer userId, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Find the budget ID associated with the category and user (assuming such a relationship exists)
        String query = "SELECT id, remaining FROM budgets WHERE category_id = ? AND user_id = ?" +
                "AND (start_date <= ? AND end_date >= ?)";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId), String.valueOf(userId),date,date});

        if (cursor != null && cursor.moveToFirst()) {
            int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int currentRemaining = cursor.getInt(cursor.getColumnIndexOrThrow("remaining"));
            // Subtract the expense amount from the remaining budget
            int updatedRemaining = currentRemaining - expenseAmount;
            ContentValues updateValues = new ContentValues();
            updateValues.put("remaining", updatedRemaining);

            db.update("budget", updateValues, "id = ?", new String[]{String.valueOf(budgetId)});
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    public void updateExpense() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", this.getAmount());
        values.put("category_id", this.getCategoryId());
        values.put("date", this.getDate());
        values.put("note", this.getNote());
        db.update("expense", values, "id = ?", new String[]{String.valueOf(this.getId())});
        db.close();
    }


    public Expense(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

// get by user id & current month
    public Map<String, Integer> getSumAmountByDay() {
        Map<String, Integer> result = new LinkedHashMap<>();
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String query = "SELECT date, SUM(amount) as total FROM expense GROUP BY date ORDER BY date";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                result.put(date, total);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }
}
