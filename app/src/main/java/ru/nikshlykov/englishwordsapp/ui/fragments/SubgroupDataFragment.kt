package ru.nikshlykov.englishwordsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.android.support.DaggerFragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener
import ru.nikshlykov.englishwordsapp.ui.viewmodels.SubgroupDataViewModel
import javax.inject.Inject

class SubgroupDataFragment : DaggerFragment() {
  // TODO сделать colorPicker/iconPicker для фона подгруппы.
  // View элементы.
  private var confirmButton: MaterialButton? = null
  private var subgroupNameEditText: TextInputEditText? = null
  private var subgroupId = 0L

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null
  private var subgroupDataViewModel: SubgroupDataViewModel? = null
  private var onChildFragmentInteractionListener: OnChildFragmentInteractionListener? = null
  override fun onAttach(context: Context) {
    super.onAttach(context)
    onChildFragmentInteractionListener =
      if (requireParentFragment().parentFragment is OnChildFragmentInteractionListener) {
        requireParentFragment().parentFragment as OnChildFragmentInteractionListener?
      } else {
        throw RuntimeException(requireParentFragment().parentFragment.toString() + " must implement OnChildFragmentInteractionListener")
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    subgroupDataViewModel = viewModelFactory!!.create(SubgroupDataViewModel::class.java)
    val extras = arguments
    if (extras != null) {
      subgroupId = SubgroupDataFragmentArgs.fromBundle(extras).subgroupId
      if (subgroupId != 0L) {
        subgroupDataViewModel!!.setSubgroup(subgroupId)
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_subgroup_data, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    findViews(view)
    if (subgroupId != 0L) {
      confirmButton!!.setText(R.string.to_save)
    }
    setConfirmButtonClickListener()
    subgroupDataViewModel!!.subgroup.observe(viewLifecycleOwner, Observer { subgroup ->
      if (subgroup != null) {
        subgroupNameEditText!!.setText(subgroup.name)
      }
    })
  }

  private fun setConfirmButtonClickListener() {
    confirmButton!!.setOnClickListener {
      val subgroupName = subgroupNameEditText!!.text.toString().trim { it <= ' ' }
      // Проверяем, что поле названия группы не пустое.
      if (subgroupName.isNotEmpty()) {
        subgroupDataViewModel!!.subgroupIsInsertedOrUpdated.observe(
          viewLifecycleOwner,
          { subgroupIsInsertedOrUpdated ->
            if (subgroupIsInsertedOrUpdated) {
              Toast.makeText(context, "Группа сохранена", Toast.LENGTH_SHORT).show()
              // TODO есть проблема в том, что мы используем NavDirections для
              //  popBackStack(), но, как я понимаю, можно это просто делать по
              //  нажатию на кнопку назад в FlowFragment, который будет это перехватывать.
              //  И 'нажимать' её в коде, когда нам нужно после каких-то действий
              //  переместиться назад.
              val navDirections =
                SubgroupDataFragmentDirections.actionSubgroupDataDestToGroupsDest()
              onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
            }
          })
        subgroupDataViewModel!!.addOrUpdateSubgroup(subgroupName)
      } else {
        Toast.makeText(
          context, R.string.error_new_subgroup_empty_name,
          Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  private fun findViews(view: View) {
    subgroupNameEditText =
      view.findViewById(R.id.fragment_subgroup_data___text_input_edit_text___subgroup_name)
    confirmButton = view.findViewById(R.id.fragment_subgroup_data___material_button___confirm_group)
  }
}