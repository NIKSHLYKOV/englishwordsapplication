package ru.nikshlykov.englishwordsapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.link.Link

class WordDialogsViewModel(
  application: Application,
  private val groupsRepository: GroupsRepository
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

  fun insertLink(subgroupId: Long) {
    val linkToInsert = Link(subgroupId, wordId)
    groupsRepository.insert(linkToInsert)
  }
}