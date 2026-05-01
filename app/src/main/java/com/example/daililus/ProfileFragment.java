package com.example.daililus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private DataBaseHelper dbHelper;
    private String currentUserEmail;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        dbHelper = new DataBaseHelper(getContext());
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        TextView tvPolicy = view.findViewById(R.id.tvPolicy);
        ImageButton btnEditName = view.findViewById(R.id.btnEditName);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
            tvUserEmail.setText(currentUserEmail);
            String name = dbHelper.getUserName(currentUserEmail);
            tvUserName.setText(name != null ? name : "Пользователь");
        }

        if(currentUserEmail != null){
            String name = dbHelper.getUserName(currentUserEmail);
            tvUserName.setText(name != null ? name : "Пользователь");
            tvUserEmail.setText(currentUserEmail);
        }

        btnEditName.setOnClickListener(v -> {
            final EditText taskInput = new EditText(getContext());
            taskInput.setText(tvUserName.getText().toString());
            taskInput.setSelection(taskInput.getText().length());

            new AlertDialog.Builder(getContext())
                    .setTitle("Изменить имя")
                    .setView(taskInput)
                    .setPositiveButton("Сохранить", (dialog, which) -> {
                        String newName = taskInput.getText().toString().trim();
                        if(!newName.isEmpty()){
                            dbHelper.updateUserName(currentUserEmail, newName);
                            tvUserName.setText(newName);
                            homeViewModel.updateName(newName);
                        }

                    })
                    .setNegativeButton("Отмена", null).show();
        });

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }
}