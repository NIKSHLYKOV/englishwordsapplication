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

public class ModesRepository {

    private static final String LOG_TAG = ModesRepository.class.getCanonicalName();

    private ModeDao modeDao;

    public ModesRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);

        modeDao = database.modeDao();
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
}
