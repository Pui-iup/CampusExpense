package com.example.campusstage2.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
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
import com.example.campusstage2.model.Budget;
import com.example.campusstage2.model.Category;

import java.util.Calendar;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private final List<Budget> budgets;
    private final Context context;

    public BudgetAdapter(Context context, List<Budget> budgets) {
        this.context = context;
        this.budgets = budgets;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgets.get(position);

        holder.amountTextView.setText("Amount: $" + budget.getAmount());
        holder.remainingTextView.setText("Remaining: $" + budget.getRemaining());
        holder.categoryTextView.setText("Category: " + budget.getCategoryName());
        holder.startDateTextView.setText("Start Date: " + budget.getStartDate());
        holder.endDateTextView.setText("End Date: " + budget.getEndDate());

        holder.deleteBudget.setOnClickListener(view -> {
            Budget deletedBudget = new Budget(context);
            deletedBudget.deleteBudget(budget.getId());
            budgets.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Budget deleted", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.activity_add_budget, null);

            AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                    .setView(dialogView)
                    .setPositiveButton("Update", null)
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();

            TextView textViewAddBudget = dialogView.findViewById(R.id.textViewAddBudget);
            textViewAddBudget.setText("Update Budget");
            Button saveBudget = dialogView.findViewById(R.id.saveBudget);
            saveBudget.setVisibility(View.INVISIBLE);

            EditText amountEditText = dialogView.findViewById(R.id.amount);
            EditText startDateEditText = dialogView.findViewById(R.id.startDate);
            EditText endDateEditText = dialogView.findViewById(R.id.endDate);
            TextView selectCategoryTextView = dialogView.findViewById(R.id.selectCategory);

            amountEditText.setText(String.valueOf(budget.getAmount()));
            startDateEditText.setText(budget.getStartDate());
            endDateEditText.setText(budget.getEndDate());
            selectCategoryTextView.setText(budget.getCategoryName());

            selectCategoryTextView.setOnClickListener(v -> {
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
                categoryDialogView.findViewById(R.id.addCategory).setVisibility(View.INVISIBLE);

                categoryListView.setOnItemClickListener((parent, view1, position1, id) -> {
                    Category selectedCategory = categories.get(position1);
                    selectCategoryTextView.setText(selectedCategory.toString());
                    categoryDialog.dismiss();
                });

                categoryDialog.show();
            });

            startDateEditText.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showDatePickerDialog(view.getContext(), startDateEditText);
                }
                return true;
            });

            endDateEditText.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showDatePickerDialog(view.getContext(), endDateEditText);
                }
                return true;
            });

            dialog.setOnShowListener(dialogInterface -> {
                Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                updateButton.setOnClickListener(v -> {
                    String updatedAmount = amountEditText.getText().toString();
                    String updatedStartDate = startDateEditText.getText().toString();
                    String updatedEndDate = endDateEditText.getText().toString();
                    String updatedCategory = selectCategoryTextView.getText().toString();

                    if (updatedAmount.isEmpty() || updatedStartDate.isEmpty() || updatedEndDate.isEmpty() || updatedCategory.isEmpty()) {
                        Toast.makeText(view.getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    budget.setAmount(Integer.parseInt(updatedAmount));
                    budget.setStartDate(updatedStartDate);
                    budget.setEndDate(updatedEndDate);
                    budget.setCategoryName(updatedCategory);

                    budgets.set(position, budget);
                    notifyItemChanged(position);

                    Toast.makeText(view.getContext(), "Budget updated successfully.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            });

            dialog.show();
        });
    }

    private void showDatePickerDialog(Context context, EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        targetEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day);
        datePickerDialog.show();
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public void updateData(List<Budget> newBudgets) {
        this.budgets.clear();
        this.budgets.addAll(newBudgets);
        notifyDataSetChanged();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView, categoryTextView, startDateTextView, remainingTextView, endDateTextView;
        Button deleteBudget;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteBudget = itemView.findViewById(R.id.deleteBudget);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            remainingTextView = itemView.findViewById(R.id.remainingTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);
        }
    }
}
