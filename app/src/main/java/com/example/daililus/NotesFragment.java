package com.example.daililus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NotesFragment extends Fragment {

    private NotesViewModel viewModel;
    private RecyclerView rvNotes;
    private NoteAdapter adapter;
    private DataBaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        dbHelper = new DataBaseHelper(getContext());
        rvNotes = view.findViewById(R.id.rvNotes);
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NoteAdapter();
        rvNotes.setAdapter(adapter);

        viewModel.getUpdateTrigger().observe(getViewLifecycleOwner(), tick -> {
            loadNotes();
        });
        loadNotes();
    }

    public void loadNotes(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && dbHelper != null && adapter != null){
            String email = user.getEmail();
            adapter.setNotes(dbHelper.getAllNotes(email));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadNotes();
    }
}