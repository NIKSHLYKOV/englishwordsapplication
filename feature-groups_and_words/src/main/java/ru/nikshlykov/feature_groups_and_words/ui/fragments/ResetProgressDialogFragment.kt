package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.nikshlykov.feature_groups_and_words.R

internal class ResetProgressDialogFragment : DialogFragment() {
  // Флаг, который отвечает за выводимые заголовок и сообщение (либо только для одного слова,
  // либо для целой подгруппы).
  private var flag = 0

  interface ResetProgressListener {
    fun resetMessage(message: String?)
  }

  private var resetProgressListener: ResetProgressListener? = null

  fun setResetProgressListener(resetProgressListener: ResetProgressListener) {
    this.resetProgressListener = resetProgressListener
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    getFlag()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return when (flag) {
      FOR_ONE_WORD -> getResetDialog(
        R.string.dialog___reset_word_progress___title,
        R.string.dialog___reset_word_progress___message
      )
      FOR_SUBGROUP -> getResetDialog(
        R.string.dialog___reset_words_progress___title,
        R.string.dialog___reset_words_progress___message
      )
      else         -> errorDialog
    }
  }

  private fun getResetDialog(dialogTitle: Int, dialogMessage: Int): AlertDialog {
    return AlertDialog.Builder(requireContext())
      .setTitle(dialogTitle)
      .setMessage(dialogMessage)
      .setPositiveButton(R.string.yes) { dialog, which ->
        resetProgressListener!!.resetMessage(RESET_MESSAGE)
      }
      .setNegativeButton(R.string.no, null)
      .create()
  }

  private val errorDialog: AlertDialog
    get() = AlertDialog.Builder(requireContext())
      .setTitle(R.string.sorry_error_happened)
      .setPositiveButton(R.string.ok, null)
      .create()

  private fun getFlag() {
    try {
      flag = requireArguments().getInt(EXTRA_FLAG)
    } catch (e: NullPointerException) {
      Log.e(LOG_TAG, e.message!!)
    }
  }

  companion object {
    private const val LOG_TAG = "ResetWordProgressDF"

    const val EXTRA_FLAG = "Flag"

    // Возможные значения флага.
    const val FOR_SUBGROUP = 1
    const val FOR_ONE_WORD = 2

    const val RESET_MESSAGE = "Reset"
  }
}