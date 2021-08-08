package ru.nikshlykov.englishwordsapp.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.nikshlykov.englishwordsapp.R

class SortWordsDialogFragment : DialogFragment() {
  // Параметр сортировки.
  private var sortParam = 0

  // Интерфейс для общения с активити.
  interface SortWordsListener {
    fun sort(sortParam: Int)
  }

  private var sortWordsListener: SortWordsListener? = null
  override fun onAttach(context: Context) {
    super.onAttach(context)
    sortWordsListener = context as SortWordsListener
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Получаем текущий параметр сортировки, переданный из Activity.
    if (savedInstanceState != null) {
      sortParam = savedInstanceState.getInt(EXTRA_SORT_PARAM)
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    // Массив отображаемых значений сортировки для диалога.
    val sortParams = resources
      .getStringArray(R.array.preference_entries___sort_words_in_subgroup)
    return AlertDialog.Builder(requireContext())
      .setTitle(R.string.dialog___sort_words___title)
      .setSingleChoiceItems(sortParams, sortParam) { dialog, which -> sortParam = which }
      .setPositiveButton(R.string.yes) { _, _ -> sortWordsListener!!.sort(sortParam) }
      .setNegativeButton(R.string.cancel, null)
      .create()
  }

  companion object {
    // Возможные значения параметра сортировки.
    const val BY_ALPHABET = 0
    const val BY_PROGRESS = 1

    // Ключ для получения флага.
    const val EXTRA_SORT_PARAM = "SortParam"
  }
}