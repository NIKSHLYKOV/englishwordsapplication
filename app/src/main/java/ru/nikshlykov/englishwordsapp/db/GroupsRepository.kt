package ru.nikshlykov.englishwordsapp.db

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import ru.nikshlykov.englishwordsapp.db.link.Link
import ru.nikshlykov.englishwordsapp.db.link.LinkDao
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.subgroup.SubgroupDao
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashSet

class GroupsRepository(database: AppDatabase) {
  private val linkDao: LinkDao = database.linkDao()
  private val subgroupDao: SubgroupDao = database.subgroupDao()

  /**
   * Методы для работы с подгруппами.
   */

  fun update(subgroup: Subgroup?) {
    UpdateSubgroupAsyncTask(subgroupDao, null).execute(subgroup)
  }

  fun update(subgroup: Subgroup?, listener: OnSubgroupUpdatedListener?) {
    UpdateSubgroupAsyncTask(subgroupDao, listener).execute(subgroup)
  }

  fun delete(subgroup: Subgroup?) {
    DeleteSubgroupAsyncTask(subgroupDao).execute(subgroup)
  }

  fun getLiveDataSubgroupById(subgroupId: Long): LiveData<Subgroup> {
    return subgroupDao!!.getLiveDataSubgroupById(subgroupId)
  }

  fun getAvailableSubgroupTo(wordId: Long, flagTo: Int, listener: OnSubgroupsLoadedListener) {
    Log.d(LOG_TAG, "getAvailableSubgroupsTo()")
    val task = GetAvailableSubgroupsToAsyncTask(
      subgroupDao,
      linkDao, flagTo, listener
    )
    task.execute(wordId)
  }

  /**
   * AsyncTasks для работы с подгруппами.
   */

  interface OnSubgroupUpdatedListener {
    fun onSubgroupUpdated(isSubgroupUpdated: Boolean)
  }

  private class UpdateSubgroupAsyncTask(
    private val subgroupDao: SubgroupDao?,
    listener: OnSubgroupUpdatedListener?
  ) : AsyncTask<Subgroup, Void, Boolean>() {
    private val listener: WeakReference<OnSubgroupUpdatedListener?>
    protected override fun doInBackground(vararg subgroups: Subgroup): Boolean {
      Log.i(LOG_TAG, "UpdateSubgroupAsyncTask: subgroup.isStudied = " + subgroups[0].studied)
      return subgroupDao!!.update(subgroups[0]) == 1
    }

    override fun onPostExecute(aBoolean: Boolean) {
      super.onPostExecute(aBoolean)
      val listener = listener.get()
      listener?.onSubgroupUpdated(aBoolean)
    }

    init {
      this.listener = WeakReference(listener)
    }
  }

  private class DeleteSubgroupAsyncTask(private val subgroupDao: SubgroupDao?) :
    AsyncTask<Subgroup, Void, Void>() {
    protected override fun doInBackground(vararg subgroups: Subgroup): Void? {
      subgroupDao!!.delete(subgroups[0])
      return null
    }
  }

  interface OnSubgroupsLoadedListener {
    fun onLoaded(subgroups: ArrayList<Subgroup>?)
  }

  private class GetAvailableSubgroupsToAsyncTask(
    private val subgroupDao: SubgroupDao, private val linkDao: LinkDao?,
    private val flagTo: Int, listener: OnSubgroupsLoadedListener
  ) : AsyncTask<Long, Void, ArrayList<Subgroup>>() {
    private val listener: WeakReference<OnSubgroupsLoadedListener> = WeakReference(listener)
    override fun doInBackground(vararg p0: Long?): ArrayList<Subgroup>? {
      // Получаем подгруппы, созданные пользователем и проверяем, что они вообще есть.
      val createdByUserSubgroups = subgroupDao.createdByUserSubgroups()
      return if (createdByUserSubgroups.isNotEmpty()) {
        // Получаем все связи нашего слова и проверяем, что они вообще есть.
        // На данный момент они точно будут, но если мы будем делать что-то типа словаря,
        // в котором слово не обязательно залинковано с подгруппой, то эта проверка потребуется.
        val linksWithWord = p0[0]?.let { linkDao!!.getLinksByWordId(it) }
        if (linksWithWord?.size != 0) {

          // Делаем коллекцию из id связанных с нашим словом подгрупп и заполняем её.
          val linkedWithWordSubgroupsIds = HashSet<Long>()
          if (linksWithWord != null) {
            for (link in linksWithWord) {
              linkedWithWordSubgroupsIds.add(link.subgroupId)
            }
          }

          // Делаем коллекцию доступных подгрупп и заполняем её пока созданными группами.
          val availableSubgroupsIds: HashSet<Long> = HashSet(createdByUserSubgroups.size)
          for (subgroup in createdByUserSubgroups) {
            availableSubgroupsIds.add(subgroup.id)
          }
          if (flagTo == LinkOrDeleteWordDialogFragment.TO_LINK) {
            // Удаляем из коллекции те id подгрупп, с которыми уже связано слово.
            availableSubgroupsIds.removeAll(linkedWithWordSubgroupsIds)
          } else if (flagTo == LinkOrDeleteWordDialogFragment.TO_DELETE) {
            // Оставляем в коллекции те id подгрупп, с которыми уже связано слово.
            availableSubgroupsIds.retainAll(linkedWithWordSubgroupsIds)
          }

          // Теперь коллекция действительно содержит id доступных подгрупп.
          val availableSubgroups: ArrayList<Subgroup>
          // Проверяем, что коллекция не пустая
          if (availableSubgroupsIds.size != 0) {
            // Заполняем список доступных подгрупп.
            availableSubgroups = ArrayList(availableSubgroupsIds.size)
            for (availableSubgroupId in availableSubgroupsIds) {
              availableSubgroups.add(subgroupDao.getSubgroupById(availableSubgroupId))
            }
          } else {
            availableSubgroups = ArrayList()
          }
          availableSubgroups
        } else {
          ArrayList(Arrays.asList(*createdByUserSubgroups))
        }
      } else {
        ArrayList()
      }
    }

    override fun onPostExecute(subgroup: ArrayList<Subgroup>) {
      super.onPostExecute(subgroup)
      val listener = listener.get()
      listener?.onLoaded(subgroup)
    }

  }


  /**
   * Методы для работы со связями.
   */
  fun insert(link: Link) {
    Log.i(
      LOG_TAG, """
   insert(link):
   subgroupId = ${link.subgroupId}; wordId = ${link.wordId}
   """.trimIndent()
    )
    InsertLinkAsyncTask(linkDao).execute(link)
  }

  fun delete(link: Link?) {
    DeleteLinkAsyncTask(linkDao).execute(link)
  }

  /**
   * AsyncTasks для работы со связями.
   */
  private class InsertLinkAsyncTask(private val linkDao: LinkDao?) :
    AsyncTask<Link, Void, Long>() {
    protected override fun doInBackground(vararg links: Link): Long {
      return linkDao!!.insert(links[0])
    }
  }

  private class DeleteLinkAsyncTask(private val linkDao: LinkDao?) :
    AsyncTask<Link, Void, Void>() {
    protected override fun doInBackground(vararg links: Link): Void? {
      linkDao!!.delete(links[0])
      return null
    }
  }

  companion object {
    const val PATH_TO_SUBGROUP_IMAGES =
      "https://raw.githubusercontent.com/NIKSHLYKOV/englishwordsappimages/master/"
    const val PATH_TO_HIGH_SUBGROUP_IMAGES =
      "https://raw.githubusercontent.com/NIKSHLYKOV/englishwordsappimages/master/high_images/"
    private val LOG_TAG = GroupsRepository::class.java.canonicalName
  }
}