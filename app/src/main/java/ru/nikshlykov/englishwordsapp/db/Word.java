package ru.nikshlykov.englishwordsapp.db;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Words")
public class Word {

    public Word(@NonNull String word, String transcription, @NonNull String value) {
        this.word = word;
        this.transcription = transcription;
        this.value = value;
    }

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "_id")
    public long id; // ID слова в базе данных.

    @NonNull
    @ColumnInfo(name = "Word")
    public String word; // Само слово на английском.

    @ColumnInfo(name = "Transcription")
    public String transcription; // Транскрипция слова на английском.

    @NonNull
    @ColumnInfo(name = "Value")
    public String value; // Значения слова на русском языке.

    @NonNull
    @ColumnInfo(name = "LearnProgress", defaultValue = "0")
    public int learnProgress; // Прогресс изучения слова - количество правильных повторов, сделанных пользователем подряд.

    @NonNull
    @ColumnInfo(name = "IsLearned", defaultValue = "0")
    public int isLearned; // Переменная, показывающая выучено ли слово.

    @ColumnInfo(name = "PartOfSpeech")
    public String partOfSpeech; // Часть/Части речи слова???????????????????????????????????????????

    @NonNull
    @ColumnInfo(name = "LastRepetitionDate", defaultValue = "0")
    public int lastRepetitionDate; // Дата последнего повтора слова.

    public static class WordsTable{
        // Названия таблицы слов и её колонок
        public static final String TABLE_WORDS = "Words";
        public static final String TABLE_WORDS_COLUMN_ID = "_id";
        public static final String TABLE_WORDS_COLUMN_WORD = "Word";
        public static final String TABLE_WORDS_COLUMN_VALUE = "Value";
        public static final String TABLE_WORDS_COLUMN_TRANSCRIPTION = "Transcription";
        public static final String TABLE_WORDS_COLUMN_LEARNPROGRESS = "LearnProgress";
        public static final String TABLE_WORDS_COLUMN_ISLEARNED = "IsLearned";
        public static final String TABLE_WORDS_COLUMN_PARTOFSPEECH = "PartOfSpeech";
        public static final String TABLE_WORDS_COLUMN_LASTREPETITIONDATE = "LastRepetitionDate";
        public static final String TABLE_WORDS_COLUMN_EXAMPLES = "Examples";
        public static final String TABLE_WORDS_COLUMN_ID_FULLNAME = "Words._id";
        public static final String TABLE_WORDS_COLUMN_WORD_FULLNAME = "Words.Word";
        public static final String TABLE_WORDS_COLUMN_VALUE_FULLNAME = "Words.Value";
        public static final String TABLE_WORDS_COLUMN_TRANSCRIPTION_FULLNAME = "Words.Transcription";
        public static final String TABLE_WORDS_COLUMN_LEARNPROGRESS_FULLNAME = "Words.LearnProgress";
        public static final String TABLE_WORDS_COLUMN_ISLEARNED_FULLNAME = "Words.IsLearned";
        public static final String TABLE_WORDS_COLUMN_PARTOFSPEECH_FULLNAME = "Words.PartOfSpeech";
        public static final String TABLE_WORDS_COLUMN_LASTREPETITIONDATE_FULLNAME = "Words.LastRepetitionDate";
        public static final String TABLE_WORDS_COLUMN_EXAMPLES_FULLNAME = "Words.Examples";
    }
}
