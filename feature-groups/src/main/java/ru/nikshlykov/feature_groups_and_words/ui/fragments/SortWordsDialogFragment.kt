package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.core_ui.R as CoreUiR

internal class SortWordsDialogFragment : DialogFragment() {
  private var sortParam = 0

  interface SortWordsListener {
    fun sort(sortParam: Int)
  }

  private var sortWordsListener: SortWordsListener? = null

  fun setSortWordsListener(sortWordsListener: SortWordsListener) {
    this.sortWordsListener = sortWordsListener
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    sortParam = arguments?.getInt(EXTRA_SORT_PARAM) ?: BY_ALPHABET
    Log.d("SortWordsDialogFragment", "sortParam from bundle = $sortParam")
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val sortParams = resources
      .getStringArray(R.array.preference_entries___sort_words_in_subgroup)
    return AlertDialog.Builder(requireContext())
      .setTitle(R.string.dialog___sort_words___title)
      .setSingleChoiceItems(sortParams, sortParam) { dialog, which -> sortParam = which }
      .setPositiveButton(CoreUiR.string.yes) { _, _ -> sortWordsListener?.sort(sortParam) }
      .setNegativeButton(CoreUiR.string.cancel, null)
      .create()
  }

  companion object {
    // TODO refactoring. Подобные места можно поменять на enum.
    // Возможные значения параметра сортировки.
    const val BY_ALPHABET = 0
    const val BY_PROGRESS = 1

    const val EXTRA_SORT_PARAM = "SortParam"
  }
}