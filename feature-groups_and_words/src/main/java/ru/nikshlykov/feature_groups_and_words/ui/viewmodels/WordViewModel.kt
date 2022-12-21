package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetAvailableSubgroupsInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetWordInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.ResetWordProgressInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.UpdateWordInteractor

internal class WordViewModel(
  private val getWordInteractor: GetWordInteractor,
  private val updateWordInteractor: UpdateWordInteractor,
  private val resetWordProgressInteractor: ResetWordProgressInteractor,
  private val getAvailableSubgroupsInteractor: GetAvailableSubgroupsInteractor
) : ViewModel() {
  lateinit var word: MutableStateFlow<Word>

  // Список подгрупп для добавления или удаления связи с ними.
  private val availableSubgroupsTo: MutableLiveData<ArrayList<Subgroup>?> = MutableLiveData()

  fun setWord(word: Word) {
    this.word = MutableStateFlow(word)
  }

  val wordId: Long
    get() {
      val word = word.value
      return word?.id ?: 0L
    }

  fun setWordParameters(word: String?, transcription: String?, value: String?) {
    val currentWord = this.word.value
    if (currentWord != null) {
      currentWord.word = word!!
      currentWord.value = value!!
      currentWord.transcription = transcription
      this.word.value = currentWord
    }
  }

  private fun loadWord(wordId: Long) {
    viewModelScope.launch {
      word.emit(getWordInteractor.getWordById(wordId))
    }
  }

  fun updateWordInDB() {
    GlobalScope.launch {
      val word = word.value
      if (word != null) {
        updateWordInteractor.updateWord(word)
      }
    }
  }

  fun resetProgress() {
    viewModelScope.launch {
      val word = word.value
      if (word != null) {
        val resetResult = resetWordProgressInteractor.resetWordProgress(wordId)
        if (resetResult == 1) {
          this@WordViewModel.word.value.id.let { loadWord(it) }
        }
      }
    }
  }

  fun getAvailableSubgroupsTo(flag: Int): MutableLiveData<ArrayList<Subgroup>?> {
    if (availableSubgroupsTo.value == null) {
      Log.d(LOG_TAG, "availableSubgroupsTo value = null")
      val word = word.value
      if (word != null) {
        viewModelScope.launch {
          // TODO порабоать над флагом, значения должны быть в одном месте.
          availableSubgroupsTo.value =
            ArrayList(getAvailableSubgroupsInteractor.getAvailableSubgroups(wordId, flag))
        }
      }
    }
    return availableSubgroupsTo
  }

  fun clearAvailableSubgroupsToAndRemoveObserver(observer: Observer<ArrayList<Subgroup>?>) {
    Log.d(LOG_TAG, "clearAvailableSubgroupsTo()")
    availableSubgroupsTo.value = null
    availableSubgroupsTo.removeObserver(observer!!)
  }

  companion object {
    private const val LOG_TAG = "WordViewModel"
  }
}