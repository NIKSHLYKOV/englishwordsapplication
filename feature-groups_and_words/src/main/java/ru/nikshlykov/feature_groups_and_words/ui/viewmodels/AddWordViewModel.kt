package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddNewWordToSubgroupInteractor

class AddWordViewModel(private val addNewWordToSubgroupInteractor: AddNewWordToSubgroupInteractor) :
  ViewModel() {

  private val _wordAdded: MutableLiveData<Boolean> = MutableLiveData(false)
  val wordAdded: LiveData<Boolean> = _wordAdded

  var subgroupId: Long = 0L
    set(value) {
      field = if (value != 0L) value else throw IllegalArgumentException()
    }

  fun addWord(word: String, transcription: String?, value: String) {
    if (subgroupId != 0L) {
      viewModelScope.launch {
        val result = addNewWordToSubgroupInteractor
          .addNewWordToSubgroup(word, transcription, value, subgroupId)
        if (result > 0L) {
          _wordAdded.value = true
        }
      }
    }
  }
}