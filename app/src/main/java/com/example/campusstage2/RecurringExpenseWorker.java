//package com.example.campusstage2;
//
//import android.content.Context;
//import android.util.Log;
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//import java.time.LocalDate;
//import java.util.List;
//import com.example.campusstage2.model.Expense;
//import com.example.campusstage2.model.RecurringExpense;
//
//public class RecurringExpenseWorker extends Worker {
//
//    private static final String TAG = "RecurringExpenseWorker";
//
//    public RecurringExpenseWorker(@NonNull Context context, @NonNull WorkerParameters params) {
//        super(context, params);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        performRecurringTransactions();
//        return Result.success();
//    }
//
//    private void performRecurringTransactions() {
//        Context context = getApplicationContext();
//        RecurringExpense recurringExpenseModel = RecurringExpense.getInstance(context);
//        String userId = getInputData().getString("user_id"); // Lấy user_id từ inputData
//
//        List<RecurringExpense> recurringExpenses = recurringExpenseModel.getRecurringExpenses(userId);
//        Log.d(TAG, "Retrieved recurring expenses: " + recurringExpenses.size()); // Log số lượng mục được lấy
//
//        LocalDate today = LocalDate.now();
//
//        for (RecurringExpense recurringExpense : recurringExpenses) {
//            int amount = (int) recurringExpense.getAmount();
//            int categoryId = recurringExpense.getCategoryId();
//            int userIdInt = Integer.parseInt(recurringExpense.getUserId());
//            String note = ""; // Sử dụng chuỗi rỗng cho note nếu không có
//
//            // Tạo một mục chi phí mới với ngày hiện tại
//            Expense expense = new Expense(context);
//            Log.d(TAG, "Inserting expense: amount=" + amount + ", categoryId=" + categoryId + ", userId=" + userIdInt);
//            expense.insertExpense(amount, categoryId, userIdInt, today.toString(), note); // Sử dụng ngày hiện tại
//        }
//    }
//
//}
//
//
//
//
//
