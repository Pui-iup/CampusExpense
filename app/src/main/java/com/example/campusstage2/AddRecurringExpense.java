package com.example.campusstage2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusstage2.Adapter.CategoryAdapter;
import com.example.campusstage2.model.Budget;
import com.example.campusstage2.model.Category;
import com.example.campusstage2.model.RecurringExpense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class AddRecurringExpense extends AppCompatActivity {

    private EditText amountEditText, startDateEditText, endDateEditText, noteEditText;
    private Spinner repeatedChoiceSpinner;
    private Button saveRecurringExpenseButton;
    private TextView selectCategory;
    private Category selectedCategory;
    private Auth auth;  // Declare Auth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_recurring_expense);

        // Initialize Auth
        auth = new Auth(this);  // Pass context to Auth

        // Initialize UI elements
        amountEditText = findViewById(R.id.amount);
        startDateEditText = findViewById(R.id.startDate);
        endDateEditText = findViewById(R.id.endDate);
        noteEditText = findViewById(R.id.note);
        repeatedChoiceSpinner = findViewById(R.id.repeatedChoiceSpinner);
        saveRecurringExpenseButton = findViewById(R.id.saveRecurringExpenseButton);
        selectCategory = findViewById(R.id.selectCategory);

        // Set up category selection
        selectCategory.setOnClickListener(view -> showCategoryDialog());

        // Set up date pickers
        startDateEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showStartDatePickerDialog();
            }
            return true;
        });

        endDateEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showEndDatePickerDialog();
            }
            return true;
        });

        // Set onClickListener for the save button
        saveRecurringExpenseButton.setOnClickListener(v -> {
            // Lấy dữ liệu từ giao diện
            String amountString = amountEditText.getText().toString().trim();
            String startDateString = startDateEditText.getText().toString().trim();
            String endDateString = endDateEditText.getText().toString().trim();
            String repeatedChoice = repeatedChoiceSpinner.getSelectedItem().toString().toLowerCase();

            // Kiểm tra tính hợp lệ của dữ liệu
            if (repeatedChoice.isEmpty() || !(repeatedChoice.equals("daily") || repeatedChoice.equals("weekly") || repeatedChoice.equals("monthly") || repeatedChoice.equals("yearly"))) {
                Toast.makeText(this, "Please select a valid repeat choice", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amountString.isEmpty() || startDateString.isEmpty() || selectedCategory == null) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Tạo đối tượng RecurringExpense
                RecurringExpense recurringExpense = new RecurringExpense(this);
                recurringExpense.setCategoryId(selectedCategory.getId());
                recurringExpense.setAmount(Double.parseDouble(amountString));

                // Sử dụng định dạng ngày để chuyển đổi
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                recurringExpense.setStartDate(LocalDate.parse(startDateString, formatter));
                if (!endDateString.isEmpty()) {
                    recurringExpense.setEndDate(LocalDate.parse(endDateString, formatter));
                }

                recurringExpense.setRepeatedChoice(repeatedChoice);
                recurringExpense.setUserId(String.valueOf(auth.getUserId())); // Lấy ID người dùng từ Auth

                // Gọi hàm insert để lưu vào cơ sở dữ liệu
                recurringExpense.insertRecurringExpense(recurringExpense);

                Toast.makeText(this, "Recurring expense created successfully!", Toast.LENGTH_LONG).show();
                finish(); // Đóng activity
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
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
            selectedCategory = categories.get(position); // Gán giá trị cho selectedCategory
            if (selectedCategory != null) { // Kiểm tra nếu selectedCategory không null
                selectCategory.setText(selectedCategory.toString()); // Hiển thị tên category
            } else {
                selectCategory.setText("Select a category"); // Hoặc hiển thị thông báo lỗi
            }
            dialog.dismiss();
        });

        dialog.show();
    }



    public void showStartDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog startDatePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    startDateEditText.setText(formattedDate);
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
                    endDateEditText.setText(formattedDate);
                }, year, month, day);
        endDatePickerDialog.show();
    }

}
