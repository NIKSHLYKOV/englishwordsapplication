package ru.nikshlykov.englishwordsapp.ui.modes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;

public class ModesActivity extends AppCompatActivity {

    private static final String LOG_TAG = "ModesActivity";

    public static final String EXTRA_SELECTED_MODES = "SelectedModes";

    // ViewModel для работы с БД.
    @Inject
    public ModesViewModel modesViewModel;

    private ModesRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App)getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");
        setContentView(R.layout.activity_modes);

        // Инициализируем ViewModel для работы с БД.
        modesViewModel = new ViewModelProvider(this).get(ModesViewModel.class);

        initRecyclerViewWithAdapter();

        initSaveButton();
    }

    private void initRecyclerViewWithAdapter() {
        // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
        RecyclerView modesRecyclerView = findViewById(R.id.activity_modes___RecyclerView);
        modesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ModesRecyclerViewAdapter(this);
        modesRecyclerView.setAdapter(adapter);

        modesViewModel.getLiveDataModes().observe(this, new Observer<List<Mode>>() {
            @Override
            public void onChanged(List<Mode> modes) {
                if (modes != null) {
                    adapter.setModes(modes);
                }
            }
        });
    }

    private void initSaveButton() {
        MaterialButton saveMaterialButton = findViewById(R.id.activity_modes___material_button___save_modes);
        saveMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Получаем режимы из адаптера.
                List<Mode> modes = adapter.getModes();

                // Обновляем режимы в БД.
                modesViewModel.updateModes(modes);

                // Подготавливаем массив id выбранных режимов.
                ArrayList<Mode> selectedModes = new ArrayList<>();
                for (Mode mode: modes){
                    if (mode.isSelected == 1){
                        selectedModes.add(mode);
                    }
                }
                long[] selectedModesIds = new long[selectedModes.size()];
                for (int i = 0; i < selectedModes.size(); i++){
                    selectedModesIds[i] = selectedModes.get(i).id;
                }

                // Отправляем id выбранных режимов в MainActivity.
                Intent selectedModesData = new Intent();
                selectedModesData.putExtra(EXTRA_SELECTED_MODES, selectedModesIds);
                setResult(RESULT_OK, selectedModesData);
                finish();
            }
        });
    }
}
