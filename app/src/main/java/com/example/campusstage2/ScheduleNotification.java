package com.example.campusstage2;

import android.app.Application;
import androidx.work.*;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ScheduleNotification extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        scheduleMorningNotification();
//        scheduleRecurringExpenseWorker();
    }

    private void scheduleMorningNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 9);
        calendar.set(Calendar.SECOND, 0);

        long currentTime = System.currentTimeMillis();
        long initialDelay = calendar.getTimeInMillis() - currentTime;
        if (initialDelay < 0) {
            initialDelay += TimeUnit.DAYS.toMillis(1); // Thêm 1 ngày nếu đã qua giờ 6 giờ sáng hôm nay
        }

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                1, TimeUnit.DAYS
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "morning_notification",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
        );
    }
//    private void scheduleRecurringExpenseWorker() {
//        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
//                RecurringExpenseWorker.class,
//                1, TimeUnit.DAYS
//        ).build();
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//                "recurring_expense_worker",
//                ExistingPeriodicWorkPolicy.REPLACE, workRequest
//        );
//    }
}
