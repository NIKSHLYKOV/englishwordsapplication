package ru.nikshlykov.englishwordsapp.db;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.nikshlykov.englishwordsapp.db.mode.Mode;
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao;

public class ModesRepository {

    private static final String LOG_TAG = ModesRepository.class.getCanonicalName();

    private ModeDao modeDao;

    public ModesRepository(AppDatabase database) {

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
