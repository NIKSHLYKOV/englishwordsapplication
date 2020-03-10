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

    DatabaseHelper databaseHelper;

    // Список для хранения режимов.
    public static ArrayList<Mode123> mode123s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        databaseHelper = new DatabaseHelper(this);

        // Инициализируем ArrayList с режимами и добавляем в него режимы из БД.
        mode123s = new ArrayList<>();
        setModesFromDbToModesArrayList();

        // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
        modesRecyclerView = findViewById(R.id.activity_modes___RecyclerView);
        adapter = new ModeRecyclerViewAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        modesRecyclerView.setAdapter(adapter);
        modesRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int[]returned = databaseHelper.updateModesInDb();
        Toast.makeText(this, "Обновлено строк: " + returned[0] +
                "\n Выбранных режимов: " + returned[1] +
                "\n Невыбранных режимов: " + returned[2], Toast.LENGTH_LONG).show();
    }

    private void setModesFromDbToModesArrayList() {
        Mode123 mode123;
        Cursor modeCursor;
        // Получаем курсор со всеми режимами из БД.
        modeCursor = databaseHelper.getModes();
        // Проверяем, что курсор не пуст и проходим по нему, доставая данные.
        if (modeCursor.moveToFirst()) {
            do {
                // Получаем из элемента курсора id, имя, параметр выбора и id изображения.
                long modeId = modeCursor.getLong(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_ID));
                String modeName = modeCursor.getString(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_MODENAME));
                boolean modeIsSelected = 1 == modeCursor.getInt(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_ISSELECTED));
                String modeImageResourceId = modeCursor.getString(modeCursor.getColumnIndex(DatabaseHelper.ModesTable.COLUMN_IMAGEID));
                int integerModeImageResourceId = getResources().getIdentifier(modeImageResourceId, "drawable", this.getPackageName());
                // Создаём новый объект режима по полученным из курсора данным и добавляем его в нашу коллекцию.
                mode123 = new Mode123(modeId, modeName, modeIsSelected, integerModeImageResourceId);
                mode123s.add(mode123);
            } while (modeCursor.moveToNext());
        }
        else
            // Выводим сообщение, если полученный курсор режимов по какой-то причине пуст.
            Toast.makeText(this, "Курсор пуст, что-то пошло не так!", Toast.LENGTH_LONG).show();
        // Закрываем курсор с режимами.
        modeCursor.close();
    }
}
