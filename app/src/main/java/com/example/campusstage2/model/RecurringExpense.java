package com.example.campusstage2.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.campusstage2.DatabaseHelper;
//import com.example.campusstage2.RecurringExpenseWorker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecurringExpense {
    private int id;
    private int categoryId;
    private double amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String repeatedChoice;
    private String userId;
    private Context context;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static RecurringExpense instance;

    // Private constructor to prevent direct instantiation
    private RecurringExpense() {}

    public RecurringExpense(int id, int categoryId, double amount, LocalDate startDate, LocalDate endDate, String repeatedChoice, String userId, Context context) {
        this.id = id;
        this.categoryId = categoryId;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.repeatedChoice = repeatedChoice;
        this.userId = userId;
        this.context = context;
    }

    public RecurringExpense(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
    }

    public static RecurringExpense getInstance(Context context) {
        if (instance == null) {
            instance = new RecurringExpense(context);
        }
        return instance;
    }

    private SQLiteDatabase getReadableDatabase() {
        if (context == null) {
            throw new IllegalStateException("Context is null. Cannot get readable database.");
        }
        SQLiteOpenHelper dbHelper = new DatabaseHelper(context);
        return dbHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() {
        if (context == null) {
            throw new IllegalStateException("Context is null. Cannot get writable database.");
        }
        SQLiteOpenHelper dbHelper = new DatabaseHelper(context);
        return dbHelper.getWritableDatabase();
    }

    public void insertRecurringExpense(RecurringExpense recurringExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", recurringExpense.getCategoryId());
        values.put("amount", recurringExpense.getAmount());
        values.put("start_date", recurringExpense.getStartDate().format(dateFormatter));
        values.put("end_date", recurringExpense.getEndDate() != null ? recurringExpense.getEndDate().format(dateFormatter) : null);
        values.put("repeated_choice", recurringExpense.getRepeatedChoice());
        values.put("user_id", recurringExpense.getUserId());

        db.insert("recurring_expenses", null, values);
        db.close();
    }

    public List<RecurringExpense> getRecurringExpenses(String userId) {
        List<RecurringExpense> recurringExpenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM recurring_expenses WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") int categoryId = cursor.getInt(cursor.getColumnIndex("category_id"));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
                @SuppressLint("Range") LocalDate startDate = LocalDate.parse(cursor.getString(cursor.getColumnIndex("start_date")), dateFormatter);
                @SuppressLint("Range") String endDateString = cursor.getString(cursor.getColumnIndex("end_date"));
                LocalDate endDate = endDateString != null ? LocalDate.parse(endDateString, dateFormatter) : null;
                @SuppressLint("Range") String repeatedChoice = cursor.getString(cursor.getColumnIndex("repeated_choice"));
                @SuppressLint("Range") String userIdFetched = cursor.getString(cursor.getColumnIndex("user_id"));

                RecurringExpense recurringExpense = new RecurringExpense(id, categoryId, amount, startDate, endDate, repeatedChoice, userIdFetched, context);
                recurringExpenses.add(recurringExpense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recurringExpenses;
    }

    public int updateRecurringExpense(RecurringExpense recurringExpense) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", recurringExpense.getCategoryId());
        values.put("amount", recurringExpense.getAmount());
        values.put("start_date", recurringExpense.getStartDate().format(dateFormatter));
        values.put("end_date", recurringExpense.getEndDate() != null ? recurringExpense.getEndDate().format(dateFormatter) : null);
        values.put("repeated_choice", recurringExpense.getRepeatedChoice());

        return db.update("recurring_expenses", values, "id = ?", new String[]{String.valueOf(recurringExpense.getId())});
    }

    public void deleteRecurringExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("recurring_expenses", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Schedules recurring expenses using WorkManager.
     */
//    public void scheduleRecurringExpenses(Context context) {
//        WorkManager workManager = WorkManager.getInstance(context);
//        Data inputData = new Data.Builder()
//                .putString("user_id", userId)
//                .build();
//
//        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RecurringExpenseWorker.class)
//                .setInputData(inputData)
//                .setInitialDelay(1, TimeUnit.MINUTES)  // Set initial delay
//                .build();
//
//        workManager.enqueue(workRequest);
//    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getRepeatedChoice() {
        return repeatedChoice;
    }

    public void setRepeatedChoice(String repeatedChoice) {
        this.repeatedChoice = repeatedChoice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
