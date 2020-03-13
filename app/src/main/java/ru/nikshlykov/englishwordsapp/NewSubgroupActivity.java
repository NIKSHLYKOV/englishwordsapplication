package ru.nikshlykov.englishwordsapp;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewSubgroupActivity extends AppCompatActivity {
    final long groupsForNewSubgroupsId = 21;

    // View элементы.
    Button creatingButton;
    EditText editText_groupName;

    // Элементы для работы с БД.
    DatabaseHelper databaseHelper;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subgroup);

        // Создаём объект DatabaseHelper и открываем подключение с базой.
        databaseHelper = new DatabaseHelper(this);

        // Находим editText, в котором будет прописываться название новой группы.
        editText_groupName = findViewById(R.id.activity_new_subgroup___edit_text___group_name);

        // Находим кнопку сохранения и присваиваем ей обработчик.
        creatingButton = findViewById(R.id.activity_new_subgroup___button___save_new_group);
        creatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = editText_groupName.getText().toString();
                // Проверяем, что поле названия группы не пустое.
                if (!groupName.isEmpty()) {
                    // Создаём объект ContentValues и вводим в него название подгруппы через пару ключ-значение.
                    contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME, groupName);
                    contentValues.put(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ISSTUDIED, 0);
                    contentValues.put(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_PARENTGROUPID, groupsForNewSubgroupsId);
                    // Добавляем подгруппу в таблицу групп.
                    databaseHelper.insert(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS, null, contentValues);
                    // Закрываем activity, возвращаясь к предыдущему.
                    finish();
                }
                // Выводим Toast о том, что поле названия не должно быть пустым.
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Необходимо указать название создаваемой группы", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }
}
