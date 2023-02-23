package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.interactors.*
import ru.nikshlykov.feature_groups_and_words.ui.fragments.SortWordsDialogFragment

internal class SubgroupViewModel(
  private val getSubgroupInteractor: GetSubgroupInteractor,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
  private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor,
  private val deleteSubgroupInteractor: DeleteSubgroupInteractor,
  private val getWordsFromSubgroupInteractor: GetWordsFromSubgroupInteractor,
  private val resetWordsProgressFromSubgroupInteractor: ResetWordsProgressFromSubgroupInteractor,
  private val getAvailableSubgroupsInteractor: GetAvailableSubgroupsInteractor
) : ViewModel() {

  private val _subgroupFlow: MutableStateFlow<Subgroup?> = MutableStateFlow(null)
  val subgroupFlow: StateFlow<Subgroup?> = _subgroupFlow

  private var subgroupId: Long = 0
  private var newIsStudied = 0

  val wordsFlow: MutableStateFlow<List<Word>> = MutableStateFlow(emptyList())

  private var wordsByAlphabetFlow: StateFlow<List<Word>> = MutableStateFlow(emptyList())
  private var wordsByProgressFlow: StateFlow<List<Word>> = MutableStateFlow(emptyList())

  fun loadSubgroupAndWords(subgroupId: Long, sortParam: Int) {
    this.subgroupId = subgroupId
    viewModelScope.launch {
      _subgroupFlow.emit(getSubgroupInteractor.getSubgroupById(subgroupId))
    }

    wordsByAlphabetFlow =
      getWordsFromSubgroupInteractor.getWordsFromSubgroupByAlphabetFlow(subgroupId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    wordsByProgressFlow =
      getWordsFromSubgroupInteractor.getWordsFromSubgroupByProgressFlow(subgroupId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // TODO подумать потом над этим кодом. Было бы классно сделать получше.
    when (sortParam) {
      SortWordsDialogFragment.BY_ALPHABET ->
        viewModelScope.launch {
          wordsByAlphabetFlow.collectLatest { words ->
            wordsFlow.emit(words)
          }
        }
      else                                ->
        viewModelScope.launch {
          wordsByProgressFlow.collectLatest { words ->
            wordsFlow.emit(words)
          }
        }
    }
  }

  suspend fun deleteSubgroup(): Boolean =
    withContext(viewModelScope.coroutineContext) {
      val subgroup = _subgroupFlow.value
      if (subgroup != null && subgroup.isCreatedByUser) {
        deleteSubgroupInteractor.deleteSubgroup(subgroup) == 1
      } else {
        false
      }
    }

  fun updateSubgroup() {
    // Обновление подгруппы необходимо только для параметра изучения (studied).
    Log.i(LOG_TAG, "updateSubgroup()")
    val subgroup = _subgroupFlow.value
    if (subgroup != null) {
      subgroup.studied = newIsStudied
      viewModelScope.launch {
        updateSubgroupInteractor.updateSubgroup(subgroup)
      }
      // TODO Подумать, когда обновлять в БД.
      //  Каждый раз при нажатии или при выходе. Надо ли тогда делать свой скоуп?
      //  Или можно каждый раз обновлять, но при этому группу запрашивать только один раз.
    }
  }

  fun setNewIsStudied(isStudied: Boolean) {
    newIsStudied = if (isStudied) {
      1
    } else {
      0
    }
  }

  fun sortWords(sortParam: Int) {
    when (sortParam) {
      SortWordsDialogFragment.BY_ALPHABET -> {
        viewModelScope.launch { wordsFlow.emit(wordsByAlphabetFlow.value) }
      }
      SortWordsDialogFragment.BY_PROGRESS -> {
        viewModelScope.launch { wordsFlow.emit(wordsByProgressFlow.value) }
      }
    }
  }


  fun resetWordsProgress() {
    // TODO можно ещё всякие условия для безопасности сделать.
    viewModelScope.launch {
      resetWordsProgressFromSubgroupInteractor.resetWordsProgressFromSubgroup(subgroupId)
    }
  }


  fun deleteWordFromSubgroup(wordId: Long) {
    viewModelScope.launch {
      // TODO сделать выведение snackbar уже после удаления слова.
      deleteWordFromSubgroupInteractor.deleteLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }

  fun addWordToSubgroup(wordId: Long) {
    viewModelScope.launch {
      addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }

  suspend fun getAvailableSubgroupsToLink(wordId: Long): List<Subgroup> =
    withContext(viewModelScope.coroutineContext) {
      getAvailableSubgroupsInteractor.getAvailableSubgroups(
        wordId,
        GetAvailableSubgroupsInteractor.TO_LINK
      )
    }

  companion object {
    private const val LOG_TAG = "SubgroupViewModel"
  }
}