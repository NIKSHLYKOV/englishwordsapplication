package ru.nikshlykov.feature_study.utils

import androidx.navigation.NavDirections
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.NavigationStudyDirections

internal object ModesNavigation {
    fun getRandomModeNavDirections(modeId: Long, word: Word): NavDirections {
        return when (modeId.toInt()) {
            1 -> NavigationStudyDirections.actionGlobalWordDictionaryCardModeDest(word)
            2 -> NavigationStudyDirections.actionGlobalValueDictionaryCardModeDest(word)
            3 -> NavigationStudyDirections.actionGlobalWriteWordByValueModeDest(word)
            4 -> NavigationStudyDirections.actionGlobalCollectWordByLettersModeDest(word)
            5 -> NavigationStudyDirections.actionGlobalWriteWordByVoiceModeDest(word)
            else -> throw IllegalArgumentException()
        }
    }
}