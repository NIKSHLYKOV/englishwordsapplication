package ru.nikshlykov.englishwordsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.ui.adapters.ModesRecyclerViewAdapter
import ru.nikshlykov.englishwordsapp.ui.viewmodels.ModesViewModel
import javax.inject.Inject

class ModesFragment : FlowFragmentChildFragment() {
  // ViewModel для работы с БД.
  private var modesViewModel: ModesViewModel? = null

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null

  private var adapter: ModesRecyclerViewAdapter? = null

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
    modesViewModel!!.loadModes()
  }

  private fun initRecyclerViewWithAdapter(v: View) {
    // Находим RecyclerView и устанавливаем ему adapter и layoutManager.
    val modesRecyclerView = v.findViewById<RecyclerView>(R.id.activity_modes___RecyclerView)
    modesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    adapter = ModesRecyclerViewAdapter(requireContext())
    modesRecyclerView.adapter = adapter
    modesViewModel!!.modes.observe(viewLifecycleOwner, { modes ->
      if (modes != null) {
        adapter!!.setModes(modes)
      }
    })
  }

  private fun initSaveButton(v: View) {
    val saveMaterialButton =
      v.findViewById<MaterialButton>(R.id.activity_modes___material_button___save_modes)
    saveMaterialButton.setOnClickListener { // Получаем режимы из адаптера.
      val modes = adapter!!.getModes()

      // Обновляем режимы в БД.
      modes?.let { modesViewModel!!.updateModes(modes) }

      onChildFragmentInteractionListener?.close()
    }
  }
}