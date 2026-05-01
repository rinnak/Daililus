package com.example.daililus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    public String selectedDate = null;
    public int selectedTab = 2;
    private final MutableLiveData<Boolean> updateTrigger = new MutableLiveData<>();
    private final MutableLiveData<String> userNameTrigger = new MutableLiveData<>();
    public LiveData<Boolean> getUpdateTrigger() {
        return updateTrigger;
    }
    public LiveData<String> getUserNameTrigger() {
        return userNameTrigger;
    }
    public void updateName(String newName) {
        userNameTrigger.setValue(newName);
    }


    public void addTask(DataBaseHelper dbHelper, String date, Task task, String userEmail){
        dbHelper.addTask(task, date, userEmail);
        updateTrigger.setValue(true);
    }

    public void updateTaskStatus(DataBaseHelper dbHelper, int id, boolean isDone){
        dbHelper.updateTaskStatus(id, isDone);
        updateTrigger.setValue(true);
    }

}
