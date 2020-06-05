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
import java.util.concurrent.ExecutionException;

import ru.nikshlykov.englishwordsapp.MyApplication;
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

public class AppRepository {

    public static final String PATH_TO_SUBGROUP_IMAGES =
            "https://raw.githubusercontent.com/NIKSHLYKOV/englishwordsappimages/master/";
    public static final String PATH_TO_HIGH_SUBGROUP_IMAGES =
            "https://raw.githubusercontent.com/NIKSHLYKOV/englishwordsappimages/master/high_images/";

    private static final String LOG_TAG = "AppRepository";
    private AppDatabase database;

    private static MyApplication application;

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

        this.application = (MyApplication) application;

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
    // ГДЕ ХРАНИТЬ EXECUTORSERVICE??? МОЖЕТ, ПРОСТО ЗДЕСЬ, В REPOSITORY?
    public void resetWordsProgress(final long subgroupId) {
        application.executeWithDatabase(new Runnable() {
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
     * Методы для работы с подгруппами.
     */
    public void getMinSubgroupId(OnMinSubgroupIdLoadedListener listener) {
        GetMinSubgroupIdAsyncTask task = new GetMinSubgroupIdAsyncTask(subgroupDao, listener);
        task.execute();
    }

    public void insert(Subgroup subgroup, OnSubgroupInsertedListener listener) {
        new InsertSubgroupAsyncTask(subgroupDao, listener).execute(subgroup);
    }

    public void update(Subgroup subgroup) {
        new UpdateSubgroupAsyncTask(subgroupDao).execute(subgroup);
    }

    public void delete(Subgroup subgroup) {
        new DeleteSubgroupAsyncTask(subgroupDao).execute(subgroup);
    }

    public LiveData<Subgroup> getLiveDataSubgroupById(long subgroupId) {
        return subgroupDao.getLiveDataSubgroupById(subgroupId);
    }

    public void getSubgroupById(long subgroupId, OnSubgroupLoadedListener listener) {
        GetSubgroupByIdAsyncTask task = new GetSubgroupByIdAsyncTask(subgroupDao, listener);
        task.execute(subgroupId);
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
    public interface OnMinSubgroupIdLoadedListener {
        void onMinSubgroupIdLoaded(Long minSubgroupId);
    }

    private static class GetMinSubgroupIdAsyncTask extends AsyncTask<Void, Void, Long> {
        private SubgroupDao subgroupDao;
        private WeakReference<OnMinSubgroupIdLoadedListener> listener;

        private GetMinSubgroupIdAsyncTask(SubgroupDao subgroupDao, OnMinSubgroupIdLoadedListener listener) {
            this.subgroupDao = subgroupDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return subgroupDao.getSubgroupWithMinId().id;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            OnMinSubgroupIdLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onMinSubgroupIdLoaded(aLong);
            }
        }
    }

    public interface OnSubgroupInsertedListener {
        void onSubgroupInserted(long subgroupId);
    }

    private static class InsertSubgroupAsyncTask extends AsyncTask<Subgroup, Void, Long> {
        private SubgroupDao subgroupDao;
        private WeakReference<OnSubgroupInsertedListener> listener;

        private InsertSubgroupAsyncTask(SubgroupDao subgroupDao, OnSubgroupInsertedListener listener) {
            this.subgroupDao = subgroupDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected Long doInBackground(Subgroup... subgroups) {
            return subgroupDao.insert(subgroups[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            OnSubgroupInsertedListener listener = this.listener.get();
            if (listener != null) {
                listener.onSubgroupInserted(aLong);
            }
        }
    }

    private static class UpdateSubgroupAsyncTask extends AsyncTask<Subgroup, Void, Void> {
        private SubgroupDao subgroupDao;

        private UpdateSubgroupAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Void doInBackground(Subgroup... subgroups) {
            Log.i(LOG_TAG, "UpdateSubgroupAsyncTask: subgroup.isStudied = " + subgroups[0].isStudied);
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
        void onSubgroupLoaded(Subgroup subgroup);
    }

    private static class GetSubgroupByIdAsyncTask extends AsyncTask<Long, Void, Subgroup> {
        private SubgroupDao subgroupDao;
        private WeakReference<OnSubgroupLoadedListener> listener;

        private GetSubgroupByIdAsyncTask(SubgroupDao subgroupDao, OnSubgroupLoadedListener listener) {
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
                listener.onSubgroupLoaded(subgroup);
            }
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
    public void getGroupItems(OnGroupItemsLoadedListener listener) {
        GetGroupItemsAsyncTask task = new GetGroupItemsAsyncTask(groupDao, subgroupDao, listener);
        task.execute();
    }

    /**
     * AsyncTasks для работы с группами.
     */
    public interface OnGroupItemsLoadedListener {
        void onGroupItemsLoaded(ArrayList<GroupItem> groupItems);
    }
    private static class GetGroupItemsAsyncTask extends AsyncTask<Void, Void, ArrayList<GroupItem>> {
        private SubgroupDao subgroupDao;
        private GroupDao groupDao;
        private WeakReference<OnGroupItemsLoadedListener> listener;

        private GetGroupItemsAsyncTask(GroupDao groupDao, SubgroupDao subgroupDao,
                                       OnGroupItemsLoadedListener listener) {
            this.groupDao = groupDao;
            this.subgroupDao = subgroupDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected ArrayList<GroupItem> doInBackground(Void... voids) {
            List<Group> groups = groupDao.getGroups();
            ArrayList<GroupItem> groupItems = new ArrayList<>(groups.size());
            for (Group group : groups) {
                List<Subgroup> subgroupsList = subgroupDao.getSubgroupsFromGroup(group.id);
                ArrayList<Subgroup> subgroups = new ArrayList<>(subgroupsList.size());
                subgroups.addAll(subgroupsList);
                if (subgroups.size() != 0) {
                    GroupItem groupItem = new GroupItem(group, subgroups);
                    groupItems.add(groupItem);
                }
            }
            return groupItems;
        }

        @Override
        protected void onPostExecute(ArrayList<GroupItem> subgroup) {
            super.onPostExecute(subgroup);
            OnGroupItemsLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onGroupItemsLoaded(subgroup);
            }
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

    public void newGetSelectedModes(OnSelectedModesLoadedListener listener) {
        NewGetSelectedModesAsyncTask task = new NewGetSelectedModesAsyncTask(modeDao, listener);
        task.execute();
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

    public interface OnSelectedModesLoadedListener {
        void onSelectedModesLoaded(List<Mode> selectedModes);
    }

    private static class NewGetSelectedModesAsyncTask extends AsyncTask<Void, Void, List<Mode>> {
        private ModeDao modeDao;
        private WeakReference<OnSelectedModesLoadedListener> listener;

        private NewGetSelectedModesAsyncTask(ModeDao modeDao, OnSelectedModesLoadedListener listener) {
            this.modeDao = modeDao;
            this.listener = new WeakReference<>(listener);
        }

        @Override
        protected List<Mode> doInBackground(Void... voids) {
            return modeDao.newGetSelectedModes();
        }

        @Override
        protected void onPostExecute(List<Mode> modes) {
            super.onPostExecute(modes);
            OnSelectedModesLoadedListener listener = this.listener.get();
            if (listener != null) {
                listener.onSelectedModesLoaded(modes);
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
