package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.nikshlykov.feature_groups_and_words.R

internal class DeleteSubgroupDialogFragment : DialogFragment() {

  private var deleteSubgroupListener: DeleteSubgroupListener? = null

  interface DeleteSubgroupListener {
    fun deleteMessage(message: String?)
  }

  fun setDeleteSubgroupListener(deleteSubgroupListener: DeleteSubgroupListener) {
    this.deleteSubgroupListener = deleteSubgroupListener
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(requireActivity())
      .setTitle(R.string.dialog___delete_subgroup___title)
      .setMessage(R.string.dialog___delete_subgroup___message)
      .setPositiveButton(R.string.yes) { dialog, which -> // Отправляем Activity сообщение о том, что удаление подтверждено.
        deleteSubgroupListener?.deleteMessage(DELETE_MESSAGE)
      }
      .setNegativeButton(R.string.no, null)
      .create()
  }

  companion object {
    // Сообщение о том, что удаление подтверждено.
    const val DELETE_MESSAGE = "Delete"
  }
}