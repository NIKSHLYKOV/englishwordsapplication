package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.GroupsRepository.OnSubgroupsLoadedListener
import ru.nikshlykov.englishwordsapp.db.WordsRepository
import ru.nikshlykov.englishwordsapp.db.WordsRepository.OnExamplesLoadedListener
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.domain.interactors.UpdateWordInteractor
import java.util.*

class WordViewModel(
  application: Application, private val wordsRepository: WordsRepository,
  private val groupsRepository: GroupsRepository,
  private val updateWordInteractor: UpdateWordInteractor
) : AndroidViewModel(application), OnSubgroupsLoadedListener {
  val wordMutableLiveData: MutableLiveData<Word> = MutableLiveData()

  // Список подгрупп для добавления или удаления связи с ними.
  private val availableSubgroupsTo: MutableLiveData<ArrayList<Subgroup>?> = MutableLiveData()

  // Слово
  fun setWord(word: Word) {
    wordMutableLiveData.value = word
  }

  val wordId: Long
    get() {
      val word = wordMutableLiveData.value
      return word?.id ?: 0L
    }

  fun setWordParameters(word: String?, transcription: String?, value: String?) {
    val currentWord = wordMutableLiveData.value
    if (currentWord != null) {
      currentWord.word = word!!
      currentWord.value = value!!
      currentWord.transcription = transcription
      wordMutableLiveData.value = currentWord
    }
  }

  fun updateWordInDB() {
    // TODO проверить работу, т.к. нужны свои слова для этого
    GlobalScope.launch {
      val word = wordMutableLiveData.value
      if (word != null) {
        updateWordInteractor.updateWord(word)
      }
    }
  }

  /**
   * Сбрасывает прогресс по слову.
   */
  fun resetProgress() {
    viewModelScope.launch {
      val word = wordMutableLiveData.value
      if (word != null) {
        word.learnProgress = -1
        updateWordInteractor.updateWord(word)
        wordMutableLiveData.value = word
      }
    }
  }

  // Связывание с пользовательскими подгруппами
  fun getAvailableSubgroupsTo(flag: Int): MutableLiveData<ArrayList<Subgroup>?> {
    if (availableSubgroupsTo.value == null) {
      Log.d(LOG_TAG, "availableSubgroupsTo value = null")
      val word = wordMutableLiveData.value
      if (word != null) {
        groupsRepository.getAvailableSubgroupTo(word.id, flag, this)
      }
    }
    return availableSubgroupsTo
  }

  fun clearAvailableSubgroupsToAndRemoveObserver(observer: Observer<ArrayList<Subgroup>?>) {
    Log.d(LOG_TAG, "clearAvailableSubgroupsTo()")
    availableSubgroupsTo.value = null
    availableSubgroupsTo.removeObserver(observer!!)
  }

  override fun onLoaded(subgroups: ArrayList<Subgroup>?) {
    Log.d(LOG_TAG, "onLoaded()")
    availableSubgroupsTo.value = subgroups
  }

  // Примеры
  fun getExamples(listener: OnExamplesLoadedListener?) {
    val word = wordMutableLiveData.value
    if (word != null) {
      wordsRepository.getExamplesByWordId(word.id, listener!!)
    }
  }

  companion object {
    private const val LOG_TAG = "WordViewModel"
  }
}