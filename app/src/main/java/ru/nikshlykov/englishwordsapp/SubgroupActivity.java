package ru.nikshlykov.englishwordsapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubgroupActivity extends AppCompatActivity {

    public static final String EXTRA_SUBGROUP_ID = "SubgroupId";

    private static final String LOG_TAG = "SubgroupActivity";

    private static final String DIALOG_SORTWORDS = "SortWordsDialogFragment";
    private static final String DIALOG_RESETWORDSPROGRESS = "ResetWordsProgressDialogFragment";
    private static final String DIALOG_DELETEWORDS = "DeleteWordsDialogFragment";

    // Helper для работы с БД.
    private DatabaseHelper databaseHelper;
    Cursor wordsCursor;

    // View элементы.
    private Button buttonForNewWordCreating;
    private CheckBox learnSubgroupCheckBox;
    private Toolbar toolbar;

    // RecyclerView и вспомогательные элементы.
    private RecyclerView recyclerView;
    private WordsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DividerItemDecoration dividerItemDecoration;

    // Для хранения слов текущей подгруппы.
    public static ArrayList<Word123> word123s;

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
        else
            finish();

        // Находим View элементы из разметки.
        viewElementsFinding();

        // Установка тулбара.
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Создаём Helper для работы с БД.
        databaseHelper = new DatabaseHelper(SubgroupActivity.this);

        // Присваиваем обработчик кнопке для создания нового слова.
        buttonForNewWordCreating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WordActivity.class);
                intent.putExtra(EXTRA_SUBGROUP_ID, subgroupID);
                startActivity(intent);
                // СДЕЛАТЬ ЕЩЁ REQUEST И RESPONSE КОДЫ.
                // СДЕЛАТЬ ЕЩЁ REQUEST И RESPONSE КОДЫ.
                // СДЕЛАТЬ ЕЩЁ REQUEST И RESPONSE КОДЫ.
            }
        });

        // Присваиваем чекбоксу изучения значение, находящееся в БД.
        // Делаем это до обработчика нажатся, чтобы данные не перезаписывались лишний раз.
        learnSubgroupCheckBox.setChecked(isSubgroupStudied());
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


        if (subgroupID > 0) {
            layoutManager = new LinearLayoutManager(SubgroupActivity.this);
            dividerItemDecoration = new DividerItemDecoration(SubgroupActivity.this, DividerItemDecoration.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(dividerItemDecoration);
        } else {
            Toast.makeText(SubgroupActivity.this, "Произошла ошибка", Toast.LENGTH_LONG).show();
            finish();
        }


        Log.d(LOG_TAG, "OnCreate");
    }

    @Override
    protected void onResume() {
        // Инициализируем список слов и заполняем его данными.
        word123s = new ArrayList<>();
        setWordsFromDbToWordsArrayList();
        adapter = new WordsRecyclerViewAdapter(SubgroupActivity.this, word123s);
        recyclerView.setAdapter(adapter);
        super.onResume();
        Log.d(LOG_TAG, "OnResume");
    }

    private void viewElementsFinding(){
        buttonForNewWordCreating = findViewById(R.id.activity_subgroup___Button___new_word);
        learnSubgroupCheckBox = findViewById(R.id.activity_subgroup___CheckBox___study_subgroup);
        toolbar = findViewById(R.id.activity_subgroup___Toolbar___toolbar);
        recyclerView = findViewById(R.id.activity_subgroup___RecyclerView___words);
    }

    boolean isSubgroupStudied(){
        // Находим запись данной подгруппы в таблице подгрупп.
        Cursor subgroupCursor = databaseHelper.getSubroupByID(subgroupID);
        subgroupCursor.moveToFirst();
        // Получаем значение IsStudied.
        int isStudied = 0;
        try {
            isStudied = Integer.parseInt(subgroupCursor.getString(subgroupCursor.getColumnIndex(DatabaseHelper.SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ISSTUDIED)));
        }
        catch (NumberFormatException e) {
            Toast.makeText(SubgroupActivity.this, "Нельзя преобразовать ячейку из БД в число", Toast.LENGTH_SHORT).show();
        }
        // Возвращаем значение типа boolean.
        return isStudied == 1;
    }

    private void setWordsFromDbToWordsArrayList() {
        Word123 newWord123;
        Cursor wordsCursor;
        // Получаем курсор со всеми словами из подгруппы из БД.
        wordsCursor = databaseHelper.rawQuery("Select " + DatabaseHelper.WordsTable.TABLE_WORDS + ".*" +
                " from " + DatabaseHelper.WordsTable.TABLE_WORDS + ", " + DatabaseHelper.LinksTable.TABLE_LINKS +
                " where " + DatabaseHelper.LinksTable.TABLE_LINKS + "." + DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_SUBGROUPID + "=" + subgroupID + " and " +
                DatabaseHelper.LinksTable.TABLE_LINKS + "." + DatabaseHelper.LinksTable.TABLE_LINKS_COLUMN_WORDID + "=" + DatabaseHelper.WordsTable.TABLE_WORDS + "." + DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID);
        // Проверяем, что курсор не пуст и проходим по нему, доставая данные.
        if (wordsCursor.moveToFirst()) {
            do {
                // Получаем из элемента курсора id, имя, параметр выбора и id изображения.
                String word = wordsCursor.getString(wordsCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_WORD));
                String transcription = wordsCursor.getString(wordsCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_TRANSCRIPTION));
                String value = wordsCursor.getString(wordsCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_VALUE));
                long id = wordsCursor.getLong(wordsCursor.getColumnIndex(DatabaseHelper.WordsTable.TABLE_WORDS_COLUMN_ID));
                // Создаём новый объект режима по полученным из курсора данным и добавляем его в нашу коллекцию.
                newWord123 = new Word123(word, transcription, value, id);
                word123s.add(newWord123);
            } while (wordsCursor.moveToNext());
        }
        else
            // Выводим сообщение, если полученный курсор режимов по какой-то причине пуст.
            Toast.makeText(this, "Курсор пуст, что-то пошло не так!", Toast.LENGTH_LONG).show();
        // Закрываем курсор с режимами.
        wordsCursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_subgroup_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();

        // Адаптировать под группу
        // !!!!!!!!!!!!!!!!!!!!!!!!!!

        switch (item.getItemId()){
            // Сортировка слов по алфавиту или сложности.
            case R.id.activity_subgroup___action___sort:
                Log.d(LOG_TAG, "sort word123s");
                SortWordsDialogFragment sortWordsDialogFragment = new SortWordsDialogFragment();
                sortWordsDialogFragment.show(manager, DIALOG_SORTWORDS);
                return true;
            // Сбрасывание прогресса слов данной подгруппы.
            case R.id.activity_subgroup___action___reset_words_progress:
                Log.d(LOG_TAG, "Reset word123s progress");
                /*ResetWordProgressDialogFragment resetWordProgressDialogFragment = new ResetWordProgressDialogFragment();
                resetWordProgressDialogFragment.show(manager, DIALOG_RESETWORDSPROGRESS);*/
                return true;
            // Удаление слов из данной подгруппы.
            case R.id.activity_subgroup___action___delete_words:
                Log.d(LOG_TAG, "Delete word123s");
                /*CopyWordDialogFragment copyWordDialogFragment = new CopyWordDialogFragment();
                copyWordDialogFragment.show(manager, DIALOG_DELETEWORDS);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
