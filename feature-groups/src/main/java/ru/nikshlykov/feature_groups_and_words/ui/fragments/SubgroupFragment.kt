package ru.nikshlykov.feature_groups_and_words.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.nikshlykov.core_network.SubgroupImages
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_groups_and_words.R
import ru.nikshlykov.feature_groups_and_words.databinding.FragmentSubgroupBinding
import ru.nikshlykov.feature_groups_and_words.di.GroupsFeatureComponentViewModel
import ru.nikshlykov.feature_groups_and_words.ui.adapters.WordsRecyclerViewAdapter
import ru.nikshlykov.feature_groups_and_words.ui.viewmodels.SubgroupViewModel
import java.io.File
import javax.inject.Inject

internal class SubgroupFragment : FlowFragmentChildFragment(R.layout.fragment_subgroup),
  SortWordsDialogFragment.SortWordsListener,
  ResetWordsProgressDialogFragment.ResetProgressListener,
  DeleteSubgroupDialogFragment.DeleteSubgroupListener {

  private val groupsFeatureComponentViewModel: GroupsFeatureComponentViewModel by viewModels()

  @Inject
  lateinit var adapter: WordsRecyclerViewAdapter

  private var deleteIcon: Drawable? = null
  private var linkIcon: Drawable? = null

  private var subgroupViewModel: SubgroupViewModel? = null

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private var subgroupId: Long = 0
  private var subgroupIsStudied = false
  private var subgroupIsCreatedByUser = false
  private var deleteFlag = false

  private var sortParam = 0

  private val binding: FragmentSubgroupBinding by viewBinding(FragmentSubgroupBinding::bind)

  var subgroupFragmentRouter: SubgroupFragmentRouter? = null

  interface SubgroupFragmentRouter {
    fun openWord(word: Word)
  }

  override fun onAttach(context: Context) {
    groupsFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
    if (requireParentFragment().parentFragment is SubgroupFragmentRouter) {
      subgroupFragmentRouter = requireParentFragment().parentFragment as SubgroupFragmentRouter?
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    subgroupViewModel = viewModelFactory.create(SubgroupViewModel::class.java)

    bundleArguments

    subgroupViewModel?.setNewIsStudied(subgroupIsStudied)
    sortParam = getSortParam()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val v = inflater.inflate(R.layout.fragment_subgroup, container, false)
    setHasOptionsMenu(true)
    return v
  }

  @SuppressLint("UnsafeRepeatOnLifecycleDetector")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)
    initCreateWordFAB()

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        subgroupViewModel?.subgroupFlow?.collectLatest { subgroup ->
          if (subgroup != null) {
            val toolbarLayout = binding.collapsingToolbar
            toolbarLayout.title = subgroup.name
            toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingToolbarCollapseTitle)
            toolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsingToolbarExpandedTitle)
            setSubgroupImage(subgroup)

            initSwipeIcons()
            ItemTouchHelper(createMySimpleCallbackBySubgroup(subgroup))
              .attachToRecyclerView(binding.wordsRecyclerView)
          }
        }
      }
    }

    initRecyclerView()
    initRecyclerViewAdapter()
    binding.wordsRecyclerView.adapter = adapter

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        subgroupViewModel?.wordsFlow?.collectLatest { words ->
          if (!deleteFlag) {
            binding.infoText.text =
              if (words.isEmpty()) {
                if (!subgroupIsCreatedByUser) {
                  "Здесь скоро появятся слова. Пожалуйста, подождите обновления!"
                } else {
                  "Добавьте слова в группу, чтобы видеть их здесь"
                }
              } else {
                ""
              }
            adapter.setWords(words)
          }
        }
      }
    }
    subgroupViewModel?.loadSubgroupAndWords(subgroupId, sortParam)
  }

  override fun onResume() {
    super.onResume()
    if (subgroupIsCreatedByUser) {
      subgroupViewModel?.subgroupFlow?.value?.let {
        setSubgroupImage(it)
      }
    }
  }

  override fun onPause() {
    super.onPause()
    Log.i(LOG_TAG, "onPause()")
    if (!deleteFlag) {
      subgroupViewModel?.updateSubgroup()
      saveSortParam(sortParam)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    Log.d(LOG_TAG, "onCreateOptionsMenu()")
    inflater.inflate(R.menu.fragment_subgroup_toolbar_menu, menu)
    if (subgroupIsStudied) {
      menu.findItem(R.id.fragment_subgroup___action___learn)
        .setChecked(true).icon = requireContext().getDrawable(R.drawable.ic_brain_selected_yellow)
    }
    if (!subgroupIsCreatedByUser) {
      menu.findItem(R.id.fragment_subgroup___action___delete_subgroup).isVisible = false
      menu.findItem(R.id.fragment_subgroup___action___edit_subgroup).isVisible = false
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val manager = requireActivity().supportFragmentManager
    return when (item.itemId) {
      R.id.fragment_subgroup___action___learn                -> {
        if (item.isChecked) {
          item.isChecked = false
          item.icon = requireContext().getDrawable(R.drawable.ic_brain_not_selected)
          subgroupViewModel?.setNewIsStudied(false)
        } else {
          item.isChecked = true
          item.icon = requireContext().getDrawable(R.drawable.ic_brain_selected_yellow)
          subgroupViewModel?.setNewIsStudied(true)
        }
        true
      }
      R.id.fragment_subgroup___action___sort                 -> {
        Log.d(LOG_TAG, "sort words")
        val sortWordsDialogFragment = SortWordsDialogFragment()
        sortWordsDialogFragment.setSortWordsListener(this)
        val sortWordsDialogArguments = Bundle()
        sortWordsDialogArguments.putInt(SortWordsDialogFragment.EXTRA_SORT_PARAM, sortParam)
        sortWordsDialogFragment.arguments = sortWordsDialogArguments
        sortWordsDialogFragment.show(manager, DIALOG_SORT_WORDS)
        true
      }
      R.id.fragment_subgroup___action___edit_subgroup        -> {
        Log.d(LOG_TAG, "edit subgroup")
        val navDirections: NavDirections =
          SubgroupFragmentDirections.actionSubgroupDestToSubgroupDataDest(subgroupId)
        onChildFragmentInteractionListener?.onChildFragmentInteraction(navDirections)
        true
      }
      R.id.fragment_subgroup___action___reset_words_progress -> {
        Log.d(LOG_TAG, "Reset words progress")
        val resetWordsProgressDialogFragment = ResetWordsProgressDialogFragment()
        resetWordsProgressDialogFragment.setResetProgressListener(this)
        resetWordsProgressDialogFragment.show(
          manager,
          DIALOG_RESET_WORDS_PROGRESS
        )
        true
      }
      R.id.fragment_subgroup___action___delete_subgroup      -> {
        Log.d(LOG_TAG, "Delete subgroup")
        val deleteSubgroupDialogFragment = DeleteSubgroupDialogFragment()
        deleteSubgroupDialogFragment.setDeleteSubgroupListener(this)
        deleteSubgroupDialogFragment.show(manager, DIALOG_DELETE_SUBGROUP)
        true
      }
      else                                                   -> super.onOptionsItemSelected(item)
    }
  }

  private fun setSubgroupImage(subgroup: Subgroup) {
    // TODO Проверить, не будет ли лагать, если будет LiveDataSubgroup.
    if (subgroup.isCreatedByUser) {
      Glide.with(this)
        .load(File(requireContext().filesDir, subgroup.imageName))
        .placeholder(R.drawable.shape_load_picture)
        .error(requireContext().getDrawable(R.drawable.user_subgroups_default_color))
        .into(binding.subgroupImage)
    } else {
      Glide.with(this)
        .load(SubgroupImages.HIGH_SUBGROUP_IMAGES_URL + subgroup.imageName)
        .placeholder(R.drawable.shape_load_picture)
        .error(Glide.with(this).load(SubgroupImages.SUBGROUP_IMAGES_URL + subgroup.imageName))
        .into(binding.subgroupImage)
    }
  }

  override fun sort(sortParam: Int) {
    Log.i(LOG_TAG, "sortParam from SortWordsDialogFragment = $sortParam")
    if (this.sortParam != sortParam) {
      this.sortParam = sortParam
      subgroupViewModel?.sortWords(sortParam)
    }
  }

  // TODO убрать взаимодейтсвие с Preferences во ViewModel.
  private fun getSortParam(): Int {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
      context
    )
    return sharedPreferences.getString(
      getString(R.string.preference_key___sort_words_in_subgroups),
      java.lang.String.valueOf(SortWordsDialogFragment.BY_ALPHABET)
    )?.toInt() ?: SortWordsDialogFragment.BY_ALPHABET
  }

  private fun saveSortParam(sortParam: Int) {
    val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    editor.putString(
      getString(R.string.preference_key___sort_words_in_subgroups),
      sortParam.toString()
    )
    editor.apply()
  }

  private val bundleArguments: Unit
    get() {
      if (arguments != null) {
        // TODO разобраться с этим объектом. Можно будет просто id передавать, если
        //  через LiveData будем следить, т.к. он сейчас используется для хранения до передачи в ViewModel.
        val subgroup = SubgroupFragmentArgs.fromBundle(requireArguments()).subgroup
        subgroupIsCreatedByUser = subgroup.isCreatedByUser
        subgroupId = subgroup.id
        subgroupIsStudied = subgroup.studied == 1
      } else {
        onChildFragmentInteractionListener?.close()
      }
    }

  private fun initCreateWordFAB() {
    if (subgroupIsCreatedByUser) {
      binding.createWordButton.setOnClickListener {
        val navDirections: NavDirections =
          SubgroupFragmentDirections.actionSubgroupDestToAddWordDest(subgroupId)
        onChildFragmentInteractionListener?.onChildFragmentInteraction(navDirections)
      }
    } else {
      binding.createWordButton.apply {
        isClickable = false
        visibility = View.GONE
      }
    }
  }


  private fun initRecyclerView() {
    binding.wordsRecyclerView.apply {
      layoutManager = LinearLayoutManager(context)

      addItemDecoration(
        DividerItemDecoration(
          context,
          DividerItemDecoration.VERTICAL
        )
      )

      if (subgroupIsCreatedByUser) {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
          override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
              binding.createWordButton.hide()
            } else if (dy < 0) {
              binding.createWordButton.show()
            }
          }
        })
      }
    }
  }

  private fun initRecyclerViewAdapter() {
    adapter.setOnEntryClickListener(object : WordsRecyclerViewAdapter.OnEntryClickListener {
      override fun onEntryClick(view: View?, position: Int) {
        val currentWord = adapter.getWords()[position]
        subgroupFragmentRouter?.openWord(currentWord)
      }
    })
  }

  private fun initSwipeIcons() {
    linkIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_link_white_24dp)
    deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_white_24dp)
  }

  private fun createMySimpleCallbackBySubgroup(subgroup: Subgroup): MySimpleCallback {
    return if (subgroup.isCreatedByUser) {
      MySimpleCallback(
        0, ItemTouchHelper.LEFT
          or ItemTouchHelper.RIGHT
      )
    } else {
      MySimpleCallback(0, ItemTouchHelper.RIGHT)
    }
  }

  inner class MySimpleCallback(dragDirs: Int, swipeDirs: Int) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder
    ): Boolean {
      return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
      val wordId = adapter.getWordAt(viewHolder.adapterPosition).id
      when (direction) {
        ItemTouchHelper.LEFT  -> {
          subgroupViewModel?.deleteWordFromSubgroup(wordId)
          Snackbar.make(viewHolder.itemView, R.string.word_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.to_cancel) { subgroupViewModel?.addWordToSubgroup(wordId) }
            .show()
        }
        ItemTouchHelper.RIGHT -> {
          showDialogForLinkWord(wordId)
          adapter.notifyDataSetChanged()
        }
      }
    }

    override fun onChildDraw(
      c: Canvas, recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      dX: Float, dY: Float, actionState: Int,
      isCurrentlyActive: Boolean
    ) {
      val itemView = viewHolder.itemView
      val swipeBackground = ColorDrawable()
      val deleteIconMargin = (itemView.height - (deleteIcon?.intrinsicHeight ?: 0)) / 2
      val linkIconMargin = (itemView.height - (linkIcon?.intrinsicHeight ?: 0)) / 2
      if (dX > 0) {
        swipeBackground.color = ContextCompat.getColor(
          requireContext(),
          R.color.swipe_add_link_word
        )
        swipeBackground.color = Color.parseColor("#C6FF00")
        swipeBackground.setBounds(
          itemView.left,
          itemView.top,
          dX.toInt(),
          itemView.bottom
        )
      } else {
        swipeBackground.color = ContextCompat.getColor(
          requireContext(),
          R.color.swipe_delete_link_word
        )
        swipeBackground.setBounds(
          itemView.right + dX.toInt(),
          itemView.top,
          itemView.right,
          itemView.bottom
        )
      }
      swipeBackground.draw(c)
      if (dX > 0) {
        linkIcon?.setBounds(
          itemView.left + linkIconMargin,
          itemView.top + linkIconMargin,
          itemView.left + linkIconMargin + (linkIcon?.intrinsicWidth ?: 0),
          itemView.bottom - linkIconMargin
        )
        linkIcon?.draw(c)
      } else {
        deleteIcon?.setBounds(
          itemView.right - deleteIconMargin - (deleteIcon?.intrinsicWidth ?: 0),
          itemView.top + deleteIconMargin,
          itemView.right - deleteIconMargin,
          itemView.bottom - deleteIconMargin
        )
        deleteIcon?.draw(c)
      }
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
  }

  private fun showDialogForLinkWord(wordId: Long) {
    lifecycleScope.launch {
      val subgroups = subgroupViewModel?.getAvailableSubgroupsToLink(wordId) ?: emptyList()
      Log.d(LOG_TAG, "availableSubgroups onChanged() value != null")
      val linkWordDialogFragment = LinkWordDialogFragment()
      val arguments = Bundle()
      arguments.putLong(
        LinkWordDialogFragment.EXTRA_WORD_ID,
        wordId
      )
      val subgroupsIds = LongArray(subgroups.size)
      val subgroupsNames = arrayOfNulls<String>(subgroups.size)
      for (i in subgroups.indices) {
        val subgroup = subgroups[i]
        subgroupsNames[i] = subgroup.name
        subgroupsIds[i] = subgroup.id
      }
      arguments.putStringArray(
        LinkWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_NAMES,
        subgroupsNames
      )
      arguments.putLongArray(
        LinkWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_IDS,
        subgroupsIds
      )
      linkWordDialogFragment.arguments = arguments
      linkWordDialogFragment.show(
        requireActivity().supportFragmentManager,
        DIALOG_LINK_OR_DELETE_WORD
      )
    }
  }

  override fun resetMessage(message: String?) {
    if (message == ResetWordsProgressDialogFragment.RESET_MESSAGE) {
      subgroupViewModel?.resetWordsProgress()
    }
  }

  override fun deleteMessage(message: String?) {
    if (message == DeleteSubgroupDialogFragment.DELETION_CONFIRMED_MESSAGE) {
      deleteFlag = true
      lifecycleScope.launch {
        if (subgroupViewModel?.deleteSubgroup() != false) {
          onChildFragmentInteractionListener?.close()
        }
      }
    }
  }


  companion object {
    // TODO сделать свою view для отображения прогресса по слову.
    //  Лучше базу брать из той, которая в WordActivity.

    private const val LOG_TAG = "SubgroupFragment"

    private const val DIALOG_SORT_WORDS = "SortWordsDialogFragment"
    private const val DIALOG_RESET_WORDS_PROGRESS = "ResetWordsProgressDialogFragment"
    private const val DIALOG_DELETE_SUBGROUP = "DeleteSubgroupDialogFragment"
    private const val DIALOG_LINK_OR_DELETE_WORD = "LinkOrDeleteWordDialogFragment"
  }
}