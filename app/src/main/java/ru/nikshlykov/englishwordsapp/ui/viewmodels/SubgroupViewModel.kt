package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.GroupsRepository.OnSubgroupsLoadedListener
import ru.nikshlykov.englishwordsapp.db.WordsRepository
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.domain.interactors.AddWordToSubgroupInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.DeleteWordFromSubgroupInteractor
import ru.nikshlykov.englishwordsapp.domain.interactors.UpdateSubgroupInteractor
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.SortWordsDialogFragment
import java.util.*

class SubgroupViewModel(
  application: Application,
  private val groupsRepository: GroupsRepository,
  private val wordsRepository: WordsRepository,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
  private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor
) : AndroidViewModel(application),
  OnSubgroupsLoadedListener {
  // Подгруппа
  lateinit var subgroupLiveData: LiveData<Subgroup>
    private set
  private var subgroupId: Long = 0
  private var newIsStudied = 0

  // Слова, залинкованные с подгруппой
  // Список слов для Activity.
  val words: MediatorLiveData<List<Word>>

  // Источники данных для words.
  private var wordsByAlphabet: LiveData<List<Word>>? = null
  private var wordsByProgress: LiveData<List<Word>>? = null

  // Observer, который сетит список слов в words.
  private val observer: Observer<List<Word>>
  private val availableSubgroupToLink: MutableLiveData<ArrayList<Subgroup>?>
  fun setLiveDataSubgroup(subgroupId: Long, sortParam: Int) {
    this.subgroupId = subgroupId
    subgroupLiveData = groupsRepository.getLiveDataSubgroupById(subgroupId)
    // Подгружаем возможные списки слов для words.
    wordsByAlphabet = wordsRepository.getWordsFromSubgroupByAlphabet(subgroupId)
    wordsByProgress = wordsRepository.getWordsFromSubgroupByProgress(subgroupId)
    // Устанавниваем начальный источник для words в зависимости от параметра сортировки.
    if (sortParam == SortWordsDialogFragment.BY_ALPHABET) {
      words.addSource(wordsByAlphabet!!, observer)
    } else {
      words.addSource(wordsByProgress!!, observer)
    }
  }

  /**
   * Удаляет подгруппу.
   */
  fun deleteSubgroup() {
    val subgroup = subgroupLiveData!!.value
    if (subgroup != null) groupsRepository.delete(subgroup)
  }

  /**
   * Обновляет поле подгруппы в БД.
   * Обновление необходимо только для параметра изучения (studied).
   */
  fun updateSubgroup() {
    Log.i(LOG_TAG, "updateSubgroup()")
    val subgroup = subgroupLiveData.value
    if (subgroup != null) {
      subgroup.studied = newIsStudied
      GlobalScope.launch {
        updateSubgroupInteractor.updateSubgroup(subgroup)
      }
      // TODO заменить на корутину (заменил, но оставил пока global scope).
      //  Подумать, когда обновлять в БД.
      //  Каждый раз при нажатии или при выходе. Надо ли тогда делать свой скоуп?
      //  Или можно каждый раз обновлять, но при этому группу запрашивать только один раз.
    }
  }

  /**
   * Устанавливает параметр изучения для подгруппы.
   *
   * @param isStudied значение параметра isChecked чекбокса.
   */
  fun setNewIsStudied(isStudied: Boolean) {
    newIsStudied = if (isStudied) {
      1
    } else {
      0
    }
  }

  /**
   * Меняет источник данных для words, создавая эффект сортировки.
   *
   * @param sortParam параметр сортировки.
   */
  fun sortWords(sortParam: Int) {
    when (sortParam) {
      SortWordsDialogFragment.BY_ALPHABET -> {
        words.removeSource(wordsByProgress!!)
        words.addSource(wordsByAlphabet!!, observer)
      }
      SortWordsDialogFragment.BY_PROGRESS -> {
        words.removeSource(wordsByAlphabet!!)
        words.addSource(wordsByProgress!!, observer)
      }
    }
  }

  /**
   * Сбрасывает прогресс по всем словам, залинкованным с данной подгруппой.
   */
  fun resetWordsProgress() {
    // TODO можно ещё всякие условия для безопасности сделать.
    wordsRepository.resetWordsProgress(subgroupId)
  }

  /**
   * Удаляет связь между текущей подгруппой и словом.
   *
   * @param wordId id слова.
   */
  fun deleteWordFromSubgroup(wordId: Long) {
    viewModelScope.launch {
      // TODO сделать выведение snackbar уже после удаления слова.
      deleteWordFromSubgroupInteractor.deleteLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }

  /**
   * Добавляет связь между текущей подгруппой и словом, которое из него удалилось.
   *
   * @param wordId id слова.
   */
  fun addWordToSubgroup(wordId: Long) {
    viewModelScope.launch {
      addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }

  fun getAvailableSubgroupsToLink(wordId: Long): MutableLiveData<ArrayList<Subgroup>?> {
    if (availableSubgroupToLink.value == null) {
      Log.d(LOG_TAG, "availableSubgroupsTo value = null")
      groupsRepository.getAvailableSubgroupTo(wordId, LinkOrDeleteWordDialogFragment.TO_LINK, this)
    }
    return availableSubgroupToLink
  }

  fun clearAvailableSubgroupsToAndRemoveObserver(observer: Observer<ArrayList<Subgroup>?>) {
    Log.d(LOG_TAG, "clearAvailableSubgroupsTo()")
    availableSubgroupToLink.value = null
    availableSubgroupToLink.removeObserver(observer)
  }

  override fun onLoaded(subgroups: ArrayList<Subgroup>?) {
    Log.d(LOG_TAG, "onLoaded()")
    availableSubgroupToLink.value = subgroups
  }

  companion object {
    private const val LOG_TAG = "SubgroupViewModel"
  }

  init {
    //subgroupMutableLiveData = new MutableLiveData<>();
    words = MediatorLiveData()
    observer = Observer { words -> this@SubgroupViewModel.words.value = words }
    availableSubgroupToLink = MutableLiveData()
  }
}