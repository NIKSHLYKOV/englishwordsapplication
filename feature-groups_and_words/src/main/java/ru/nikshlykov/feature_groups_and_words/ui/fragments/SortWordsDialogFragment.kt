package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.nikshlykov.feature_groups_and_words.R

class SortWordsDialogFragment : DialogFragment() {
  // Параметр сортировки.
  private var sortParam = 0

  // Интерфейс для общения.
  interface SortWordsListener {
    fun sort(sortParam: Int)
  }

  private var sortWordsListener: SortWordsListener? = null

  fun setSortWordsListener(sortWordsListener: SortWordsListener) {
    this.sortWordsListener = sortWordsListener
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Получаем текущий параметр сортировки.
    sortParam = arguments?.getInt(EXTRA_SORT_PARAM)!!
    Log.d("SortWordsDialogFragment", "sortParam from bundle = $sortParam")
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