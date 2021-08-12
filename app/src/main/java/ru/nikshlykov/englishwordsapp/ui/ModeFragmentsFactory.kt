package ru.nikshlykov.englishwordsapp.ui

import android.content.Context
import androidx.fragment.app.Fragment
import ru.nikshlykov.englishwordsapp.ui.fragments.modesfragments.*

// Перечисление режимов.
// Необходимо для того, чтобы возвращать определённый вновь созданный фрагмент по id режима.
enum class ModeFragmentsFactory(private val id: Long) {
  VOC_CARD_ENG_TO_RUS(1L) {
    override fun createFragment(context: Context?): Fragment {
      val fragment = DictionaryCardsModeFragment()
      fragment.setFlag(DictionaryCardsModeFragment.FLAG_ENG_TO_RUS)
      return fragment
    }
  },
  VOC_CARD_RUS_TO_ENG(2L) {
    override fun createFragment(context: Context?): Fragment {
      val fragment = DictionaryCardsModeFragment()
      fragment.setFlag(DictionaryCardsModeFragment.FLAG_RUS_TO_ENG)
      return fragment
    }
  },
  WRITE_WORD_BY_VALUE(3L) {
    override fun createFragment(context: Context?): Fragment {
      return WriteWordByValueModeFragment()
    }
  },
  COLLECT_WORD_BY_LETTERS(4L) {
    override fun createFragment(context: Context?): Fragment {
      return CollectWordByLettersModeFragment()
    }
  },
  WRITE_WORD_BY_VOICE(5L) {
    override fun createFragment(context: Context?): Fragment {
      return WriteWordByVoiceModeFragment()
    }
  },
  CHOOSE_FROM_FOUR_VARIANTS(6L) {
    override fun createFragment(context: Context?): Fragment {
      return ChooseFromFourVariantsModeFragment()
    }
  };

  abstract fun createFragment(context: Context?): Fragment

  companion object {
    fun byId(id: Long): ModeFragmentsFactory {
      for (mode in values()) {
        if (mode.id == id) {
          return mode
        }
      }
      throw IllegalStateException()
    }
  }
}