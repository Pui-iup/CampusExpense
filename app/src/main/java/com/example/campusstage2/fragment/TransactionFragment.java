package com.example.campusstage2.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusstage2.Adapter.ExpenseAdapter;
import com.example.campusstage2.DatabaseHelper;
import com.example.campusstage2.R;
import com.example.campusstage2.Auth;
import com.example.campusstage2.model.Expense;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends Fragment {
    private RecyclerView expenseRecyclerView;
    private Auth auth;

    // Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TransactionFragment() {
        // Required empty public constructor
    }

    public static TransactionFragment newInstance(String param1, String param2) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        auth = new Auth(getContext()); // Initialize auth
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        expenseRecyclerView = view.findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Expense> expenses = loadExpenses();
        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        expenseRecyclerView.setAdapter(adapter);
        return view;
    }

    private List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = new DatabaseHelper(getContext()).getReadableDatabase();
        int userId = auth.getUserId(); // Get the user ID of the current user
        String query = "SELECT e.id, e.amount, e.category_id, e.user_id, e.date, c.name AS category_name " +
                "FROM expense e " +
                "LEFT JOIN categories c ON e.category_id = c.id " +
                "WHERE e.user_id = ?"; // Filter by user_id

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow("amount"));
                Integer categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                Integer userIdFromCursor = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

                Expense expense = new Expense(id, amount, categoryId, userIdFromCursor, date);
                expense.setCategoryName(categoryName);
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenses;
    }
}
