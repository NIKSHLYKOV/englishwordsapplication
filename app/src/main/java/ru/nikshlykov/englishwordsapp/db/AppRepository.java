package ru.nikshlykov.englishwordsapp.db;

import android.app.Application;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.nikshlykov.englishwordsapp.db.example.ExampleDao;
import ru.nikshlykov.englishwordsapp.db.group.GroupDao;
import ru.nikshlykov.englishwordsapp.db.link.Link;
import ru.nikshlykov.englishwordsapp.db.link.LinkDao;
import ru.nikshlykov.englishwordsapp.db.mode.Mode;
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao;
import ru.nikshlykov.englishwordsapp.db.repeat.Repeat;
import ru.nikshlykov.englishwordsapp.db.repeat.RepeatDao;
import ru.nikshlykov.englishwordsapp.db.setting.Setting;
import ru.nikshlykov.englishwordsapp.db.setting.SettingDao;
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup;
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.db.word.WordDao;

import static ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment.TO_DELETE;
import static ru.nikshlykov.englishwordsapp.ui.word.LinkOrDeleteWordDialogFragment.TO_LINK;

public class AppRepository {

    private static final String LOG_TAG = "AppRepository";
    private AppDatabase database;

    private ExampleDao exampleDao;
    private GroupDao groupDao;
    private LinkDao linkDao;
    private ModeDao modeDao;
    private RepeatDao repeatDao;
    private SettingDao settingDao;
    private SubgroupDao subgroupDao;
    private WordDao wordDao;

    public AppRepository(Application application) {
        database = AppDatabase.getInstance(application);

        exampleDao = database.exampleDao();
        groupDao = database.groupDao();
        linkDao = database.linkDao();
        modeDao = database.modeDao();
        repeatDao = database.repeatDao();
        settingDao = database.settingDao();
        subgroupDao = database.subgroupDao();
        wordDao = database.wordDao();
    }


    /**
     * Методы для работы со словами.
     */

    public void insert(Word word, OnWordInsertedListener listener) {
        NewInsertWordAsyncTask task = new NewInsertWordAsyncTask(wordDao, listener);
        task.execute(word);
    }

    public void update(Word word) {
        new UpdateWordAsyncTask(wordDao).execute(word);
    }

    public void delete(Word word) {
        new DeleteWordAsyncTask(wordDao).execute(word);
    }

    // РАЗОБРАТЬСЯ С ИСПОЛЬЗОВАНИЕМ В STUDYVIEWMODEL.
    public Word getWordById(long id) {
        GetWordByIdAsyncTask task = new GetWordByIdAsyncTask(wordDao);
        task.execute(id);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<Word> getLiveDataWordById(long wordId) {
        return wordDao.getLiveDataWordById(wordId);
    }

    public void getWord(long wordId, OnWordLoadedListener listener) {
        GetWordAsyncTask task = new GetWordAsyncTask(wordDao, listener);
        task.execute(wordId);
    }

    public LiveData<List<Word>> getWordsFromSubgroupByProgress(long subgroupId) {
        return wordDao.getWordsFromSubgroupByProgress(subgroupId);
    }

    public LiveData<List<Word>> getWordsFromSubgroupByAlphabet(long subgroupId) {
        return wordDao.getWordsFromSubgroupByAlphabet(subgroupId);
    }

    // РАЗОБРАТЬСЯ С ИСПОЛЬЗОВАНИЕМ В STUDYVIEWMODEL. ЗАМЕНИТЬ НА LIVEDATA.
    public ArrayList<Word> getAvailableToRepeatWords() {
        GetAvailableToRepeatWordsAsyncTask task
                = new GetAvailableToRepeatWordsAsyncTask(wordDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void resetWordsProgress(final long subgroupId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Word> words = wordDao.getWordsFromSubgroup(subgroupId);
                for (Word word : words) {
                    word.learnProgress = -1;
                }
                wordDao.update(words);
            }
        }).start();
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

    private static class UpdateWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private UpdateWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.update(words[0]);
            return null;
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

    private static class GetWordByIdAsyncTask extends AsyncTask<Long, Void, Word> {
        private WordDao wordDao;

        private GetWordByIdAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Word doInBackground(Long... longs) {
            Log.i(LOG_TAG, "id слова в asyncTask = " + longs[0]);
            return wordDao.getWordById(longs[0]);
        }
    }

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

    private static class GetAvailableToRepeatWordsAsyncTask extends AsyncTask<Void, Void, ArrayList<Word>> {
        private WordDao wordDao;

        private GetAvailableToRepeatWordsAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected ArrayList<Word> doInBackground(Void... voids) {
            Word[] wordsFromStudiedSubgroups = wordDao.getAllWordsFromStudiedSubgroups();
            ArrayList<Word> availableToRepeatWords = new ArrayList<>();
            Date currentDate = new Date();
            for (Word word : wordsFromStudiedSubgroups) {
                if (word.isAvailableToRepeat(currentDate)) {
                    availableToRepeatWords.add(word);
                }
            }
            return availableToRepeatWords;
        }
    }


    /**
     * Методы для работы с подгруппами.
     */

    // РАЗОБРАТЬСЯ С ИСПОЛЬЗОВАНИЕМ В GROUPSFRAGMENT. ПОМЕНЯТЬ НА LISTENER.
    public long getLastSubgroupId() {
        GetLastSubgroupIdAsyncTask task = new GetLastSubgroupIdAsyncTask(subgroupDao);
        task.execute();
        long lastSubgroupId = 0L;
        try {
            lastSubgroupId = task.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return lastSubgroupId;
    }

    public void insert(Subgroup subgroup) {
        new InsertSubgroupAsyncTask(subgroupDao).execute(subgroup);
    }

    public void update(Subgroup subgroup) {
        new UpdateSubgroupAsyncTask(subgroupDao).execute(subgroup);
    }

    public void delete(Subgroup subgroup) {
        new DeleteSubgroupAsyncTask(subgroupDao).execute(subgroup);
    }

    public void getSubgroup(long subgroupId, OnSubgroupLoadedListener listener) {
        GetSubgroupAsyncTask task = new GetSubgroupAsyncTask(subgroupDao, listener);
        task.execute(subgroupId);
    }

    public LiveData<Subgroup> getLiveDataSubgroupById(long subgroupId) {
        return subgroupDao.getLiveDataSubgroupById(subgroupId);
    }

    // УЙДЁТ, КОГДА ИЗБАВИМСЯ ОТ EXPANDABLELISTVIEW.
    public Cursor getSubgroupsFromGroup(long groupId) {
        GetSubgroupsFromGroupAsyncTask task = new GetSubgroupsFromGroupAsyncTask(subgroupDao);
        task.execute(groupId);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    // РАЗОБРАТЬСЯ С ИСПОЛЬЗОВАНИЕМ В STUDYVIEWMODEL. ЗАМЕНИТЬ НА LIVEDATA.
    public Subgroup[] getStudiedSubgroups() {
        GetStudiedSubgroupsAsyncTask task = new GetStudiedSubgroupsAsyncTask(subgroupDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getAvailableSubgroupTo(long wordId, int flagTo, OnSubgroupsLoadedListener listener) {
        Log.d(LOG_TAG, "getAvailableSubgroupsTo()");
        GetAvailableSubgroupsToAsyncTask task = new GetAvailableSubgroupsToAsyncTask(subgroupDao,
                linkDao, flagTo, listener);
        task.execute(wordId);
    }

    /**
     * AsyncTasks для работы с подгруппами.
     */
    private static class GetLastSubgroupIdAsyncTask extends AsyncTask<Void, Void, Long> {
        private SubgroupDao subgroupDao;

        private GetLastSubgroupIdAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return subgroupDao.getSubgroupWithMaxId().id;
        }
    }

    private static class InsertSubgroupAsyncTask extends AsyncTask<Subgroup, Void, Long> {
        private SubgroupDao subgroupDao;

        private InsertSubgroupAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Long doInBackground(Subgroup... subgroups) {
            return subgroupDao.insert(subgroups[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }
    }

    private static class UpdateSubgroupAsyncTask extends AsyncTask<Subgroup, Void, Void> {
        private SubgroupDao subgroupDao;

        private UpdateSubgroupAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Void doInBackground(Subgroup... subgroups) {
            subgroupDao.update(subgroups[0]);
            return null;
        }
    }

    private static class DeleteSubgroupAsyncTask extends AsyncTask<Subgroup, Void, Void> {
        private SubgroupDao subgroupDao;

        private DeleteSubgroupAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Void doInBackground(Subgroup... subgroups) {
            subgroupDao.delete(subgroups[0]);
            return null;
        }
    }

    public interface OnSubgroupLoadedListener {
        void onLoaded(Subgroup subgroup);
    }
    private static class GetSubgroupAsyncTask extends AsyncTask<Long, Void, Subgroup> {
        private SubgroupDao subgroupDao;
        private WeakReference<OnSubgroupLoadedListener> listener;

        private GetSubgroupAsyncTask(SubgroupDao subgroupDao,
                                     OnSubgroupLoadedListener listener) {
            this.subgroupDao = subgroupDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Subgroup doInBackground(Long... longs) {
            return subgroupDao.getSubgroupById(longs[0]);
        }

        @Override
        protected void onPostExecute(Subgroup subgroup) {
            super.onPostExecute(subgroup);
            OnSubgroupLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onLoaded(subgroup);
            }
        }
    }

    private static class GetSubgroupsFromGroupAsyncTask extends AsyncTask<Long, Void, Cursor> {
        private SubgroupDao subgroupDao;

        private GetSubgroupsFromGroupAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Cursor doInBackground(Long... longs) {
            Log.i(LOG_TAG, "id подгруппы в asyncTask = " + longs[0]);
            return subgroupDao.getSubgroupsFromGroup(longs[0]);
        }
    }

    private static class GetStudiedSubgroupsAsyncTask extends AsyncTask<Void, Void, Subgroup[]> {
        private SubgroupDao subgroupDao;

        private GetStudiedSubgroupsAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Subgroup[] doInBackground(Void... longs) {
            return subgroupDao.getStudiedSubgroups();
        }
    }

    public interface OnSubgroupsLoadedListener {
        void onLoaded(ArrayList<Subgroup> subgroups);
    }
    private static class GetAvailableSubgroupsToAsyncTask extends AsyncTask<Long, Void, ArrayList<Subgroup>> {
        private SubgroupDao subgroupDao;
        private LinkDao linkDao;
        private WeakReference<OnSubgroupsLoadedListener> listener;
        private int flagTo;

        private GetAvailableSubgroupsToAsyncTask(SubgroupDao subgroupDao, LinkDao linkDao,
                                                 int flagTo, OnSubgroupsLoadedListener listener) {
            this.subgroupDao = subgroupDao;
            this.linkDao = linkDao;
            this.flagTo = flagTo;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected ArrayList<Subgroup> doInBackground(Long... longs) {
            // Получаем подгруппы, созданные пользователем и проверяем, что они вообще есть.
            Subgroup[] createdByUserSubgroups = subgroupDao.getCreatedByUserSubgroups();
            if (createdByUserSubgroups.length != 0) {
                // Получаем все связи нашего слова и проверяем, что они вообще есть.
                // На данный момент они точно будут, но если мы будем делать что-то типа словаря,
                // в котором слово не обязательно залинковано с подгруппой, то эта проверка потребуется.
                Link[] linksWithWord = linkDao.getLinksByWordId(longs[0]);
                if (linksWithWord.length != 0) {

                    // Делаем коллекцию из id связанных с нашим словом подгрупп и заполняем её.
                    HashSet<Long> linkedWithWordSubgroupsIds = new HashSet<>(linksWithWord.length);
                    for (Link link : linksWithWord) {
                        linkedWithWordSubgroupsIds.add(link.getSubgroupId());
                    }

                    // Делаем коллекцию доступных подгрупп и заполняем её пока созданными группами.
                    HashSet<Long> availableSubgroupsIds = new HashSet<>(createdByUserSubgroups.length);
                    for (Subgroup subgroup : createdByUserSubgroups) {
                        availableSubgroupsIds.add(subgroup.id);
                    }

                    if (flagTo == TO_LINK) {
                        // Удаляем из коллекции те id подгрупп, с которыми уже связано слово.
                        availableSubgroupsIds.removeAll(linkedWithWordSubgroupsIds);
                    } else if (flagTo == TO_DELETE) {
                        // Оставляем в коллекции те id подгрупп, с которыми уже связано слово.
                        availableSubgroupsIds.retainAll(linkedWithWordSubgroupsIds);
                    }

                    // Теперь коллекция действительно содержит id доступных подгрупп.


                    ArrayList<Subgroup> availableSubgroups;
                    // Проверяем, что коллекция не пустая
                    if (availableSubgroupsIds.size() != 0) {
                        // Заполняем список доступных подгрупп.
                        availableSubgroups = new ArrayList<>(availableSubgroupsIds.size());
                        for (Long availableSubgroupId : availableSubgroupsIds) {
                            availableSubgroups.add(subgroupDao.getSubgroupById(availableSubgroupId));
                        }
                    } else {
                        availableSubgroups = new ArrayList<>();
                    }

                    return availableSubgroups;
                } else {
                    return new ArrayList<>(Arrays.asList(createdByUserSubgroups));
                }
            } else {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Subgroup> subgroup) {
            super.onPostExecute(subgroup);
            OnSubgroupsLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onLoaded(subgroup);
            }
        }


    }

    /**
     * Методы для работы с группами.
     */

    // УЙДЁТ, КОГДА ИЗБАВИМСЯ ОТ EXPANDABLELISTVIEW.
    public Cursor getAllGroups() {
        GetAllGroupsAsyncTask getAllGroupsAsyncTask = new GetAllGroupsAsyncTask(groupDao);
        getAllGroupsAsyncTask.execute();
        try {
            return getAllGroupsAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AsyncTasks для работы с группами.
     */
    private static class GetAllGroupsAsyncTask extends AsyncTask<Void, Void, Cursor> {
        private GroupDao groupDao;

        private GetAllGroupsAsyncTask(GroupDao groupDao) {
            this.groupDao = groupDao;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return groupDao.getAllGroups();
        }
    }


    /**
     * Методы для работы со связями.
     */
    public void insert(Link link) {
        Log.i(LOG_TAG, "insert(link):\n" +
                "subgroupId = " + link.getSubgroupId() + "; wordId = " + link.getWordId());
        new InsertLinkAsyncTask(linkDao).execute(link);
    }

    public void delete(Link link) {
        new DeleteLinkAsyncTask(linkDao).execute(link);
    }

    /**
     * AsyncTasks для работы со связями.
     */
    private static class InsertLinkAsyncTask extends AsyncTask<Link, Void, Long> {
        private LinkDao linkDao;

        private InsertLinkAsyncTask(LinkDao linkDao) {
            this.linkDao = linkDao;
        }

        @Override
        protected Long doInBackground(Link... links) {
            return linkDao.insert(links[0]);
        }
    }

    private static class DeleteLinkAsyncTask extends AsyncTask<Link, Void, Void> {
        private LinkDao linkDao;

        private DeleteLinkAsyncTask(LinkDao linkDao) {
            this.linkDao = linkDao;
        }

        @Override
        protected Void doInBackground(Link... links) {
            linkDao.delete(links[0]);
            return null;
        }
    }


    /**
     * Методы для работы с режимами.
     */
    public void update(List<Mode> modes) {
        new UpdateModeAsyncTask(modeDao).execute(modes);
    }

    public Mode[] getSelectedModes() {
        GetSelectedModesAsyncTask task = new GetSelectedModesAsyncTask(modeDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<Mode>> getLiveDataModes() {
        return modeDao.getLiveDataModes();
    }

    /**
     * AsyncTasks для работы с режимами.
     */
    private static class UpdateModeAsyncTask extends AsyncTask<List<Mode>, Void, Void> {
        private ModeDao modeDao;

        private UpdateModeAsyncTask(ModeDao modeDao) {
            this.modeDao = modeDao;
        }

        @Override
        protected Void doInBackground(List<Mode>... modes) {
            modeDao.update(modes[0]);
            return null;
        }
    }

    private static class GetSelectedModesAsyncTask extends AsyncTask<Void, Void, Mode[]> {
        private ModeDao modeDao;

        private GetSelectedModesAsyncTask(ModeDao modeDao) {
            this.modeDao = modeDao;
        }

        @Override
        protected Mode[] doInBackground(Void... voids) {
            return modeDao.getSelectedModes();
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

    // РАЗОБРАТЬСЯ С ИСПОЛЬЗОВАНИЕМ В STUDYVIEWMODEL.
    public Repeat getLastRepeatByWord(long wordId) {
        GetLastRepeatByWordAsyncTask task = new GetLastRepeatByWordAsyncTask(repeatDao);
        task.execute(wordId);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    // РАЗОБРАТЬСЯ С ИСПОЛЬЗОВАНИЕМ В STUDYVIEWMODEL.
    public long getLastRepeatId() {
        GetLastRepeatIdAsyncTask task = new GetLastRepeatIdAsyncTask(repeatDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0L;
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

    private static class GetLastRepeatByWordAsyncTask extends AsyncTask<Long, Void, Repeat> {
        private RepeatDao repeatDao;

        private GetLastRepeatByWordAsyncTask(RepeatDao repeatDao) {
            this.repeatDao = repeatDao;
        }

        @Override
        protected Repeat doInBackground(Long... longs) {
            return repeatDao.getLastRepeatByWord(longs[0]);
        }
    }

    private static class GetLastRepeatIdAsyncTask extends AsyncTask<Void, Void, Long> {
        private RepeatDao repeatDao;

        private GetLastRepeatIdAsyncTask(RepeatDao repeatDao) {
            this.repeatDao = repeatDao;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            Repeat lastRepeat = repeatDao.getRepeatWithMaxId();
            if (lastRepeat != null)
                return lastRepeat.getId();
            else
                return 0L;
        }
    }
}
