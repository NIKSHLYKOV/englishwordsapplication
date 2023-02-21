package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

  fun setWord(word: Word) {
    this.word = MutableStateFlow(word)
  }

  val wordId: Long
    get() {
      return word.value?.id ?: 0L
    }

  fun setWordParameters(word: String, transcription: String?, value: String) {
    val currentWord = this.word.value
    if (currentWord != null) {
      currentWord.word = word
      currentWord.value = value
      currentWord.transcription = transcription
      this.word.value = currentWord
    }
  }

  private fun loadWord(wordId: Long) {
    viewModelScope.launch {
      word.emit(getWordInteractor.getWordById(wordId))
    }
  }

  suspend fun updateWordInDB(): Boolean {
    return withContext(viewModelScope.coroutineContext) {
      updateWordInteractor.updateWord(word.value) == 1
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

  suspend fun getAvailableSubgroupsTo(flag: Int): List<Subgroup> =
    withContext(viewModelScope.coroutineContext) {
      getAvailableSubgroupsInteractor.getAvailableSubgroups(wordId, flag)
    }
}