package ru.nikshlykov.feature_modes.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import ru.nikshlykov.feature_modes.R
import ru.nikshlykov.feature_modes.navigation.ModesRouterSource
import ru.nikshlykov.feature_modes.ui.adapters.ModesRecyclerViewAdapter
import ru.nikshlykov.feature_modes.ui.viewmodels.ModesFeatureComponentViewModel
import ru.nikshlykov.feature_modes.ui.viewmodels.ModesViewModel
import javax.inject.Inject

class ModesFragment : Fragment() {

  private val modesFeatureComponentViewModel: ModesFeatureComponentViewModel by viewModels()

  // ViewModel для работы с БД.
  private var modesViewModel: ModesViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private var adapter: ModesRecyclerViewAdapter? = null

  @Inject
  lateinit var modesRouterSource: ModesRouterSource

  override fun onAttach(context: Context) {
    modesFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    modesViewModel = viewModelFactory!!.create(ModesViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.activity_modes, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerViewWithAdapter(view)
    initSaveButton(view)
  }

  private fun initRecyclerViewWithAdapter(v: View) {
    // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
    val modesRecyclerView = v.findViewById<RecyclerView>(R.id.activity_modes___RecyclerView)
    modesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    adapter = ModesRecyclerViewAdapter(requireContext())
    modesRecyclerView.adapter = adapter
    lifecycleScope.launch{
        adapter!!.setModes(modesViewModel?.loadModes())
    }
  }

  private fun initSaveButton(v: View) {
    val saveMaterialButton =
      v.findViewById<MaterialButton>(R.id.activity_modes___material_button___save_modes)
    saveMaterialButton.setOnClickListener { // Получаем режимы из адаптера.
      val modes = adapter!!.getModes()

      // Обновляем режимы в БД.
      modes?.let { modesViewModel!!.updateModes(modes) }

      /*// TODO решить, что делать с Router'ом. Оставлять или нет?
      modesRouterSource.close()*/
      requireActivity().onBackPressed()
    }
  }
}