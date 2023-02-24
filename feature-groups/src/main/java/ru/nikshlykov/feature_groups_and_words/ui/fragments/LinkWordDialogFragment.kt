package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.WordDialogsViewModel
import javax.inject.Inject

internal class LinkWordDialogFragment : DialogFragment() {
  // TODO refactoring. Убрать весь код, связанный с удалением слова.

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  // id слова, для которого вызывается диалог.
  private var wordId: Long = 0

  private var availableSubgroupsNames: Array<String>? = null
  private var availableSubgroupsIds: Array<Long> = emptyArray()

  private lateinit var checkedValuesOfSubgroups: BooleanArray

  private var wordDialogsViewModel: WordDialogsViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    wordDialogsViewModel = viewModelFactory.create(WordDialogsViewModel::class.java)
    dialogArguments
    wordDialogsViewModel?.setWordId(wordId)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    var availableSubgroupsCount = 0
    if (availableSubgroupsNames != null) {
      Log.d(LOG_TAG, "availableSubgroupsNames != null")
      availableSubgroupsCount = availableSubgroupsNames?.size ?: 0
    }
    Log.d(LOG_TAG, "availableSubgroupsCount = $availableSubgroupsCount")

    return if (availableSubgroupsCount != 0) {
      getAvailableSubgroupsExistDialog(availableSubgroupsCount, availableSubgroupsNames)
    } else {
      availableSubgroupsDoNotExistDialog
    }
  }

  private val dialogArguments: Unit
    get() {
      val arguments = arguments
      wordId = arguments?.getLong(EXTRA_WORD_ID) ?: 0
      Log.d(LOG_TAG, "wordId: $wordId")
      availableSubgroupsNames = arguments?.getStringArray(EXTRA_AVAILABLE_SUBGROUPS_NAMES)
      arguments?.getLongArray(EXTRA_AVAILABLE_SUBGROUPS_IDS)?.let {
        availableSubgroupsIds = it.toTypedArray()
      }
    }

  private fun getAvailableSubgroupsExistDialog(
    availableSubgroupsCount: Int,
    availableSubgroupsNames: Array<String>?
  ): AlertDialog {
    checkedValuesOfSubgroups = BooleanArray(availableSubgroupsCount)
    return AlertDialog.Builder(requireContext())
      .setTitle(R.string.dialog___link_word___title)
      .setMultiChoiceItems(
        availableSubgroupsNames,
        null
      ) { _, which, isChecked ->
        checkedValuesOfSubgroups[which] = isChecked
      }
      .setPositiveButton(R.string.dialog___link_word___positive_button) { _, _ ->
        var i = 0
        while (i < checkedValuesOfSubgroups.size) {
          if (checkedValuesOfSubgroups[i]) {
            wordDialogsViewModel?.addWordToSubgroup(availableSubgroupsIds[i])
          }
          i++
        }
      }
      .setNegativeButton(R.string.cancel, null)
      .create()
  }

  private val availableSubgroupsDoNotExistDialog: AlertDialog
    get() = AlertDialog.Builder(
      requireContext()
    )
      .setTitle(R.string.dialog___link_word___title)
      .setMessage(R.string.dialog___link_word___error_message)
      .setPositiveButton(R.string.ok, null)
      .create()

  companion object {
    private const val LOG_TAG = "NewLinkOrDeleteWordDF"

    const val EXTRA_WORD_ID = "WordId"
    const val EXTRA_AVAILABLE_SUBGROUPS_NAMES = "AvailableSubgroupsNames"
    const val EXTRA_AVAILABLE_SUBGROUPS_IDS = "AvailableSubgroupsIds"
  }
}