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

internal class LinkOrDeleteWordDialogFragment : DialogFragment() {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  // Флаг, который отвечает за подбираемые подгруппы.
  private var flag = 0

  // id слова, для которого вызывается диалог.
  private var wordId: Long = 0
  private var availableSubgroupsNames: Array<String>? = null
  private var availableSubgroupsIds: LongArray? = null

  // Массив значений чекбоксов подгрупп.
  private lateinit var checkedSubgroups: BooleanArray

  // ViewModel для работы с БД.
  private var wordDialogsViewModel: WordDialogsViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    Log.d(LOG_TAG, "onCreate")
    super.onCreate(savedInstanceState)
    wordDialogsViewModel = viewModelFactory!!.create(WordDialogsViewModel::class.java)
    dialogArguments
    wordDialogsViewModel!!.setWordId(wordId)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    Log.d(LOG_TAG, "onCreateDialog")
    // Получаем названия доступных подгрупп.
    var availableSubgroupsCount = 0
    if (availableSubgroupsNames != null) {
      Log.d(LOG_TAG, "availableSubgroupsNames != null")
      availableSubgroupsCount = availableSubgroupsNames!!.size
    }
    Log.d(LOG_TAG, "availableSubgroupsCount = $availableSubgroupsCount")

    // Выводим dialog в зависимости от того, есть доступные подгруппы или их нет.
    return if (availableSubgroupsCount != 0) {
      getAvailableSubgroupsExistDialog(availableSubgroupsCount, availableSubgroupsNames)
    } else {
      availableSubgroupsDoNotExistDialog
    }
  }

  // Получаем id слова.
  private val dialogArguments: Unit
    get() {
      val arguments = arguments
      // Получаем id слова.
      try {
        wordId = arguments!!.getLong(EXTRA_WORD_ID)
        Log.d(LOG_TAG, "wordId: $wordId")
        flag = arguments.getInt(EXTRA_FLAG)
        Log.d(LOG_TAG, "Flag: $flag")
        availableSubgroupsNames = arguments.getStringArray(EXTRA_AVAILABLE_SUBGROUPS_NAMES)
        availableSubgroupsIds = arguments.getLongArray(EXTRA_AVAILABLE_SUBGROUPS_IDS)
        for (i in availableSubgroupsNames!!.indices) {
          Log.d(
            LOG_TAG,
            "Subgroup " + i + ": id=" + availableSubgroupsIds!![i] + "; name=" + availableSubgroupsNames!![i]
          )
        }
      } catch (e: NullPointerException) {
        Log.e(LOG_TAG, e.message!!)
      }
    }

  private fun getAvailableSubgroupsExistDialog(
    availableSubgroupsCount: Int,
    availableSubgroupsNames: Array<String>?
  ): AlertDialog {
    checkedSubgroups = BooleanArray(availableSubgroupsCount)
    return when (flag) {
      TO_LINK ->                 // Возвращаем диалог с подгруппами, доступными для связывания с ними.
        AlertDialog.Builder(requireContext())
          .setTitle(R.string.dialog___link_word___title)
          .setMultiChoiceItems(
            availableSubgroupsNames,
            null
          ) { _, which, isChecked -> // Меняем значение в массиве значений чекбоксов.
            checkedSubgroups[which] = isChecked
          }
          .setPositiveButton(R.string.dialog___link_word___positive_button) { _, _ -> // Добавляем связь между подгруппой и словом, если чекбокс выставлен.
            var i = 0
            while (i < checkedSubgroups.size) {
              if (checkedSubgroups[i]) {
                wordDialogsViewModel!!.addWordToSubgroup(availableSubgroupsIds!![i])
              }
              i++
            }
          }
          .setNegativeButton(R.string.cancel, null)
          .create()
      TO_DELETE ->                 // Возвращаем диалог с подгруппами, доступными для удаления из них слова.
        AlertDialog.Builder(requireContext())
          .setTitle(R.string.dialog___delete_word___title)
          .setMultiChoiceItems(
            availableSubgroupsNames,
            null
          ) { _, which, isChecked -> // Меняем значение в массиве значений чекбоксов.
            checkedSubgroups[which] = isChecked
          }
          .setPositiveButton(R.string.dialog___delete_word___positive_button) { _, _ -> // Удаляем связь между подгруппой и словом, если чекбокс выставлен.
            var i = 0
            while (i < checkedSubgroups.size) {
              if (checkedSubgroups[i]) {
                wordDialogsViewModel!!.deleteWordFromSubgroup(availableSubgroupsIds!![i])
              }
              i++
            }
          }
          .setNegativeButton(R.string.cancel, null)
          .create()
      else      -> errorDialog
    }
  }

  private val availableSubgroupsDoNotExistDialog: AlertDialog
    get() = when (flag) {
      TO_DELETE -> AlertDialog.Builder(
        requireContext()
      )
        .setTitle(R.string.dialog___delete_word___title)
        .setMessage(R.string.dialog___delete_word___error_message)
        .setPositiveButton(R.string.ok, null)
        .create()
      TO_LINK -> AlertDialog.Builder(
        requireContext()
      )
        .setTitle(R.string.dialog___link_word___title)
        .setMessage(R.string.dialog___link_word___error_message)
        .setPositiveButton(R.string.ok, null)
        .create()
      else      -> errorDialog
    }
  private val errorDialog: AlertDialog
    get() = AlertDialog.Builder(requireContext())
      .setTitle(R.string.sorry_error_happened)
      .setPositiveButton(R.string.ok, null)
      .create()

  companion object {
    // Тег для логирования.
    private const val LOG_TAG = "NewLinkOrDeleteWordDF"

    // Ключи для получения аргументов.
    const val EXTRA_FLAG = "Flag"
    const val EXTRA_WORD_ID = "WordId"
    const val EXTRA_AVAILABLE_SUBGROUPS_NAMES = "AvailableSubgroupsNames"
    const val EXTRA_AVAILABLE_SUBGROUPS_IDS = "AvailableSubgroupsIds"

    // Возможные значения флага.
    const val TO_LINK = 1
    const val TO_DELETE = 2
  }
}