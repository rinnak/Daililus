package com.example.daililus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteBottomSheet extends BottomSheetDialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.layout_add_note, container, false);

        EditText etTitle = view.findViewById(R.id.etNoteTitle);
        Button btnSave = view.findViewById(R.id.btnSaveNote);
        DataBaseHelper dbHelper = new DataBaseHelper(getContext());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (!title.isEmpty()){
                String userEmail = user.getEmail();
                String currentDate = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date());
                dbHelper.addNote(title, currentDate, userEmail);
                NotesViewModel viewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
                viewModel.triggerUpdate();
                dismiss();
            }
        });
        return view;
    }
}
