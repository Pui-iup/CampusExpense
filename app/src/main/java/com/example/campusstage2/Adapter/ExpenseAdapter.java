package com.example.campusstage2.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusstage2.R;
import com.example.campusstage2.model.Category;
import com.example.campusstage2.model.Expense;

import java.util.Calendar;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private final List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.amountTextView.setText("Amount: $" + expense.getAmount());
        holder.categoryTextView.setText("Category: " + expense.getCategoryName());
        holder.dateTextView.setText("Date: " + expense.getDate());
        holder.deleteExpense.setOnClickListener(view -> {
            Expense deleteExpense = new Expense(view.getContext());
            deleteExpense.deleteExpense(expense.getId());
            expenses.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(view.getContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.fragment_add, null);
            AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Update Expense")
                    .setView(dialogView)
                    .setPositiveButton("Update", null)
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            EditText amountInput = dialogView.findViewById(R.id.amountInput);
            EditText noteInput = dialogView.findViewById(R.id.noteInput);
            EditText dateInput = dialogView.findViewById(R.id.dateInput);
            TextView selectCategory = dialogView.findViewById(R.id.selectCategory);

            // Điền dữ liệu hiện tại
            TextView textView = dialogView.findViewById(R.id.textViewExpense);
            textView.setText("Update an Expense");

            amountInput.setText(String.valueOf(expense.getAmount()));
            noteInput.setText(expense.getNote());
            dateInput.setText(expense.getDate());
            selectCategory.setText(expense.getCategoryName());
            Button saveExpense = dialogView.findViewById(R.id.saveExpense);
            saveExpense.setVisibility(View.INVISIBLE);

            // Xử lý chọn category
            selectCategory.setOnClickListener(v -> {
                Category category = new Category(view.getContext());
                List<Category> categories = category.getAllCategories();
                View categoryDialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.category_list_view, null);
                ListView categoryListView = categoryDialogView.findViewById(R.id.categoryList);

                CategoryAdapter adapter = new CategoryAdapter(view.getContext(), categories);
                categoryListView.setAdapter(adapter);

                AlertDialog categoryDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Select Category")
                        .setView(categoryDialogView)
                        .create();

                categoryListView.setOnItemClickListener((parent, categoryView, categoryPosition, id) -> {
                    Category selectedCategory = categories.get(categoryPosition);
                    selectCategory.setText(selectedCategory.toString());
                    expense.setCategoryId(selectedCategory.getId());
                    expense.setCategoryName(selectedCategory.getName());
                    categoryDialog.dismiss();
                });

                categoryDialog.show();
            });

            // Xử lý chọn date
            dateInput.setOnTouchListener((v, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                            (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                dateInput.setText(selectedDate);
                                expense.setDate(selectedDate);
                            }, year, month, day);
                    datePickerDialog.show();
                }
                return true;
            });

            // Cập nhật khi nhấn nút "Update"
            dialog.setOnShowListener(dialogInterface -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String newAmount = amountInput.getText().toString().trim();
                    String newNote = noteInput.getText().toString().trim();

                    expense.setAmount(Integer.parseInt(newAmount));
                    expense.setNote(newNote);
                    expense.updateExpense();
                    expenses.set(position, expense);
                    notifyItemChanged(position);
                    dialog.dismiss();
                });
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView, categoryTextView, dateTextView;
        Button deleteExpense;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            deleteExpense = itemView.findViewById(R.id.deleteExpense);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
