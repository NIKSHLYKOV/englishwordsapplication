package ru.nikshlykov.englishwordsapp.db;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.nikshlykov.englishwordsapp.App;
import ru.nikshlykov.englishwordsapp.db.example.Example;
import ru.nikshlykov.englishwordsapp.db.example.ExampleDao;
import ru.nikshlykov.englishwordsapp.db.group.Group;
import ru.nikshlykov.englishwordsapp.db.group.GroupDao;
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.link.LinkDao;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;
import ru.nikshlykov.englishwordsapp.db.repeat.RepeatDao;
import ru.nikshlykov.englishwordsapp.db.setting.SettingDao;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.db.word.WordDao;
import ru.nikshlykov.englishwordsapp.ui.groups.GroupItem;

import static ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment.TO_DELETE;
import static ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment.TO_LINK;

public class WordsRepository {

    private static final String LOG_TAG = WordsRepository.class.getCanonicalName();

    private ExampleDao exampleDao;
    private RepeatDao repeatDao;
    private WordDao wordDao;

    private ExecutorService databaseExecutorService;


    public WordsRepository(AppDatabase database) {

        exampleDao = database.exampleDao();
        repeatDao = database.repeatDao();
        wordDao = database.wordDao();

        databaseExecutorService = Executors.newFixedThreadPool(1);
    }


    public void execute(Runnable runnable){
        databaseExecutorService.execute(runnable);
    }



    /**
     * Методы для работы со словами.
     */
    public void insert(Word word, OnWordInsertedListener listener) {
        NewInsertWordAsyncTask task = new NewInsertWordAsyncTask(wordDao, listener);
        task.execute(word);
    }

    public void update(Word word, OnWordUpdatedListener listener) {
        new UpdateWordAsyncTask(wordDao, listener).execute(word);
    }

    public void delete(Word word) {
        new DeleteWordAsyncTask(wordDao).execute(word);
    }

    // МОЖНО ЛИ ТАК ПИСАТЬ??? (БЕЗ НОВОГО ПОТОКА)
    public Word getWordById(long id) {
        /*GetWordByIdAsyncTask task = new GetWordByIdAsyncTask(wordDao);
        task.execute(id);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;*/
        return wordDao.getWordById(id);
    }

    public LiveData<Word> getLiveDataWordById(long wordId) {
        return wordDao.getLiveDataWordById(wordId);
    }

    public void getWord(long wordId, OnWordLoadedListener listener) {
        GetWordAsyncTask task = new GetWordAsyncTask(wordDao, listener);
        task.execute(wordId);
    }

    public LiveData<List<Word>> getWordsFromStudiedSubgroups() {
        Log.i(LOG_TAG, "getWordsFromStudiedSubgroups");
        return wordDao.getAllLiveDataWordsFromStudiedSubgroups();
    }

    public LiveData<List<Word>> getWordsFromSubgroupByProgress(long subgroupId) {
        return wordDao.getWordsFromSubgroupByProgress(subgroupId);
    }

    public LiveData<List<Word>> getWordsFromSubgroupByAlphabet(long subgroupId) {
        return wordDao.getWordsFromSubgroupByAlphabet(subgroupId);
    }

    public void getAvailableToRepeatWord(boolean withNew, OnAvailableToRepeatWordLoadedListener listener) {
        GetAvailableToRepeatWordAsyncTask task
                = new GetAvailableToRepeatWordAsyncTask(wordDao, withNew, listener);
        task.execute();
    }

    // МОЖНО ЛИ ТАК ПИСАТЬ???
    public void resetWordsProgress(final long subgroupId) {
        execute(new Runnable() {
            @Override
            public void run() {
                List<Word> words = wordDao.getWordsFromSubgroup(subgroupId);
                for (Word word : words) {
                    word.learnProgress = -1;
                }
                wordDao.update(words);
            }
        });
    }

    /**
     * AsyncTasks для работы со словами.
     */
    public interface OnWordInsertedListener {
        void onInserted(long wordId);
    }
    private static class NewInsertWordAsyncTask extends AsyncTask<Word, Void, Long> {
        private WordDao wordDao;
        private WeakReference<OnWordInsertedListener> listener;

        private NewInsertWordAsyncTask(WordDao wordDao,
                                       OnWordInsertedListener listener) {
            this.wordDao = wordDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Long doInBackground(Word... words) {
            words[0].id = wordDao.getWordWithMinId().id - 1;
            words[0].learnProgress = -1;
            return wordDao.insert(words[0]);
        }

        @Override
        protected void onPostExecute(Long wordId) {
            super.onPostExecute(wordId);
            OnWordInsertedListener listener = this.listener.get();
            if (listener != null) {
                listener.onInserted(wordId);
            }
        }
    }

    public interface OnWordUpdatedListener {
        void onWordUpdated(int isUpdated);
    }
    private static class UpdateWordAsyncTask extends AsyncTask<Word, Void, Integer> {
        private WordDao wordDao;
        private WeakReference<OnWordUpdatedListener> listener;

        private UpdateWordAsyncTask(WordDao wordDao, OnWordUpdatedListener listener) {
            this.wordDao = wordDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Integer doInBackground(Word... words) {
            return wordDao.update(words[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            OnWordUpdatedListener listener = this.listener.get();
            if (listener != null) {
                listener.onWordUpdated(integer);
            }
        }
    }

    private static class DeleteWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private DeleteWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.delete(words[0]);
            return null;
        }
    }
    /*private static class GetWordByIdAsyncTask extends AsyncTask<Long, Void, Word> {
        private WordDao wordDao;

        private GetWordByIdAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Word doInBackground(Long... longs) {
            return wordDao.getWordById(longs[0]);
        }
    }*/
    public interface OnWordLoadedListener {
        void onLoaded(Word word);
    }
    private static class GetWordAsyncTask extends AsyncTask<Long, Void, Word> {
        private WordDao wordDao;
        private WeakReference<OnWordLoadedListener> listener;

        private GetWordAsyncTask(WordDao wordDao,
                                 OnWordLoadedListener listener) {
            this.wordDao = wordDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Word doInBackground(Long... longs) {
            return wordDao.getWordById(longs[0]);
        }

        @Override
        protected void onPostExecute(Word word) {
            super.onPostExecute(word);
            OnWordLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onLoaded(word);
            }
        }
    }

    public interface OnAvailableToRepeatWordLoadedListener {
        void onAvailableToRepeatWordLoaded(Word word);
    }
    private static class GetAvailableToRepeatWordAsyncTask extends AsyncTask<Void, Void, Word> {
        private WordDao wordDao;
        private boolean withNew;
        private WeakReference<OnAvailableToRepeatWordLoadedListener> listener;

        private GetAvailableToRepeatWordAsyncTask(WordDao wordDao, boolean withNew, OnAvailableToRepeatWordLoadedListener listener) {
            this.wordDao = wordDao;
            this.withNew = withNew;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Word doInBackground(Void... voids) {

            Word[] wordsFromStudiedSubgroups;
            if (withNew) {
                wordsFromStudiedSubgroups = wordDao.getAllWordsFromStudiedSubgroups();
            } else {
                wordsFromStudiedSubgroups = wordDao.getNotNewWordsFromStudiedSubgroups();
            }
            Date currentDate = new Date();
            for (Word word : wordsFromStudiedSubgroups) {
                if (word.isAvailableToRepeat(currentDate)) {
                    return word;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Word word) {
            super.onPostExecute(word);
            OnAvailableToRepeatWordLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onAvailableToRepeatWordLoaded(word);
            }
        }
    }



    /**
     * Методы для работы с повторами.
     */
    public void insert(Repeat repeat) {
        new InsertRepeatAsyncTask(repeatDao).execute(repeat);
    }

    public void delete(Repeat repeat) {
        new DeleteRepeatAsyncTask(repeatDao).execute(repeat);
    }

    // МОЖНО ЛИ ТАК ПИСАТЬ?
    public Repeat getLastRepeatByWord(long wordId) {
        /*GetLastRepeatByWordAsyncTask task = new GetLastRepeatByWordAsyncTask(repeatDao);
        task.execute(wordId);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;*/
        return repeatDao.getLastRepeatByWord(wordId);
    }

    public void getRepeatsCountForToday(OnRepeatsCountForTodayLoadedListener listener) {
        GetNewWordsFirstShowRepeatsCountForTodayAsyncTask task = new GetNewWordsFirstShowRepeatsCountForTodayAsyncTask(repeatDao, listener);
        task.execute();
    }

    public interface OnRepeatsCountForTodayLoadedListener {
        void onRepeatsCountForTodayLoaded(int repeatsCount);
    }

    /**
     * AsyncTasks для работы с повторами.
     */
    private static class InsertRepeatAsyncTask extends AsyncTask<Repeat, Void, Long> {
        private RepeatDao repeatDao;

        private InsertRepeatAsyncTask(RepeatDao repeatDao) {
            this.repeatDao = repeatDao;
        }

        @Override
        protected Long doInBackground(Repeat... repeats) {
            Repeat repeatToInsert = repeats[0];
            Repeat lastRepeat = repeatDao.getRepeatWithMaxId();
            long idForNewRepeat;
            if (lastRepeat != null)
                idForNewRepeat = lastRepeat.getId() + 1;
            else
                idForNewRepeat = 0L;
            repeatToInsert.setId(idForNewRepeat);
            return repeatDao.insert(repeats[0]);
        }
    }

    private static class DeleteRepeatAsyncTask extends AsyncTask<Repeat, Void, Integer> {
        private RepeatDao repeatDao;

        private DeleteRepeatAsyncTask(RepeatDao repeatDao) {
            this.repeatDao = repeatDao;
        }

        @Override
        protected Integer doInBackground(Repeat... repeats) {
            return repeatDao.delete(repeats[0]);
        }
    }
    /* private static class GetLastRepeatByWordAsyncTask extends AsyncTask<Long, Void, Repeat> {
         private RepeatDao repeatDao;

         private GetLastRepeatByWordAsyncTask(RepeatDao repeatDao) {
             this.repeatDao = repeatDao;
         }

         @Override
         protected Repeat doInBackground(Long... longs) {
             return repeatDao.getLastRepeatByWord(longs[0]);
         }
     }*/
    private static class GetNewWordsFirstShowRepeatsCountForTodayAsyncTask extends AsyncTask<Void, Void, Integer> {
        private RepeatDao repeatDao;
        private WeakReference<OnRepeatsCountForTodayLoadedListener> listener;

        private GetNewWordsFirstShowRepeatsCountForTodayAsyncTask(RepeatDao repeatDao,
                                                                  OnRepeatsCountForTodayLoadedListener listener) {
            this.repeatDao = repeatDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            ArrayList<Repeat> allRepeats = new ArrayList<>(repeatDao.getAllRepeats());
            Log.i(LOG_TAG, "Повторов за всё время: " + allRepeats.size());
            Calendar now = Calendar.getInstance();
            int newWordsFirstShowRepeatsCountForToday = 0;
            for (Repeat repeat : allRepeats) {
                if (repeat.getSequenceNumber() == 0) {
                    Calendar repeatDate = Calendar.getInstance();
                    repeatDate.setTime(new Date(repeat.getDate()));
                    if (repeatDate.get(Calendar.DATE) == now.get(Calendar.DATE))
                        newWordsFirstShowRepeatsCountForToday++;
                }
            }
            return newWordsFirstShowRepeatsCountForToday;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            OnRepeatsCountForTodayLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onRepeatsCountForTodayLoaded(integer);
            }
        }
    }



    /**
     * Методы для работы с примерами.
     */
    public void getExamplesByWordId(long wordId, OnExamplesLoadedListener listener) {
        new GetExamplesByWordAsyncTask(exampleDao, listener).execute(wordId);
    }

    public interface OnExamplesLoadedListener {
        void onLoaded(List<Example> examples);
    }
    private static class GetExamplesByWordAsyncTask extends AsyncTask<Long, Void, List<Example>> {
        private ExampleDao exampleDao;
        private WeakReference<OnExamplesLoadedListener> listener;

        private GetExamplesByWordAsyncTask(ExampleDao exampleDao, OnExamplesLoadedListener listener) {
            this.exampleDao = exampleDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected List<Example> doInBackground(Long... longs) {
            return exampleDao.getExamplesByWordId(longs[0]);
        }

        @Override
        protected void onPostExecute(List<Example> subgroup) {
            super.onPostExecute(subgroup);
            OnExamplesLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onLoaded(subgroup);
            }
        }
    }
}
