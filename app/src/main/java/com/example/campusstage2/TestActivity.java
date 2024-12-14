//package com.example.campusstage2;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;
//
//import com.example.campusstage2.model.RecurringExpense;
//
//import java.time.LocalDate;
//
//public class TestActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        // Tạo công việc OneTimeWorkRequest
//        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RecurringExpenseWorker.class)
//                .setInputData(new androidx.work.Data.Builder().putString("user_id", "1").build()) // Truyền user_id thực tế
//                .build();
//
//        // Chạy công việc ngay lập tức
//        WorkManager.getInstance(this).enqueue(workRequest);
//    }
//
//}
