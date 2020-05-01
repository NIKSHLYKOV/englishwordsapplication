package ru.nikshlykov.englishwordsapp.ui.subgroup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.nikshlykov.englishwordsapp.R;

public class AddOrEditSubgroupActivity extends AppCompatActivity {

    // Extra для возвращения имени подгруппы.
    public static final String EXTRA_SUBGROUP_NAME = "SubgroupName";

    // View элементы.
    Button confirmButton;
    EditText subgroupNameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subgroup);

        findViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String subgroupName = extras.getString(EXTRA_SUBGROUP_NAME, null);
            if (subgroupName != null){
                this.subgroupNameEditText.setText(subgroupName);
                confirmButton.setText(R.string.to_save);
            }
        }

        setConfirmButtonListener();
    }

    private void setConfirmButtonListener() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subgroupName = AddOrEditSubgroupActivity.this.subgroupNameEditText.getText().toString();
                // Проверяем, что поле названия группы не пустое.
                if (!subgroupName.isEmpty()) {
                    Intent subgroupData = new Intent();
                    subgroupData.putExtra(EXTRA_SUBGROUP_NAME, subgroupName);
                    setResult(RESULT_OK, subgroupData);
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
        subgroupNameEditText = findViewById(R.id.activity_new_subgroup___edit_text___group_name);
        confirmButton = findViewById(R.id.activity_new_subgroup___button___save_new_group);
    }
}
