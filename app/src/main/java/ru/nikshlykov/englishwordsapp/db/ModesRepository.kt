package ru.nikshlykov.englishwordsapp.db

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.mode.Mode
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao
import java.lang.ref.WeakReference

class ModesRepository(database: AppDatabase) {
  private val modeDao: ModeDao = database.modeDao()

  /**
   * Методы для работы с режимами.
   */
  fun update(modes: List<Mode?>?) {
    UpdateModeAsyncTask(modeDao).execute(modes as List<Mode>?)
  }

  fun newGetSelectedModes(listener: OnSelectedModesLoadedListener) {
    val task = NewGetSelectedModesAsyncTask(modeDao, listener)
    task.execute()
  }

  /**
   * AsyncTasks для работы с режимами.
   */
  private class UpdateModeAsyncTask(private val modeDao: ModeDao) :
    AsyncTask<List<Mode>, Void, Void>() {
    protected override fun doInBackground(vararg modes: List<Mode>): Void? {
      modeDao.update(modes[0])
      return null
    }
  }

  interface OnSelectedModesLoadedListener {
    fun onSelectedModesLoaded(selectedModes: List<Mode>?)
  }

  private class NewGetSelectedModesAsyncTask(
    private val modeDao: ModeDao?,
    listener: OnSelectedModesLoadedListener
  ) : AsyncTask<Void, Void, List<Mode>>() {
    private val listener: WeakReference<OnSelectedModesLoadedListener> = WeakReference(listener)
    override fun doInBackground(vararg voids: Void): List<Mode> {
      return modeDao!!.newGetSelectedModes()
    }

    override fun onPostExecute(modes: List<Mode>) {
      super.onPostExecute(modes)
      val listener = listener.get()
      listener?.onSelectedModesLoaded(modes)
    }

  }
}