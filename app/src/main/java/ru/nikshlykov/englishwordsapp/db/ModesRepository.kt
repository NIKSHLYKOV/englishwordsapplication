package ru.nikshlykov.englishwordsapp.db

import android.os.AsyncTask
import ru.nikshlykov.englishwordsapp.db.mode.Mode
import ru.nikshlykov.englishwordsapp.db.mode.ModeDao
import java.lang.ref.WeakReference

class ModesRepository(database: AppDatabase) {
  private val modeDao: ModeDao = database.modeDao()

  /**
   * Методы для работы с режимами.
   */

  fun newGetSelectedModes(listener: OnSelectedModesLoadedListener) {
    val task = NewGetSelectedModesAsyncTask(modeDao, listener)
    task.execute()
  }

  /**
   * AsyncTasks для работы с режимами.
   */

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