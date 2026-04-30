package com.example.daililus;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setItemIconTintList(null);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if(id == R.id.nav_home){
                selectedFragment = new HomeFragment();
            }
            else if (id == R.id.nav_notes){
//                selectedFragment = new NotesFragment();
            }
            else if (id == R.id.nav_profile){
//                selectedFragment = new ProfileFragment();
            }
            else if (id == R.id.nav_add){
                AddTaskBottomSheet addSheet = new AddTaskBottomSheet();
                addSheet.show(getSupportFragmentManager(), "AddTask");
                return false;
            }

            if (selectedFragment != null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

    }
}