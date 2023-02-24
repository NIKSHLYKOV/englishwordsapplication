package ru.nikshlykov.feature_word.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.nikshlykov.feature_word.R

internal class ResetProgressDialogFragment : DialogFragment() {

  interface ResetProgressListener {
    fun resetMessage(message: String?)
  }

  private var resetProgressListener: ResetProgressListener? = null

  fun setResetProgressListener(resetProgressListener: ResetProgressListener) {
    this.resetProgressListener = resetProgressListener
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(requireContext())
      .setTitle(R.string.dialog___reset_word_progress___title)
      .setMessage(R.string.dialog___reset_word_progress___message)
      .setPositiveButton(R.string.yes) { dialog, which ->
        resetProgressListener?.resetMessage(RESET_PROGRESS_CONFIRMED_MESSAGE)
      }
      .setNegativeButton(R.string.no, null)
      .create()
  }

  companion object {
    const val RESET_PROGRESS_CONFIRMED_MESSAGE = "Reset"
  }
}