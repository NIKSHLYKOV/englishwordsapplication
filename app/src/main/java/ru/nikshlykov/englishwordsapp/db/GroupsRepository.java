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

public class GroupsRepository {

    public static final String PATH_TO_SUBGROUP_IMAGES =
            "https://raw.githubusercontent.com/NIKSHLYKOV/englishwordsappimages/master/";
    public static final String PATH_TO_HIGH_SUBGROUP_IMAGES =
            "https://raw.githubusercontent.com/NIKSHLYKOV/englishwordsappimages/master/high_images/";

    private static final String LOG_TAG = GroupsRepository.class.getCanonicalName();

    private GroupDao groupDao;
    private LinkDao linkDao;
    private SubgroupDao subgroupDao;

    private ExecutorService databaseExecutorService;


    public GroupsRepository(AppDatabase database) {

        groupDao = database.groupDao();
        linkDao = database.linkDao();
        subgroupDao = database.subgroupDao();
        //wordDao = database.wordDao();

        databaseExecutorService = Executors.newFixedThreadPool(1);
    }

    public void execute(Runnable runnable){
        databaseExecutorService.execute(runnable);
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
}
