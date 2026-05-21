package com.example.daililus;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class HomeFragment extends Fragment {
    private TextView tvUserName, tvCurrentDate;
    private ImageButton btnCalendar;
    private RecyclerView rvTasks;
    private DataBaseHelper dbHelper;
    private HomeViewModel viewModel;
    private TabLayout tabLayout;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        btnCalendar = view.findViewById(R.id.btnCalendar);
        rvTasks = view.findViewById(R.id.rvTasks);
        dbHelper = new DataBaseHelper(getContext());
        tabLayout = view.findViewById(R.id.tabLayout);

        if(viewModel.selectedDate == null){
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM, yyyy", new Locale("ru"));
            viewModel.selectedDate=sdf.format(new Date());
        }

        tvCurrentDate.setText(viewModel.selectedDate);

        loadUserName();
        setupTabs();
        setupRecyclerView();

        TabLayout.Tab savedTab = tabLayout.getTabAt(viewModel.selectedTab);
        if (savedTab != null) savedTab.select();

        btnCalendar.setOnClickListener(v -> showDatePicker());


        viewModel.getUserNameTrigger().observe(getViewLifecycleOwner(), newName -> {
            if (newName != null) tvUserName.setText(newName);
        });
        loadUserName();
        updateTaskList();
    }

    private void setupTabs(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.selectedTab = tab.getPosition();
                updateTaskList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void updateTaskList(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String email = user.getEmail();
        List<Task> allTasksForDate = dbHelper.getTaskByDate(viewModel.selectedDate, email);
        List<Task> filteredTasks = new ArrayList<>();

        for(Task task : allTasksForDate){
            if(viewModel.selectedTab == 2){
                filteredTasks.add(task);
            }
            else if (viewModel.selectedTab == 0 && !task.isDone()){
                filteredTasks.add(task);
            }
            else if (viewModel.selectedTab == 1 && task.isDone()){
                filteredTasks.add(task);
            }
        }
        if(adapter != null){
            adapter.setTasks(filteredTasks);
        }
    }
    private void loadUserName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String email = user.getEmail();
        new Thread(() -> {
            String name = dbHelper.getUserName(email);
            if (getActivity() != null){
                getActivity().runOnUiThread(() -> {
                    if (name != null) tvUserName.setText(name);
                });
            }
        }).start();
    }

    private void showDatePicker(){
        final Calendar c = Calendar.getInstance();

        if (viewModel.selectedDate != null){
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("d MMMM, yyyy", new Locale("ru"));
                Date date = sdf.parse(viewModel.selectedDate);
                if (date != null){
                    c.setTime(date);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year1, monthOfYear, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMMM, yyyy", new Locale("ru"));
                    String selectedDate = sdf.format(selectedCal.getTime());

                    viewModel.selectedDate = selectedDate;
                    tvCurrentDate.setText(selectedDate);
                    updateTaskList();
                }, year, month, day
                );
        datePickerDialog.show();
    }

    private void setupRecyclerView(){
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskAdapter(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskChanged(Task task, boolean isChecked) {
                viewModel.updateTaskStatus(dbHelper, task.getId(), isChecked);
                task.setDone(isChecked);
                updateTaskList();
            }

            @Override
            public void onTaskLongClick(Task task) {
                showDeleteTaskDialog(task);
            }
        });

        rvTasks.setAdapter(adapter);

    }

    private void showDeleteTaskDialog(Task task) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Удаление задачи")
                .setMessage("Вы уверены, что хотите удалить задачу \"" + task.getText() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        dbHelper.deleteTask(task.getId());

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                updateTaskList();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
    @Override
    public void onResume(){
        super.onResume();
        loadUserName();
        updateTaskList();
    }
}