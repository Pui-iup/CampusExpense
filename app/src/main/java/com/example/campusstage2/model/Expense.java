package com.example.campusstage2.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusstage2.DatabaseHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    public Expense(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public Expense(Integer id, int amount, Integer categoryId, Integer userId, String date) {
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.userId = userId;
        this.date = date;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void insertExpense(int amount, Integer categoryId, Integer userId, String date, String note) {
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

    public void deleteExpense(int expenseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("expense", "id = ?", new String[]{String.valueOf(expenseId)});
        db.close();
    }

    private void updateBudgetRemaining(int expenseAmount, Integer categoryId, Integer userId, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT id, remaining FROM budgets WHERE category_id = ? AND user_id = ? AND (start_date <= ? AND end_date >= ?)";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId), String.valueOf(userId), date, date});

        if (cursor != null && cursor.moveToFirst()) {
            int budgetId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int currentRemaining = cursor.getInt(cursor.getColumnIndexOrThrow("remaining"));
            int updatedRemaining = currentRemaining - expenseAmount;
            ContentValues updateValues = new ContentValues();
            updateValues.put("remaining", updatedRemaining);
            db.update("budgets", updateValues, "id = ?", new String[]{String.valueOf(budgetId)});
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    public List<Expense> getExpensesByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT e.id, e.amount, e.category_id, e.user_id, e.date, e.note, c.name AS category_name " +
                "FROM expense e LEFT JOIN categories c ON e.category_id = c.id " +
                "WHERE e.user_id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                int userIdFromCursor = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

                Expense expense = new Expense(id, amount, categoryId, userIdFromCursor, date);
                expense.setNote(note); // Set note
                expense.setCategoryName(categoryName); // Set category name
                expenses.add(expense); // Add expense to the list
            } while (cursor.moveToNext());
        }

        cursor.close(); // Always close the cursor to avoid memory leaks
        db.close(); // Close the database connection
        return expenses; // Return the list of expenses
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

    public Map<String, Integer> getSumAmountByDay(int userId) {
        Map<String, Integer> result = new LinkedHashMap<>();
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String query = "SELECT date, SUM(amount) as total FROM expense WHERE user_id = ? GROUP BY date ORDER BY date";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                result.put(date, total);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
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
}
