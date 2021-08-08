package ru.nikshlykov.englishwordsapp.utils

import ru.nikshlykov.englishwordsapp.R

object Navigation {
  fun getModeDestinationId(modeId: Long): Int {
    return when (modeId.toInt()) {
      1, 2 -> R.id.action_global_dictionary_cards_mode_dest
      3 -> R.id.action_global_write_word_by_value_mode_dest
      4 -> R.id.action_global_collect_word_by_letters_mode_dest
      5 -> R.id.action_global_write_word_by_voice_mode_dest
      else -> R.id.action_global_dictionary_cards_mode_dest
    }
  }
}