package ru.nikshlykov.englishwordsapp.utils

import androidx.navigation.NavDirections
import ru.nikshlykov.englishwordsapp.NavigationStudyDirections
import ru.nikshlykov.data.database.models.Word

object ModesNavigation {
  fun getRandomModeNavDirections(modeId: Long, word: Word): NavDirections {
    return when (modeId.toInt()) {
      1, 2 -> NavigationStudyDirections.actionGlobalDictionaryCardsModeDest(word)
      3 -> NavigationStudyDirections.actionGlobalWriteWordByValueModeDest(word)
      4 -> NavigationStudyDirections.actionGlobalCollectWordByLettersModeDest(word)
      5 -> NavigationStudyDirections.actionGlobalWriteWordByVoiceModeDest(word)
      else -> throw IllegalArgumentException()
    }
  }
}