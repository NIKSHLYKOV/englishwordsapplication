package ru.nikshlykov.feature_study.utils

import androidx.navigation.NavDirections
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.NavigationStudyDirections
import ru.nikshlykov.feature_study.ui.fragments.modesfragments.DictionaryCardsModeFragment

internal object ModesNavigation {
  fun getRandomModeNavDirections(modeId: Long, word: Word): NavDirections {
    return when (modeId.toInt()) {
      1    -> NavigationStudyDirections.actionGlobalDictionaryCardsModeDest(
        word,
        DictionaryCardsModeFragment.FLAG_ENG_TO_RUS
      )
      2    -> NavigationStudyDirections.actionGlobalDictionaryCardsModeDest(
        word,
        DictionaryCardsModeFragment.FLAG_RUS_TO_ENG
      )
      3    -> NavigationStudyDirections.actionGlobalWriteWordByValueModeDest(word)
      4    -> NavigationStudyDirections.actionGlobalCollectWordByLettersModeDest(word)
      5    -> NavigationStudyDirections.actionGlobalWriteWordByVoiceModeDest(word)
      else -> throw IllegalArgumentException()
    }
  }
}