package com.example.campusstage2.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.campusstage2.Auth;
import com.example.campusstage2.LoginActivity;
import com.example.campusstage2.R;
import com.example.campusstage2.RecurringExpenseActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private Button logoutButton, recurringExpenseButton;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView nameTextView, usernameTextView, emailTextView, phoneTextView;
    private Auth auth;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        logoutButton = view.findViewById(R.id.logoutButton);
        recurringExpenseButton = view.findViewById(R.id.recurringExpenseButton);
        nameTextView = view.findViewById(R.id.userNameTextView);
        usernameTextView = view.findViewById(R.id.userUsernameTextView);
        emailTextView = view.findViewById(R.id.userEmailTextView);
        phoneTextView = view.findViewById(R.id.userPhoneTextView);

        auth = new Auth(getContext());
        setUserInfo();
        // Set up logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth auth = new Auth(getContext());
                auth.logout(); // Perform logout
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                view.getContext().startActivity(intent); // Navigate to LoginActivity
            }
        });

        // Set up recurring expense button
        recurringExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecurringExpenseActivity.class);
                startActivity(intent); // Navigate to RecurringExpensesActivity
            }
        });

        return view;
    }
    private void setUserInfo() {
        nameTextView.setText("Name: " + auth.getName());
        usernameTextView.setText("Username: " + auth.getUsername());
        emailTextView.setText("Email: " + auth.getEmail());
        phoneTextView.setText("Phone: " + auth.getPhone());
    }
}
