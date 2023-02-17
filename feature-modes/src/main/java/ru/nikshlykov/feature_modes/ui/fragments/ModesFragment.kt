package ru.nikshlykov.feature_modes.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_modes.R
import ru.nikshlykov.feature_modes.databinding.FragmentModesBinding
import ru.nikshlykov.feature_modes.navigation.ModesRouterSource
import ru.nikshlykov.feature_modes.ui.adapters.ModesRecyclerViewAdapter
import ru.nikshlykov.feature_modes.ui.viewmodels.ModesFeatureComponentViewModel
import ru.nikshlykov.feature_modes.ui.viewmodels.ModesViewModel
import javax.inject.Inject

class ModesFragment : Fragment(R.layout.fragment_modes) {

  private val modesFeatureComponentViewModel: ModesFeatureComponentViewModel by viewModels()

  private var modesViewModel: ModesViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private var adapter: ModesRecyclerViewAdapter? = null

  @Inject
  lateinit var modesRouterSource: ModesRouterSource

  private val binding: FragmentModesBinding by viewBinding(FragmentModesBinding::bind)

  override fun onAttach(context: Context) {
    modesFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    modesViewModel = viewModelFactory.create(ModesViewModel::class.java)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerViewWithAdapter()
    initSaveButton()
  }

  private fun initRecyclerViewWithAdapter() {
    val modesRecyclerView = binding.modesRecyclerView
    modesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    adapter = ModesRecyclerViewAdapter(requireContext())
    modesRecyclerView.adapter = adapter
    lifecycleScope.launch {
      modesViewModel?.loadModes()?.let { modes ->
        adapter?.setModes(modes)
      }
    }
  }

  private fun initSaveButton() {
    binding.saveModesButton.setOnClickListener { // Получаем режимы из адаптера.
      val modes = adapter?.getModes()

      // TODO refactoring. Понять, в каком порядке всё это вызывается.
      modes?.let { modesViewModel?.updateModes(modes) }

      /*// TODO решить, что делать с Router'ом. Оставлять или нет?
      modesRouterSource.close()*/
      requireActivity().onBackPressed()
    }
  }
}