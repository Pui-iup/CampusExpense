package com.example.campusstage2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.campusstage2.Adapter.CategoryAdapter;
import com.example.campusstage2.model.Budget;
import com.example.campusstage2.model.Category;

import java.util.Calendar;
import java.util.List;

public class AddBudgetActivity extends AppCompatActivity {

    EditText amount;
    EditText startDate;
    EditText endDate;
    Button saveBudget;
    TextView selectCategory;
    Category selectedCategory;
    Auth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_budget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        amount = findViewById(R.id.amount);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        selectCategory = findViewById(R.id.selectCategory);
        saveBudget = findViewById(R.id.saveBudget);
        auth = new Auth(this);

        // Event Listeners
        startDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showStartDatePickerDialog();
            }
            return true;
        });

        endDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showEndDatePickerDialog();
            }
            return true;
        });

        selectCategory.setOnClickListener(view -> showCategoryDialog());

        saveBudget.setOnClickListener(view -> {
            String startDateString = startDate.getText().toString();
            String endDateString = endDate.getText().toString();
            Budget budget = new Budget(this);
            budget.insertBudget(
                    Integer.valueOf(amount.getText().toString()), selectedCategory.getId(),
                    auth.getUserId(), startDateString, endDateString);
            Toast.makeText(getBaseContext(), "Created budget success!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void showCategoryDialog() {
        Category category = new Category(this);
        List<Category> categories = category.getAllCategories();
        View dialogView = LayoutInflater.from(this).inflate(R.layout.category_list_view, null);
        ListView categoryListView = dialogView.findViewById(R.id.categoryList);
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        categoryListView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setView(dialogView)
                .create();

        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = categories.get(position);
            selectCategory.setText(selectedCategory.getName());
            dialog.dismiss();
        });

        dialog.show();

        Button addCategory = dialogView.findViewById(R.id.addCategory);
        addCategory.setOnClickListener(view -> {
            dialog.dismiss();
            View createCategoryView = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_add_category, null);
            AlertDialog createCategoryDialog = new AlertDialog.Builder(this)
                    .setTitle("Add Category")
                    .setView(createCategoryView)
                    .create();
            createCategoryDialog.show();

            Button saveCategory = createCategoryDialog.findViewById(R.id.submitCategory);
            saveCategory.setOnClickListener(view1 -> {
                EditText newCategoryInput = createCategoryDialog.findViewById(R.id.categoryInput);
                Category newCategory = new Category(this.getBaseContext());
                newCategory.insertCategory(newCategoryInput.getText().toString(), null, auth.getUserId());
                createCategoryDialog.dismiss();
                showCategoryDialog();
            });
        });
    }

    public void showStartDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog startDatePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    startDate.setText(formattedDate);
                }, year, month, day);
        startDatePickerDialog.show();
    }

    public void showEndDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog endDatePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    endDate.setText(formattedDate);
                }, year, month, day);
        endDatePickerDialog.show();
    }
}
