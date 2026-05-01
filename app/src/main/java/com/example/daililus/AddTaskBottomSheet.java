package com.example.daililus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jspecify.annotations.NonNull;

public class AddTaskBottomSheet extends BottomSheetDialogFragment {
    private HomeViewModel viewModel;
    private DataBaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,  @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        EditText etTaskName = view.findViewById(R.id.etTaskName);
        Button btnSave = view.findViewById(R.id.btnSaveTask);
        dbHelper = new DataBaseHelper(getContext());

        btnSave.setOnClickListener(v -> {
            String text = etTaskName.getText().toString();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (!text.isEmpty()){
                String userEmail = user.getEmail();
                Task newTask = new Task(0, text, false);
                viewModel.addTask(dbHelper, viewModel.selectedDate, newTask, userEmail);
                dismiss();
            }
            else{
                Toast.makeText(getContext(), "Введите название задачи", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
