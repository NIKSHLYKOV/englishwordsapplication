package ru.nikshlykov.englishwordsapp.ui.modes;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;

public class ModesActivity extends AppCompatActivity {

    // ViewModel для работы с БД.
    ModesViewModel modesViewModel;

    ModesRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
        RecyclerView modesRecyclerView = findViewById(R.id.activity_modes___RecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        modesRecyclerView.setLayoutManager(layoutManager);
        adapter = new ModesRecyclerViewAdapter(this);
        modesRecyclerView.setAdapter(adapter);

        // Инициализируем ViewModel для работы с БД.
        modesViewModel = new ViewModelProvider(this).get(ModesViewModel.class);
        modesViewModel.getLiveDataModes().observe(this, new Observer<List<Mode>>() {
            @Override
            public void onChanged(List<Mode> modes) {
                if (modes != null){
                    adapter.setModes(modes);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        modesViewModel.updateModes();
    }
}
