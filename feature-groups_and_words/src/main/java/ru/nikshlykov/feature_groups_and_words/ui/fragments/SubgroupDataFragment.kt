package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.SubgroupDataViewModel
import javax.inject.Inject

internal class SubgroupDataFragment : FlowFragmentChildFragment() {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  // TODO сделать решить проблему с большими фото. приложение вылетает
  private var confirmButton: MaterialButton? = null
  private var subgroupImage: ShapeableImageView? = null
  private var subgroupNameEditText: TextInputEditText? = null
  private var subgroupId = 0L

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory
  private var subgroupDataViewModel: SubgroupDataViewModel? = null

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    subgroupDataViewModel = viewModelFactory!!.create(SubgroupDataViewModel::class.java)
    val extras = arguments
    if (extras != null) {
      subgroupId = SubgroupDataFragmentArgs.fromBundle(extras).subgroupId
      if (subgroupId != 0L) {
        subgroupDataViewModel!!.loadSubgroupAndPhoto(requireContext(), subgroupId)
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
    setSubgroupImageViewClickListener()
    setConfirmButtonClickListener()
    subgroupDataViewModel!!.subgroup.observe(viewLifecycleOwner, Observer { subgroup ->
      if (subgroup != null) {
        subgroupNameEditText!!.setText(subgroup.name)
      }
    })

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        subgroupDataViewModel!!.subgroupImage.collectLatest { image ->
          if (image != null) {
            subgroupImage?.setImageBitmap(image)
          }
        }
      }
    }
  }

  private fun setSubgroupImageViewClickListener() {
    subgroupImage!!.setOnClickListener {
      val imagePickIntent = Intent().apply {
        action = Intent.ACTION_GET_CONTENT
        type = "image/*"
      }
      startActivityForResult(imagePickIntent, REQUEST_CODE_PICK_IMAGE)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE_PICK_IMAGE) {
      subgroupImage?.setImageURI(data?.data)
      subgroupDataViewModel!!.setSubgroupNewImage(
        MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data?.data)
      )
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  private fun setConfirmButtonClickListener() {
    confirmButton!!.setOnClickListener {
      val subgroupName = subgroupNameEditText!!.text.toString().trim { it <= ' ' }
      if (subgroupName.isNotEmpty()) {
        subgroupDataViewModel!!.subgroupIsInsertedOrUpdated.observe(
          viewLifecycleOwner,
          { subgroupIsInsertedOrUpdated ->
            if (subgroupIsInsertedOrUpdated) {
              Toast.makeText(context, "Группа сохранена", Toast.LENGTH_SHORT).show()
              onChildFragmentInteractionListener!!.close()
            }
          })
        subgroupDataViewModel!!.addOrUpdateSubgroup(subgroupName, requireContext())
      } else {
        Toast.makeText(
          context, R.string.error_new_subgroup_empty_name,
          Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  private fun findViews(view: View) {
    subgroupImage = view.findViewById(R.id.fragment_subgroup_data___image_view___subgroup_image)
    subgroupNameEditText =
      view.findViewById(R.id.fragment_subgroup_data___text_input_edit_text___subgroup_name)
    confirmButton = view.findViewById(R.id.fragment_subgroup_data___material_button___confirm_group)
  }

  companion object {
    private const val REQUEST_CODE_PICK_IMAGE = 1
  }
}