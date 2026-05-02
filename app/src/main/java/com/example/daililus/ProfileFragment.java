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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onStart(){
        super.onStart();
        View btnAddNote = getActivity().findViewById(R.id.nav_add);
        if (btnAddNote != null) {
            btnAddNote.setEnabled(false);
            btnAddNote.setAlpha(0.5f);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        dbHelper = new DataBaseHelper(getContext());
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);
        ImageButton btnEditName = view.findViewById(R.id.btnEditName);
        View llPolicyContainer = view.findViewById(R.id.rPolicy);
        TextView tvPolicyDetails = view.findViewById(R.id.tvPolicyDetails);
        ImageView ivPolicyArrow = view.findViewById(R.id.ivArrowPolicy);

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


        view.findViewById(R.id.rlLanguage).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Функционал в разработке", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.btnAddAvatar).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Функционал в разработке", Toast.LENGTH_SHORT).show();
        });


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

        llPolicyContainer.setOnClickListener(v -> {
            boolean isVisible = tvPolicyDetails.getVisibility() == View.VISIBLE;
            if (isVisible) {
                tvPolicyDetails.setVisibility(View.GONE);
                ivPolicyArrow.animate().rotation(0).setDuration(300).start();
            } else {
                tvPolicyDetails.setVisibility(View.VISIBLE);
                ivPolicyArrow.animate().rotation(90).setDuration(300).start();
            }
        });

        return view;
    }

    @Override
    public void onStop(){
        super.onStop();
        View btnAddNote = getActivity().findViewById(R.id.nav_add);
        if (btnAddNote != null) {
            btnAddNote.setEnabled(true);
            btnAddNote.setAlpha(1.0f);
        }
    }
}