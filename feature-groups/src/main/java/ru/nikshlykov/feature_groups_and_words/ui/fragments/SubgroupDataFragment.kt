package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.databinding.FragmentSubgroupDataBinding
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.SubgroupDataViewModel
import javax.inject.Inject

internal class SubgroupDataFragment : FlowFragmentChildFragment(R.layout.fragment_subgroup_data) {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  private var subgroupId = 0L

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory
  private var subgroupDataViewModel: SubgroupDataViewModel? = null

  private val binding: FragmentSubgroupDataBinding by viewBinding(FragmentSubgroupDataBinding::bind)

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    subgroupDataViewModel = viewModelFactory.create(SubgroupDataViewModel::class.java)
    val extras = arguments
    if (extras != null) {
      subgroupId = SubgroupDataFragmentArgs.fromBundle(extras).subgroupId
      if (subgroupId != 0L) {
        subgroupDataViewModel?.loadSubgroupAndPhoto(requireContext(), subgroupId)
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (subgroupId != 0L) {
      binding.saveSubgroupButton.setText(R.string.to_save)
    }
    setSubgroupImageViewClickListener()
    setConfirmButtonClickListener()
    subgroupDataViewModel?.subgroup?.observe(viewLifecycleOwner) { subgroup ->
      if (subgroup != null) {
        binding.subgroupNameEditText.setText(subgroup.name)
      }
    }

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        subgroupDataViewModel?.subgroupImage?.collectLatest { image ->
          if (image != null) {
            // Здесь используется Glide, т.к. приложение вылетало при использовании setImageBitmap()
            Glide.with(this@SubgroupDataFragment)
              .load(image)
              .placeholder(R.drawable.shape_load_picture)
              .error(requireContext().getDrawable(R.drawable.user_subgroups_default_color))
              .into(binding.subgroupImage)
          }
        }
      }
    }
  }

  private fun setSubgroupImageViewClickListener() {
    binding.subgroupImage.setOnClickListener {
      val imagePickIntent = Intent().apply {
        action = Intent.ACTION_GET_CONTENT
        type = "image/*"
      }
      startActivityForResult(imagePickIntent, REQUEST_CODE_PICK_IMAGE)
    }
  }

  // TODO Fix. Большие вертикальные фото почему-то переворачиваются при получении.
  //  https://stackoverflow.com/questions/3647993/android-bitmaps-loaded-from-gallery-are-rotated-in-imageview
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE_PICK_IMAGE && data != null) {
      subgroupDataViewModel?.setSubgroupNewImage(
        MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.data)
      )
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  private fun setConfirmButtonClickListener() {
    binding.saveSubgroupButton.setOnClickListener {
      val subgroupName = binding.subgroupNameEditText.text.toString().trim { it <= ' ' }
      if (subgroupName.isNotEmpty()) {
        subgroupDataViewModel?.subgroupIsInsertedOrUpdated?.observe(
          viewLifecycleOwner
        ) { subgroupIsInsertedOrUpdated ->
          if (subgroupIsInsertedOrUpdated) {
            Toast.makeText(context, "Группа сохранена", Toast.LENGTH_SHORT).show()
            onChildFragmentInteractionListener?.close()
          }
        }
        subgroupDataViewModel?.addOrUpdateSubgroup(subgroupName, requireContext())
      } else {
        Toast.makeText(
          context, R.string.error_new_subgroup_empty_name,
          Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  companion object {
    private const val REQUEST_CODE_PICK_IMAGE = 1
  }
}