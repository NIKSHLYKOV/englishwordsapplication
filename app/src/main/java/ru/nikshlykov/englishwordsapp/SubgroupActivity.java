package ru.nikshlykov.englishwordsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SubgroupActivity extends AppCompatActivity {

    private String EXTRA_SUBGROUP_ID = "SubgroupId";
    private String EXTRA_WORD_ID = "WordId";

    private final static String LOG_TAG = "SubgroupActivity";

    // Helper для работы с БД.
    private DatabaseHelper databaseHelper;
    Cursor wordsCursor;

    // View элементы.
    private ListView wordsList;
    private Button buttonForNewWordCreating;
    private CheckBox learnSubgroupCheckBox;

    // Полученные данные из Intent'а.
    private Bundle arguments;
    private long subgroupID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subgroup);

        // Получаем Exstras из Intent'а.
        arguments = getIntent().getExtras();
        if (arguments != null)
            subgroupID = arguments.getLong(EXTRA_SUBGROUP_ID);

        // Находим View элементы из разметки.
        viewElementsFinding();

        // Создаём Helper для работы с БД.
        databaseHelper = new DatabaseHelper(SubgroupActivity.this);

        // Присваеваем обработчик нажатия на элемент списка (слово), где открываем
        // WordActivity, передавая в него id данного слова.
        wordsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WordActivity.class);
                intent.putExtra(EXTRA_WORD_ID, id);
                startActivity(intent);
            }
        });

        // Присваиваем обработчик кнопке для создания нового слова.
        buttonForNewWordCreating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WordActivity.class);
                intent.putExtra(EXTRA_SUBGROUP_ID, subgroupID);
                startActivity(intent);
                //
                // СДЕЛАТЬ ЕЩЁ REQUEST И RESPONSE КОДЫ.
                //
            }
        });

        // Присваиваем чекбоксу изучения значение, находящееся в БД.
        // Делаем это до обработчика нажатся, чтобы данные не перезаписывались лишний раз.
        learnSubgroupCheckBox.setChecked(isSugroupStudied());

        // Присваиваем обработчик нажатия на чекбокс изучения подгруппы.
        learnSubgroupCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Меняем данные по id подгруппы в БД.
                // Если флажок выставлен, то isStudied должен стать 1.
                // Если же флажок не выставлени, то isStudied должен стать 0.

                // Подготавливаем данные для обновления.
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ISSTUDIED, isChecked);
                // Обновляем запись данной подгруппы по id.
                int updCount = databaseHelper.update(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS, contentValues, "_id = ?",
                        new String[] { String.valueOf(subgroupID) });

                // Проверяем обновление данных в БД и выводим сообщение об его успешности или неуспешности.
                String result;
                if (updCount == 1){
                    result = "Обновление БД прошло успешно";
                }
                else {
                    result = "Обновление БД прошло неуспешно";
                }
                Toast.makeText(SubgroupActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });

        Log.d(LOG_TAG, "OnCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*// Открываем подключение.
        try {
            databaseHelper.openDataBaseToRead();
        } catch (SQLException sqle) {
            throw sqle;
        }*/


        if (subgroupID > 0) {
            // Выполняем запрос на получение слов из данной подгруппы.
            wordsCursor = databaseHelper.rawQuery("Select " + DatabaseHelper.WordsTable.TABLE_WORDS + ".*" +
                    " from " + DatabaseHelper.WordsTable.TABLE_WORDS + ", " + DatabaseHelper.LinksTable.TABLE_LINKS +
                    " where " + DatabaseHelper.LinksTable.TABLE_LINKS + "." + DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_SUBGROUPID + "=" + subgroupID + " and " +
                    DatabaseHelper.LinksTable.TABLE_LINKS + "." + DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_WORDID + "=" + DatabaseHelper.WordsTable.TABLE_WORDS + "." + DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID);
            wordsCursor.moveToFirst();

            // Определяем столбцы, которые будут выводиться.
            String[] headers = new String[]{DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_WORD, DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_VALUE, DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_TRANSCRIPTION};
            // Создаем адаптер, передаем в него курсор, который хранит результат запроса.
            SimpleCursorAdapter userAdapter = new SimpleCursorAdapter(this, R.layout.subgroup_item,
                    wordsCursor, headers, new int[]{R.id.subgroup_item___TextView___word, R.id.subgroup_item___TextView___value, R.id.subgroup_item___TextView___transcription}, 0);
            wordsList.setAdapter(userAdapter);


        } else {
            Toast toast = Toast.makeText(SubgroupActivity.this, "Произошла ошибка", Toast.LENGTH_LONG);
            toast.show();
        }
        Log.d(LOG_TAG, "OnResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //databaseHelper.close();

        Log.d(LOG_TAG, "OnStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wordsCursor.close();
        Log.d(LOG_TAG, "OnDestroy");
    }

    private void viewElementsFinding(){
        wordsList = findViewById(R.id.activity_subgroup___ListView___words);
        buttonForNewWordCreating = findViewById(R.id.activity_subgroup___Button___new_word);
        learnSubgroupCheckBox = findViewById(R.id.activity_subgroup___CheckBox___study_subgroup);
    }

    boolean isSugroupStudied (){
        // Находим запись данной подгруппы в таблице подгрупп.
        Cursor subgroupCursor = databaseHelper.getSubroupByID(subgroupID);
        subgroupCursor.moveToFirst();
        // Получаем значение IsStudied.
        int isStudied = 0;
        try {
            isStudied = Integer.parseInt(subgroupCursor.getString(subgroupCursor.getColumnIndex(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ISSTUDIED)));
        }
        catch (NumberFormatException e) {
            Toast.makeText(SubgroupActivity.this, "Извините, произошла ошибка с базой данных", Toast.LENGTH_SHORT).show();
        }
        // Возвращаем значение типа boolean.
        return isStudied == 1;
    }

}
