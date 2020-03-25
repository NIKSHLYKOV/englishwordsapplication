package ru.nikshlykov.englishwordsapp.ui.modes;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.nikshlykov.englishwordsapp.R;

public class ModesActivity extends AppCompatActivity {

    // ViewModel для работы с БД.
    ModesViewModel modesViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        // Инициализируем ViewModel для работы с БД.
        modesViewModel = new ModesViewModel(getApplication());

        // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
        RecyclerView modesRecyclerView = findViewById(R.id.activity_modes___RecyclerView);
        ModeRecyclerViewAdapter adapter = new ModeRecyclerViewAdapter(this, modesViewModel.modes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        modesRecyclerView.setAdapter(adapter);
        modesRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStop() {
        super.onStop();
        modesViewModel.updateModes();
    }
}
