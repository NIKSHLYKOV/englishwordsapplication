package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.nikshlykov.englishwordsapp.R;

public class AddSubgroupActivity extends AppCompatActivity {

    // Extra для возвращения имени новой подгруппы.
    public static final String EXTRA_NEW_SUBGROUP_NAME = "NewSubgroupName";

    // View элементы.
    Button creatingButton;
    EditText groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subgroup);

        findViews();

        initCreatingButton();
    }

    private void initCreatingButton() {
        creatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subgroupName = groupName.getText().toString();
                // Проверяем, что поле названия группы не пустое.
                if (!subgroupName.isEmpty()) {
                    Intent newSubgroupData = new Intent();
                    newSubgroupData.putExtra(EXTRA_NEW_SUBGROUP_NAME, subgroupName);
                    setResult(RESULT_OK, newSubgroupData);
                    finish();
                }
                // Выводим Toast о том, что поле названия не должно быть пустым.
                else {
                    Toast.makeText(getApplicationContext(), R.string.error_new_subgroup_empty_name,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void findViews(){
        groupName = findViewById(R.id.activity_new_subgroup___edit_text___group_name);
        creatingButton = findViewById(R.id.activity_new_subgroup___button___save_new_group);
    }
}
