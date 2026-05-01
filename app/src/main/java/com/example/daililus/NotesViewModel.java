package com.example.daililus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class NotesViewModel extends ViewModel {
    private final MutableLiveData<Boolean> updateTrigger = new MutableLiveData<>();

    public LiveData<Boolean> getUpdateTrigger() {
        return updateTrigger;
    }

    public void triggerUpdate(){
        updateTrigger.setValue(true);
    }

    public List<Note> getNotes(DataBaseHelper dbHelper, String userEmail){
        return dbHelper.getAllNotes(userEmail);
    }

}
