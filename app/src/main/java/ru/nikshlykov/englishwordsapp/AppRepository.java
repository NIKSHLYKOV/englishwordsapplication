package ru.nikshlykov.englishwordsapp;

import android.app.Application;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AppRepository {

    private final static String LOG_TAG = "AppRepository";
    private AppDatabase database;

    private WordDao wordDao;
    private SubgroupDao subgroupDao;
    private GroupDao groupDao;
    private LinkDao linkDao;
    private ModeDao modeDao;
    private SettingDao settingDao;


    public AppRepository(Application application) {
        database = AppDatabase.getInstance(application);

        wordDao = database.wordDao();
        subgroupDao = database.subgroupDao();
        groupDao = database.groupDao();
        modeDao = database.modeDao();
        linkDao = database.linkDao();
        settingDao = database.settingDao();
    }

    /**
     *  Методы для работы со словами.
     */
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

    public LiveData<List<Word>> getWordsFromSubgroup(long subgroupId) {
        return wordDao.getWordsFromSubgroup(subgroupId);
    }

    /**
     * AsyncTasks для работы со словами.
     */
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

    /**
     * Методы для работы с подгруппами.
     */
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

    /**
     * AsyncTasks для работы с подгруппами.
     */
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


}

