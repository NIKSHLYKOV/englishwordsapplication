package ru.nikshlykov.englishwordsapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.nikshlykov.englishwordsapp.R

class DeleteSubgroupDialogFragment : DialogFragment() {

  private var deleteSubgroupListener: DeleteSubgroupListener? = null

  interface DeleteSubgroupListener {
    fun deleteMessage(message: String?)
  }

  // TODO просмотреть ещё диалоговые фрагменты, в которых могут быть слушатели.
  //  Т.к. раньше были acitivity, которые их обрабатывали. А сейчас всё перешло на фрагменты.
  fun setDeleteSubgroupListener(deleteSubgroupListener: DeleteSubgroupListener) {
    this.deleteSubgroupListener = deleteSubgroupListener
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(requireActivity())
      .setTitle(R.string.dialog___delete_subgroup___title)
      .setMessage(R.string.dialog___delete_subgroup___message)
      .setPositiveButton(R.string.yes) { dialog, which -> // Отправляем Activity сообщение о том, что удаление подтверждено.
        deleteSubgroupListener!!.deleteMessage(DELETE_MESSAGE)
      }
      .setNegativeButton(R.string.no, null)
      .create()
  }

  companion object {
    // Тег для логирования.
    private const val LOG_TAG = "DeleteSubgroupDF"

    // Сообщение о том, что удаление подтверждено.
    const val DELETE_MESSAGE = "Delete"
  }
}