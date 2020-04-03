package ru.nikshlykov.englishwordsapp.db;

import android.app.Application;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.nikshlykov.englishwordsapp.db.example.ExampleDao;
import ru.nikshlykov.englishwordsapp.db.group.Group;
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
    public long getMinWordId() {
        GetMinWordIdAsyncTask task = new GetMinWordIdAsyncTask(wordDao);
        task.execute();
        long minWordId = 0L;
        try {
            minWordId = task.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return minWordId;
    }

    public long insert(Word word) {
        InsertWordAsyncTask insertWordAsyncTask = new InsertWordAsyncTask(wordDao);
        insertWordAsyncTask.execute(word);

        long newWordID = 0L;

        try {
            newWordID = insertWordAsyncTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return 0L;
        }
        return newWordID;
    }

    public void update(Word word) {
        new UpdateWordAsyncTask(wordDao).execute(word);
    }

    public void delete(Word word) {
        new DeleteWordAsyncTask(wordDao).execute(word);
    }

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

    public LiveData<List<Word>> getWordsFromSubgroupByProgress(long subgroupId) {
        return wordDao.getWordsFromSubgroupByProgress(subgroupId);
    }

    /*public LiveData<List<Word>> getWordsFromSubgroupByAlphabet(long subgroupId) {
        return wordDao.getWordsFromSubgroupByAlphabet(subgroupId);
    }*/

    public Word[] getAllWordsFromStudiedSubgroups() {
        GetAllWordsFromStudiedSubgroupsByIdAsyncTask task
                = new GetAllWordsFromStudiedSubgroupsByIdAsyncTask(wordDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

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


    /**
     * AsyncTasks для работы со словами.
     */
    private static class GetMinWordIdAsyncTask extends AsyncTask<Void, Void, Long> {
        private WordDao wordDao;

        private GetMinWordIdAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return wordDao.getWordWithMinId().id;
        }
    }

    private static class InsertWordAsyncTask extends AsyncTask<Word, Void, Long> {
        private WordDao wordDao;

        private InsertWordAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Long doInBackground(Word... words) {
            return wordDao.insert(words[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
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

    private static class GetAllWordsFromStudiedSubgroupsByIdAsyncTask extends AsyncTask<Void, Void, Word[]> {
        private WordDao wordDao;

        private GetAllWordsFromStudiedSubgroupsByIdAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Word[] doInBackground(Void... voids) {
            return wordDao.getAllWordsFromStudiedSubgroups();
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
            for (Word word : wordsFromStudiedSubgroups) {
                if(word.isAvailableToRepeat()){
                    availableToRepeatWords.add(word);
                }
            }
            return availableToRepeatWords;
        }
    }

    /**
     * Методы для работы с подгруппами.
     */
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

    public long insert(Subgroup subgroup) {
        InsertSubgroupAsyncTask insertSubgroupAsyncTask = new InsertSubgroupAsyncTask(subgroupDao);
        insertSubgroupAsyncTask.execute(subgroup);
        long newSubgroupId = 0L;
        try {
            newSubgroupId = insertSubgroupAsyncTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return newSubgroupId;
    }

    public void update(Subgroup subgroup) {
        new UpdateSubgroupAsyncTask(subgroupDao).execute(subgroup);
    }

    public void delete(Subgroup subgroup) {
        new DeleteSubgroupAsyncTask(subgroupDao).execute(subgroup);
    }

    public Subgroup getSubgroupById(long id) {
        GetSubgroupByIdAsyncTask getSubgroupByIdAsyncTask = new GetSubgroupByIdAsyncTask(subgroupDao);
        getSubgroupByIdAsyncTask.execute(id);
        try {
            return getSubgroupByIdAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public Subgroup[] getCreatedByUserSubgroups() {
        GetCreatedByUserSubgroupsAsyncTask task = new GetCreatedByUserSubgroupsAsyncTask(subgroupDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
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

    private static class GetSubgroupByIdAsyncTask extends AsyncTask<Long, Void, Subgroup> {
        private SubgroupDao subgroupDao;

        private GetSubgroupByIdAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Subgroup doInBackground(Long... longs) {
            Log.i(LOG_TAG, "id подгруппы в asyncTask = " + longs[0]);
            return subgroupDao.getSubgroupById(longs[0]);
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

    private static class GetCreatedByUserSubgroupsAsyncTask extends AsyncTask<Void, Void, Subgroup[]> {
        private SubgroupDao subgroupDao;

        private GetCreatedByUserSubgroupsAsyncTask(SubgroupDao subgroupDao) {
            this.subgroupDao = subgroupDao;
        }

        @Override
        protected Subgroup[] doInBackground(Void... longs) {
            return subgroupDao.getCreatedByUserSubgroups();
        }
    }


    /**
     * Методы для работы с группами.
     */
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

    public Group getGroupById(long id) {
        GetGroupByIdAsyncTask getGroupByIdAsyncTask = new GetGroupByIdAsyncTask(groupDao);
        getGroupByIdAsyncTask.execute(id);
        try {
            return getGroupByIdAsyncTask.get();
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

    private static class GetGroupByIdAsyncTask extends AsyncTask<Long, Void, Group> {
        private GroupDao groupDao;

        private GetGroupByIdAsyncTask(GroupDao groupDao) {
            this.groupDao = groupDao;
        }

        @Override
        protected Group doInBackground(Long... longs) {
            Log.i(LOG_TAG, "id группы в asyncTask = " + longs[0]);
            return groupDao.getGroupById(longs[0]);
        }
    }


    /**
     * Методы для работы со связями.
     */
    public long insert(Link link) {
        InsertLinkAsyncTask insertLinkAsyncTask = new InsertLinkAsyncTask(linkDao);
        insertLinkAsyncTask.execute(link);
        long newLinkId = 0L;
        try {
            newLinkId = insertLinkAsyncTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return newLinkId;
    }

    public void delete(Link link) {
        new DeleteLinkAsyncTask(linkDao).execute(link);
    }

    public Link[] getLinksByWordId(long wordId) {
        GetLinksByWordIdAsyncTask task = new GetLinksByWordIdAsyncTask(linkDao);
        task.execute(wordId);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Link getLink(long wordId, long subgroupId) {
        GetLinkAsyncTask task = new GetLinkAsyncTask(linkDao);
        task.execute(wordId, subgroupId);
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
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

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
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

    private static class GetLinksByWordIdAsyncTask extends AsyncTask<Long, Void, Link[]> {
        private LinkDao linkDao;

        private GetLinksByWordIdAsyncTask(LinkDao linkDao) {
            this.linkDao = linkDao;
        }

        @Override
        protected Link[] doInBackground(Long... longs) {
            return linkDao.getLinksByWordId(longs[0]);
        }
    }

    private static class GetLinkAsyncTask extends AsyncTask<Long, Void, Link> {
        private LinkDao linkDao;

        private GetLinkAsyncTask(LinkDao linkDao) {
            this.linkDao = linkDao;
        }

        @Override
        protected Link doInBackground(Long... longs) {
            return linkDao.getLink(longs[0], longs[1]);
        }
    }


    /**
     * Методы для работы с режимами.
     */
    public void update(List<Mode> modes) {
        new UpdateModeAsyncTask(modeDao).execute(modes);
    }

    public List<Mode> getAllModes() {
        GetAllModesAsyncTask task = new GetAllModesAsyncTask(modeDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
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

    private static class GetAllModesAsyncTask extends AsyncTask<Void, Void, List<Mode>> {
        private ModeDao modeDao;

        private GetAllModesAsyncTask(ModeDao modeDao) {
            this.modeDao = modeDao;
        }

        @Override
        protected List<Mode> doInBackground(Void... voids) {
            return modeDao.getAllModes();
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
     * Методы для работы с настройками.
     */
    public void update(Setting[] settings) {
        new UpdateSettingsAsyncTask(settingDao).execute(settings);
    }

    public Setting[] getAllSettings() {
        GetAllSettingsAsyncTask task = new GetAllSettingsAsyncTask(settingDao);
        task.execute();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AsyncTasks для работы с настройками.
     */
    private static class UpdateSettingsAsyncTask extends AsyncTask<Setting, Void, Void> {
        private SettingDao settingDao;

        private UpdateSettingsAsyncTask(SettingDao settingDao) {
            this.settingDao = settingDao;
        }

        @Override
        protected Void doInBackground(Setting... settings) {
            settingDao.update(settings);
            return null;
        }
    }

    private static class GetAllSettingsAsyncTask extends AsyncTask<Void, Void, Setting[]> {
        private SettingDao settingDao;

        private GetAllSettingsAsyncTask(SettingDao settingDao) {
            this.settingDao = settingDao;
        }

        @Override
        protected Setting[] doInBackground(Void... voids) {
            return settingDao.getAllSettings();
        }
    }

    /**
     * Методы для работы с повторами.
     */
    public long insert(Repeat repeat) {
        InsertRepeatAsyncTask insertWordAsyncTask = new InsertRepeatAsyncTask(repeatDao);
        insertWordAsyncTask.execute(repeat);

        long newRepeatId = 0L;

        try {
            newRepeatId = insertWordAsyncTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return 0L;
        }
        return newRepeatId;
    }

    public int delete(Repeat repeat) {
        DeleteRepeatAsyncTask insertWordAsyncTask = new DeleteRepeatAsyncTask(repeatDao);
        insertWordAsyncTask.execute(repeat);

        int deletedRepeatsCount = 0;

        try {
            deletedRepeatsCount = insertWordAsyncTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return deletedRepeatsCount;
        }
        return deletedRepeatsCount;
    }

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

