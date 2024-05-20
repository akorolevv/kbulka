package com.example.kbulka;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BodyActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.body_activity); // Используем созданный макет

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_uchet_vypuska) {
                selectedFragment = new FragmentUchetVypuska();
            } else if (itemId == R.id.nav_uchet_realizacii) {
                selectedFragment = new FragmentUchetRealizacii();
            } else if (itemId == R.id.nav_analiz_dannyh) {
                selectedFragment = new FragmentAnalizDannyh();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Загружаем начальный фрагмент (например, FragmentUchetVypuska)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FragmentUchetVypuska())
                .commit();
    }
}