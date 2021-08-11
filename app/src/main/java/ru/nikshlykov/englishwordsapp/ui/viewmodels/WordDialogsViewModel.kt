package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.link.Link
import ru.nikshlykov.englishwordsapp.domain.interactors.AddWordToSubgroupInteractor

class WordDialogsViewModel(
  application: Application,
  private val groupsRepository: GroupsRepository,
  private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor
) : AndroidViewModel(application) {
  private var wordId: Long = 0
  fun setWordId(wordId: Long) {
    this.wordId = wordId
  }

  // Методы для обработки результата работы диалога.
  fun deleteLink(subgroupId: Long) {
    val linkToDelete = Link(subgroupId, wordId)
    groupsRepository.delete(linkToDelete)
  }

  fun addWordToSubgroup(subgroupId: Long) {
    // TODO Поменять потом GlobalScope на свой.
    GlobalScope.launch {
      addWordToSubgroupInteractor.addLinkBetweenWordAndSubgroup(wordId, subgroupId)
    }
  }
}