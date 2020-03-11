package ru.nikshlykov.englishwordsapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModesActivity extends AppCompatActivity {

    // RecyclerView и вспомогательные элементы.
    private RecyclerView modesRecyclerView;
    private ModeRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ModesViewModel modesViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        modesViewModel = new ModesViewModel(getApplication());

        // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
        modesRecyclerView = findViewById(R.id.activity_modes___RecyclerView);
        adapter = new ModeRecyclerViewAdapter(this, modesViewModel.modes);
        layoutManager = new LinearLayoutManager(this);
        modesRecyclerView.setAdapter(adapter);
        modesRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modesViewModel.updateModes();
    }
}
