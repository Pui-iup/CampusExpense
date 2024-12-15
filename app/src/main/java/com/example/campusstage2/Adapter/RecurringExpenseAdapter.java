package com.example.campusstage2.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusstage2.R;
import com.example.campusstage2.model.Category;
import com.example.campusstage2.model.RecurringExpense;
import com.example.campusstage2.DatabaseHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class RecurringExpenseAdapter extends RecyclerView.Adapter<RecurringExpenseAdapter.ViewHolder> {
    private final List<RecurringExpense> recurringExpenses;
    private final Context context;
    private final RecurringExpense recurringExpenseModel;
    private final DatabaseHelper databaseHelper; // Sử dụng DatabaseHelper
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public RecurringExpenseAdapter(List<RecurringExpense> recurringExpenses, Context context) {
        this.recurringExpenses = recurringExpenses;
        this.context = context;
        this.recurringExpenseModel = new RecurringExpense(context);
        this.databaseHelper = DatabaseHelper.getInstance(context); // Tạo đối tượng DatabaseHelper với context
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recurring_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecurringExpense expense = recurringExpenses.get(position);
        holder.categoryTextView.setText("Category: " + databaseHelper.getCategoryNameById(expense.getCategoryId())); // Hiển thị tên danh mục thay vì ID
        holder.amountTextView.setText("Amount: $" + expense.getAmount());
        holder.startDateTextView.setText("Start Date: " + expense.getStartDate().format(dateFormatter));
        holder.endDateTextView.setText(expense.getEndDate() != null ? "End Date: " + expense.getEndDate().format(dateFormatter) : "N/A");
        holder.repeatedChoiceTextView.setText("Repeated Choice: " + expense.getRepeatedChoice().toLowerCase());

        holder.editButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                showEditDialog(expense, adapterPosition);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                showDeleteConfirmationDialog(expense, adapterPosition); // Hiển thị hộp thoại xác nhận xóa
            }
        });
    }

    @Override
    public int getItemCount() {
        return recurringExpenses.size();
    }

    private int getSpinnerIndex(Spinner spinner, String repeatedChoice) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(repeatedChoice.toLowerCase())) {
                return i;
            }
        }
        return 0; // Nếu không tìm thấy, chọn item đầu tiên
    }

    private void showEditDialog(RecurringExpense recurringExpense, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_recurring_expense, null);
        builder.setView(dialogView);

        EditText editAmount = dialogView.findViewById(R.id.amount);
        EditText editStartDate = dialogView.findViewById(R.id.startDate);
        EditText editEndDate = dialogView.findViewById(R.id.endDate);
        Spinner spinnerRepeatedChoice = dialogView.findViewById(R.id.repeatedChoiceSpinner);
        TextView selectCategory = dialogView.findViewById(R.id.selectCategory);

        // Gán dữ liệu cho các EditText
        editAmount.setText(String.valueOf(recurringExpense.getAmount()));
        editStartDate.setText(recurringExpense.getStartDate().format(dateFormatter));
        editEndDate.setText(recurringExpense.getEndDate() != null ? recurringExpense.getEndDate().format(dateFormatter) : "");
        spinnerRepeatedChoice.setSelection(getSpinnerIndex(spinnerRepeatedChoice, recurringExpense.getRepeatedChoice().toLowerCase()));
        selectCategory.setText(databaseHelper.getCategoryNameById(recurringExpense.getCategoryId()));

        // Thiết lập sự kiện chọn ngày
        editStartDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showStartDatePickerDialog(editStartDate);
            }
            return true;
        });

        editEndDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showEndDatePickerDialog(editEndDate);
            }
            return true;
        });

        // Thiết lập sự kiện chọn danh mục
        selectCategory.setOnClickListener(v -> {
            showCategoryDialog(recurringExpense, selectCategory);
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            recurringExpense.setAmount(Double.parseDouble(editAmount.getText().toString()));
            recurringExpense.setStartDate(LocalDate.parse(editStartDate.getText().toString(), dateFormatter));
            recurringExpense.setEndDate(!editEndDate.getText().toString().isEmpty() ? LocalDate.parse(editEndDate.getText().toString(), dateFormatter) : null);
            recurringExpense.setRepeatedChoice(spinnerRepeatedChoice.getSelectedItem().toString().toLowerCase());
            updateRecurringExpense(recurringExpense, position); // Cập nhật dữ liệu
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void showStartDatePickerDialog(EditText editText) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog startDatePickerDialog = new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
            editText.setText(formattedDate); // Set date to the EditText
        }, year, month, day);
        startDatePickerDialog.show();
    }

    private void showEndDatePickerDialog(EditText editText) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog endDatePickerDialog = new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
            editText.setText(formattedDate); // Set date to the EditText
        }, year, month, day);
        endDatePickerDialog.show();
    }

    private void showCategoryDialog(RecurringExpense recurringExpense, TextView selectCategory) {
        AlertDialog.Builder categoryDialogBuilder = new AlertDialog.Builder(context);
        View categoryDialogView = LayoutInflater.from(context).inflate(R.layout.category_list_view, null);
        categoryDialogBuilder.setView(categoryDialogView);

        ListView categoryListView = categoryDialogView.findViewById(R.id.categoryList);
        Category category = new Category(context);
        List<Category> categories = category.getAllCategories();
        CategoryAdapter adapter = new CategoryAdapter(context, categories);
        categoryListView.setAdapter(adapter);

        AlertDialog categoryDialog = categoryDialogBuilder.create();
        categoryListView.setOnItemClickListener((parent, view, position, id) -> {
            Category selectedCategory = categories.get(position);
            selectCategory.setText(selectedCategory.getName());
            recurringExpense.setCategoryId(selectedCategory.getId());
            categoryDialog.dismiss();
        });

        categoryDialog.show();
    }

    private void updateRecurringExpense(RecurringExpense recurringExpense, int position) {
        recurringExpenseModel.updateRecurringExpense(recurringExpense); // Gọi phương thức update từ RecurringExpense
        recurringExpenses.set(position, recurringExpense); // Cập nhật danh sách
        notifyItemChanged(position); // Cập nhật RecyclerView
    }

    private void showDeleteConfirmationDialog(RecurringExpense recurringExpense, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmation of Deletion")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteRecurringExpense(recurringExpense.getId()); // Gọi phương thức xóa
                    recurringExpenses.remove(position); // Xóa khỏi danh sách
                    notifyItemRemoved(position); // Cập nhật RecyclerView
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void deleteRecurringExpense(int id) {
        recurringExpenseModel.deleteRecurringExpense(id); // Gọi phương thức xóa từ RecurringExpense
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView, amountTextView, startDateTextView, endDateTextView, repeatedChoiceTextView;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);
            repeatedChoiceTextView = itemView.findViewById(R.id.repeatedChoiceTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteAmountButton);
        }
    }
}
