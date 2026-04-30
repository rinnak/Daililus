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
    public int selectedTab = 0;
    private final MutableLiveData<Boolean> updateTrigger = new MutableLiveData<>();
    public LiveData<Boolean> getUpdateTrigger() {
        return updateTrigger;
    }

    public Map<String, List<Task>> taskByDate = new HashMap<>();

    public List<Task> getTaskByDate(DataBaseHelper dbHelper, String date){
        return dbHelper.getTaskByDate(date);
    }

    public void addTask(DataBaseHelper dbHelper, String date, Task task){
        dbHelper.addTask(task, date);
        updateTrigger.setValue(true);
    }

    public void updateTaskStatus(DataBaseHelper dbHelper, int id, boolean isDone){
        dbHelper.updateTaskStatus(id, isDone);
        updateTrigger.setValue(true);
    }

}
