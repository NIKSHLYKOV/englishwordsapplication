package ru.nikshlykov.englishwordsapp;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import static ru.nikshlykov.englishwordsapp.ModesFragment.modes;

public class DatabaseHelper extends SQLiteOpenHelper {


    // путь к базе данных вашего приложения
    private static String DB_PATH = "/data/data/ru.nikshlykov.englishwordsapp/databases/";
    private static String DB_NAME = "words.db";
    private static SQLiteDatabase myDataBase;
    private final Context mContext;
    private final static String LOG_TAG = "DatabaseHelper";

    public static class WordsTable {
        // Названия таблицы слов и её колонок
        static final String TABLE_WORDS = "Words";
        static final String TABLE_WORDS_COLUMN_ID = "_id";
        static final String TABLE_WORDS_COLUMN_WORD = "Word";
        static final String TABLE_WORDS_COLUMN_VALUE = "Value";
        static final String TABLE_WORDS_COLUMN_TRANSCRIPTION = "Transcription";
        static final String TABLE_WORDS_COLUMN_LEARNPROGRESS = "LearnProgress";
        static final String TABLE_WORDS_COLUMN_ISLEARNED = "IsLearned";
        static final String TABLE_WORDS_COLUMN_PARTOFSPEECH = "PartOfSpeech";
        static final String TABLE_WORDS_COLUMN_LASTREPETITIONDATE = "LastRepetitionDate";
        static final String TABLE_WORDS_COLUMN_EXAMPLES = "Examples";
    }

    public static class LinksTable {
        // Названия таблицы связей и её колонок
        static final String TABLE_LINKS = "Links";
        static final String TABLE_LINKS_COLUMN_WORDID = "WordID";
        static final String TABLE_LINKS_COLUMN_SUBGROUPID = "SubgroupID";
        static final String TABLE_LINKS_COLUMN_LEVELINPARENTGROUP = "LevelInParentGroup";
    }

    public static class SubgroupsTable {
        // Названия таблицы подгрупп и её колонок
        static final String TABLE_SUBGROUPS = "Subgroups";
        static final String TABLE_SUBGROUPS_COLUMN_ID = "_id";
        static final String TABLE_SUBGROUPS_COLUMN_SUBGROUPNAME = "SubgroupName";
        static final String TABLE_SUBGROUPS_COLUMN_PARENTGROUPID = "ParentGroupID";
        static final String TABLE_SUBGROUPS_COLUMN_ISSTUDIED = "IsStudied";
    }

    public static class GroupsTable {
        // Названия таблицы групп и её колонок
        static final String TABLE_GROUPS = "Groups";
        static final String TABLE_GROUPS_COLUMN_ID = "_id";
        static final String TABLE_GROUPS_COLUMN_GROUPNAME = "GroupName";
    }

    public static final class ModesTable {
        // Названия таблицы режимов и её колонок.
        static final String TABLE_NAME = "Modes";
        static final String COLUMN_ID = "_id";
        static final String COLUMN_MODENAME = "ModeName";
        static final String COLUMN_ISSELECTED = "IsSelected";
        static final String COLUMN_IMAGEID = "ImageResourceId";
    }


    /**
     * Конструктор
     * Принимает и сохраняет ссылку на переданный контекст для доступа к ресурсам приложения
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    /**
     * Создает пустую базу данных и перезаписывает ее нашей собственной базой
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (dbExist) {
            //ничего не делать - база уже есть
        } else {
            //вызывая этот метод создаем пустую базу, позже она будет перезаписана
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
     *
     * @return true если существует, false если не существует
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //база еще не существует
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null; // Упростил от return checkDB != null ? true : false;
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     */
    private void copyDataBase() throws IOException {
        //Открываем локальную БД как входящий поток
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        //Путь ко вновь созданной БД
        String outFileName = DB_PATH + DB_NAME;

        //Открываем пустую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    // Методы открытия БД.
    private static void openDataBaseToRead() throws SQLException {
        if (myDataBase == null) {
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        Log.d(LOG_TAG, "Open to read");
    }

    private static void openDataBaseToReadAndWrite() throws SQLException {
        if (myDataBase == null) {
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        Log.d(LOG_TAG, "Open to read and write");
    }

    // Метод закрытия БД.
    @Override
    public synchronized void close() {
        if (myDataBase != null)
            super.close();
        Log.d(LOG_TAG, "Close");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public static int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        Log.d(LOG_TAG, "update");
        openDataBaseToReadAndWrite();
        int updatedLinesCount = myDataBase.update(table, values, whereClause, whereArgs);
        myDataBase.close();
        myDataBase = null;
        return updatedLinesCount;
    }

    public static int[] updateModesInDb() {
        openDataBaseToReadAndWrite();
        int updatedLinesCount = 0;
        int selected = 0;
        int notSelected = 0;
        for (int i = 0; i < ModesActivity.modes.size(); i++) {
            ContentValues contentValues = new ContentValues();
            Mode mode = ModesActivity.modes.get(i);
            String modeId = String.valueOf(mode.getId());
            if (mode.getIsSelected()){
                selected++;
                contentValues.put(ModesTable.COLUMN_ISSELECTED, "1");
            }
            else {
                notSelected++;
                contentValues.put(ModesTable.COLUMN_ISSELECTED, "0");
            }
            updatedLinesCount += myDataBase.update(ModesTable.TABLE_NAME, contentValues, "_id = ?", new String[] {modeId});
        }
        myDataBase.close();
        myDataBase = null;

        int[] returned = new int[3];
        returned[0] = updatedLinesCount;
        returned[1] = selected;
        returned[2] = notSelected;

        return returned;

        /*ArrayList<String> selectedModesIds = new ArrayList<>();
        ArrayList<String> notSelectedModesIds = new ArrayList<>();

        for (int i = 0; i < modes.size(); i++) {
            Mode mode = modes.get(i);
            String modeId = String.valueOf(mode.getId());
            if (mode.getIsSelected())
                selectedModesIds.add(modeId);
            else
                notSelectedModesIds.add(modeId);
        }

        String[] arraySelectedModesIds = String[];

        ContentValues selectedModesContentValues = new ContentValues();
        selectedModesContentValues.put(ModesTable.COLUMN_ISSELECTED, "1");

        ContentValues notSelectedModesContentValues = new ContentValues();
        notSelectedModesContentValues.put(ModesTable.COLUMN_ISSELECTED, "0");


        openDataBaseToReadAndWrite();
        myDataBase.update(ModesTable.TABLE_NAME, selectedModesContentValues, "_id = ?", selectedModesIds)
        myDataBase.update()
        myDataBase.close();
        myDataBase = null;
        return updatedLinesCount;*/
    }

    public static long insert(String table, String nullColumnHack, ContentValues values) {
        Log.d(LOG_TAG, "insert");
        openDataBaseToReadAndWrite();
        long newWordId = myDataBase.insert(table, nullColumnHack, values);
        myDataBase.close();
        myDataBase = null;
        return newWordId;
    }

    public static long delete(String table, String whereClause, String[] whereArgs) {
        return myDataBase.delete(table, whereClause, whereArgs);
    }


    // Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
    // вы можете возвращать курсоры через "return myDataBase.query(....)", это облегчит их использование
    // в создании адаптеров для ваших view

    public Cursor rawQuery(String query) {
        Log.d(LOG_TAG, "rawQuery");
        openDataBaseToReadAndWrite();
        Cursor cursor = myDataBase.rawQuery(query, null);
        close();
        return cursor;
    }

    /**
     * Возращает Cursor со всеми группами из БД.
     */
    public Cursor getGroups() {
        return rawQuery("select * from " + GroupsTable.TABLE_GROUPS);
    }

    /**
     * Возращает Cursor со всеми подгруппами, включёнными в группу,
     * ID которой равен groupID.
     */
    public Cursor getSubgroupsFromGroup(int groupID) {
        return rawQuery("SELECT * FROM " + SubgroupsTable.TABLE_SUBGROUPS
                + " WHERE " + SubgroupsTable.TABLE_SUBGROUPS_COLUMN_PARENTGROUPID + "=" + groupID);
    }

    public Cursor getSubroupByID(long id) {
        return rawQuery("SELECT * FROM " + SubgroupsTable.TABLE_SUBGROUPS +
                " WHERE " + SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ID + "=" + id);
    }

    public Cursor getModes() {
        return rawQuery("SELECT * FROM " + ModesTable.TABLE_NAME);
    }

    public Cursor getStudiedSubgroups()
    {
        return rawQuery("SELECT * FROM " + SubgroupsTable.TABLE_SUBGROUPS +
                " WHERE " + SubgroupsTable.TABLE_SUBGROUPS_COLUMN_ISSTUDIED + "=" + "1");
    }
    public Cursor getSelectedModes(){
        return rawQuery("SELECT * FROM " + ModesTable.TABLE_NAME +
                " WHERE " + ModesTable.COLUMN_ISSELECTED + "=" + "1");
    }

    public Cursor getWordById(long id){
        return rawQuery("SELECT * FROM " + WordsTable.TABLE_WORDS +
                " WHERE " + WordsTable.TABLE_WORDS_COLUMN_ID + "=" + id);
    }

    public HashSet<Integer> getLinkedSubgroupsIds(long wordId){
        Cursor cursor = rawQuery("SELECT * FROM " + LinksTable.TABLE_LINKS +
                " WHERE " + LinksTable.TABLE_LINKS_COLUMN_WORDID + "=" + wordId);
        if (cursor.moveToFirst())
        {
            HashSet<Integer> subgroupsIds = new HashSet<Integer>(cursor.getCount());
            do {
                subgroupsIds.add(cursor.getInt(cursor.getColumnIndex(LinksTable.TABLE_LINKS_COLUMN_SUBGROUPID)));
            } while (cursor.moveToNext());
            return subgroupsIds;
        }
        return null;
    }
}
