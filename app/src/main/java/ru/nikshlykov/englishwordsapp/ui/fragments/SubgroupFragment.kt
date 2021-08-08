package ru.nikshlykov.englishwordsapp.ui.fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.GroupsRepository
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.ui.adapters.WordsRecyclerViewAdapter
import ru.nikshlykov.englishwordsapp.ui.adapters.WordsRecyclerViewAdapter.OnEntryClickListener
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener
import ru.nikshlykov.englishwordsapp.ui.fragments.DeleteSubgroupDialogFragment.DeleteSubgroupListener
import ru.nikshlykov.englishwordsapp.ui.fragments.ResetProgressDialogFragment.ResetProgressListener
import ru.nikshlykov.englishwordsapp.ui.fragments.SortWordsDialogFragment.SortWordsListener
import ru.nikshlykov.englishwordsapp.ui.viewmodels.SubgroupViewModel
import java.util.*
import javax.inject.Inject

class SubgroupFragment : DaggerFragment(), SortWordsListener, ResetProgressListener,
  DeleteSubgroupListener {
  // View элементы.
  private var createWordFAB: FloatingActionButton? = null
  private var toolbar: Toolbar? = null
  private var infoTextView: TextView? = null
  private var subgroupImageView: ImageView? = null
  private var recyclerView: RecyclerView? = null
  private var adapter: WordsRecyclerViewAdapter? = null

  // Иконки для свайпа слова в recyclerView.
  private var deleteIcon: Drawable? = null
  private var linkIcon: Drawable? = null
  private var subgroupViewModel: SubgroupViewModel? = null

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null
  private var onChildFragmentInteractionListener: OnChildFragmentInteractionListener? = null
  private var subgroupId: Long = 0
  private var subgroupIsStudied = false
  private var subgroupIsCreatedByUser = false
  private var deleteFlag = false
  private lateinit var availableSubgroupsObserver: Observer<ArrayList<Subgroup>?>

  // параметр сортировки слов в подгруппе.
  private var sortParam = 0
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
    subgroupViewModel = viewModelFactory!!.create(SubgroupViewModel::class.java)

    // Получаем id подгруппы из Intent.
    bundleArguments
    sortParam = getSortParam()
    subgroupViewModel!!.setLiveDataSubgroup(subgroupId, sortParam)
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Находим наши View.
    findViews(view)

    // Устанавливаем наш toolbar.
    (activity as DaggerAppCompatActivity?)!!.setSupportActionBar(toolbar)
    initCreateWordFAB()
    subgroupViewModel!!.subgroupLiveData.observe(viewLifecycleOwner, Observer { subgroup ->
      if (subgroup != null) {
        Log.i(LOG_TAG, "subgroup onChanged()")
        val toolbarLayout: CollapsingToolbarLayout = view.findViewById(
          R.id.fragment_subgroup___collapsing_toolbar_layout
        )
        toolbarLayout.title = subgroup.name
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingToolbarCollapseTitle)
        toolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsingToolbarExpandedTitle)
        setSubgroupImage(subgroup.isCreatedByUser, subgroup.imageURL)

        // Создаём вещи для свайпа слов.
        initSwipeIcons()
        ItemTouchHelper(createMySimpleCallbackBySubgroup(subgroup))
          .attachToRecyclerView(recyclerView)
      }
    })

    // Создаём Recycler и адаптер для него.
    initRecyclerView()
    initRecyclerViewAdapter()
    recyclerView!!.adapter = adapter

    // Закидываем данные в адаптер, подписывая его на изменения в LiveData.
    subgroupViewModel!!.words.observe(viewLifecycleOwner, Observer { words ->
      Log.i(LOG_TAG, "words onChanged()")
      if (words != null) {
        if (!deleteFlag) {
          // Если слов нет, то скрываем CheckBox изучения подгруппы.
          if (words.isEmpty()) {
            if (!subgroupIsCreatedByUser) {
              infoTextView!!.text = "Здесь скоро появятся слова. Пожалуйста, подождите обновления!"
            } else {
              //learnSubgroupCheckBox.setVisibility(View.GONE);
              /*Toast.makeText(SubgroupActivity.this,
                                          R.string.error_subgroup_is_empty, Toast.LENGTH_LONG)
                                          .show();*/
            }
          } else {
            //learnSubgroupCheckBox.setVisibility(View.VISIBLE);
          }
          adapter!!.setWords(words)
        }
      }
    })
  }

  override fun onPause() {
    super.onPause()
    Log.i(LOG_TAG, "onPause()")
    if (!deleteFlag) {
      // TODO проверить Lifecycle в GroupsFragment.
      //  Если заходить в неизучаемую группу и делать её изучаемой, то не показывается
      //  обновлённый значок мозга, если сразу назад переходить
      //  Если заходить в изучаемую группу и делать её неизучаемой, то при переходе назад
      //  она вообще почему-то не обновляется.
      subgroupViewModel!!.updateSubgroup()
      saveSortParam(sortParam)
    }
  }

  // Toolbar.
  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)

    // TODO Не работает (не появляются пункты меню в тулбаре), разобраться.
    Log.d(LOG_TAG, "onCreateOptionsMenu()")
    inflater.inflate(R.menu.activity_subgroup_toolbar_menu, menu)
    if (subgroupIsStudied) {
      menu.findItem(R.id.activity_subgroup___action___learn)
        .setChecked(true).icon = requireContext().getDrawable(R.drawable.ic_brain_selected_yellow)
    }
    // Скрываем действия, доступные только для созданных пользователем подгрупп.
    if (!subgroupIsCreatedByUser) {
      menu.findItem(R.id.activity_subgroup___action___delete_subgroup).isVisible = false
      menu.findItem(R.id.activity_subgroup___action___edit_subgroup).isVisible = false
    }

    /*ToggleButton learnToggleButton = (ToggleButton) menu.findItem(R.id.activity_subgroup___action___learn)
                .getActionView();
        learnToggleButton.setLayoutParams(new Toolbar.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_width),
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_height)));
        learnToggleButton.setText("");
        learnToggleButton.setTextOn("");
        learnToggleButton.setTextOff("");
        learnToggleButton.setBackgroundDrawable(getDrawable(R.drawable.background_brain_icon));*/
  }

  /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.activity_subgroup_toolbar_menu, menu);

        if (subgroupIsStudied) {
            menu.findItem(R.id.activity_subgroup___action___learn)
                    .setChecked(true)
                    .setIcon(getDrawable(R.drawable.ic_brain_selected_yellow));
        }
        // Скрываем действия, доступные только для созданных пользователем подгрупп.
        if (!subgroupIsCreatedByUser) {
            menu.findItem(R.id.activity_subgroup___action___delete_subgroup).setVisible(false);
            menu.findItem(R.id.activity_subgroup___action___edit_subgroup).setVisible(false);
        }

        */
  /*ToggleButton learnToggleButton = (ToggleButton) menu.findItem(R.id.activity_subgroup___action___learn)
                .getActionView();
        learnToggleButton.setLayoutParams(new Toolbar.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_width),
                getResources().getDimensionPixelSize(R.dimen.toggle_button_brain_height)));
        learnToggleButton.setText("");
        learnToggleButton.setTextOn("");
        learnToggleButton.setTextOff("");
        learnToggleButton.setBackgroundDrawable(getDrawable(R.drawable.background_brain_icon));*/
  /*

        return true;
    }*/
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val manager = requireActivity().supportFragmentManager
    return when (item.itemId) {
      R.id.activity_subgroup___action___learn -> {
        if (item.isChecked) {
          item.isChecked = false
          item.icon = requireContext().getDrawable(R.drawable.ic_brain_not_selected)
          subgroupViewModel!!.setNewIsStudied(false)
        } else {
          item.isChecked = true
          item.icon = requireContext().getDrawable(R.drawable.ic_brain_selected_yellow)
          subgroupViewModel!!.setNewIsStudied(true)
        }
        true
      }
      R.id.activity_subgroup___action___sort -> {
        Log.d(LOG_TAG, "sort words")
        val sortWordsDialogFragment = SortWordsDialogFragment()
        val sortWordsDialogArguments = Bundle()
        sortWordsDialogArguments.putInt(SortWordsDialogFragment.EXTRA_SORT_PARAM, sortParam)
        sortWordsDialogFragment.arguments = sortWordsDialogArguments
        sortWordsDialogFragment.show(manager, DIALOG_SORT_WORDS)
        true
      }
      R.id.activity_subgroup___action___edit_subgroup -> {
        Log.d(LOG_TAG, "edit subgroup")
        val navDirections: NavDirections = SubgroupFragmentDirections
          .actionGlobalSubgroupDataDest()
          .setSubgroupId(subgroupId)
        onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
        true
      }
      R.id.activity_subgroup___action___reset_words_progress -> {
        Log.d(LOG_TAG, "Reset words progress")
        val resetProgressDialogFragment = ResetProgressDialogFragment()
        val resetProgressDialogArguments = Bundle()
        resetProgressDialogArguments.putInt(
          ResetProgressDialogFragment.EXTRA_FLAG,
          ResetProgressDialogFragment.FOR_SUBGROUP
        )
        resetProgressDialogFragment.arguments = resetProgressDialogArguments
        resetProgressDialogFragment.show(
          manager,
          DIALOG_RESET_WORDS_PROGRESS
        )
        true
      }
      R.id.activity_subgroup___action___delete_subgroup -> {
        Log.d(LOG_TAG, "Delete subgroup")
        val deleteSubgroupDialogFragment = DeleteSubgroupDialogFragment()
        deleteSubgroupDialogFragment.show(manager, DIALOG_DELETE_SUBGROUP)
        true
      }
      else                                                   -> super.onOptionsItemSelected(item)
    }
  }

  private fun setSubgroupImage(subgroupIsCreatedByUser: Boolean, imageResourceId: String) {
    // TODO Проверить, не будет ли лагать, если будет LiveDataSubgroup.
    if (subgroupIsCreatedByUser) {
      val imageColor = requireContext().getDrawable(R.drawable.user_subgroups_default_color)
      subgroupImageView!!.setImageDrawable(imageColor)
    } else {
      Glide.with(this)
        .load(GroupsRepository.PATH_TO_HIGH_SUBGROUP_IMAGES + imageResourceId)
        .placeholder(R.drawable.shape_load_picture)
        .error(Glide.with(this).load(GroupsRepository.PATH_TO_SUBGROUP_IMAGES + imageResourceId))
        .into(subgroupImageView!!)
    }
  }
  // Параметр сортировки слова.
  /**
   * Обрабатывает результат работы SortWordsDialogFragment.
   *
   * @param sortParam параметр сортировки, полученный из фрагмента.
   */
  override fun sort(sortParam: Int) {
    Log.i(LOG_TAG, "sortParam from SortWordsDialogFragment = $sortParam")
    // Проверяем, изменился ли вообще параметр, чтобы не делать лишней работы.
    if (this.sortParam != sortParam) {
      // Устанавливаем новый параметр сортировки.
      this.sortParam = sortParam
      // Сообщаем модели о том, что необходимо отсортировать слова.
      subgroupViewModel!!.sortWords(sortParam)
    }
  }

  /**
   * Достаёт параметр сортировки слов в подгруппе.
   *
   * @return параметр сортировки.
   */
  private fun getSortParam(): Int {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
      context
    )
    return sharedPreferences.getString(
      getString(R.string.preference_key___sort_words_in_subgroups),
      java.lang.String.valueOf(SortWordsDialogFragment.BY_ALPHABET)
    )!!.toInt()
  }

  /**
   * Сохраняет параметр сортировки слов в подгруппе.
   *
   * @param sortParam последний выставленный параметр сортировки.
   */
  private fun saveSortParam(sortParam: Int) {
    val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    editor.putString(
      getString(R.string.preference_key___sort_words_in_subgroups),
      sortParam.toString()
    )
    editor.apply()
  }
  // Создание Activity и View.// TODO разобраться с этим объектом. Можно будет просто id передавать, если
  //  через LiveData будем следить, т.к. он сейчас используется для хранения до передачи в ViewModel.
  /**
   * Получает из Extras id подгруппы и флаг того, создана ли она пользователем.
   */
  private val bundleArguments: Unit
    private get() {
      if (arguments != null) {
        // TODO разобраться с этим объектом. Можно будет просто id передавать, если
        //  через LiveData будем следить, т.к. он сейчас используется для хранения до передачи в ViewModel.
        val subgroup = SubgroupFragmentArgs.fromBundle(requireArguments()).subgroup
        subgroupIsCreatedByUser = subgroup.isCreatedByUser
        subgroupId = subgroup.id
        subgroupIsStudied = subgroup.isStudied == 1
      } else {
        val navDirections = SubgroupFragmentDirections.actionSubgroupDestToGroupsDest()
        onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
      }
    }

  /**
   * Находит View в разметке.
   */
  private fun findViews(view: View) {
    createWordFAB = view.findViewById(R.id.fragment_subgroup___floating_action_button___new_word)
    toolbar = view.findViewById(R.id.fragment_subgroup___toolbar)
    recyclerView = view.findViewById(R.id.fragment_subgroup___recycler_view___words)
    infoTextView = view.findViewById(R.id.fragment_subgroup___text_view___info)
    subgroupImageView = view.findViewById(R.id.fragment_subgroup___image_view___subgroup_image)
  }

  private fun initCreateWordFAB() {
    // Проверяем, что подгруппа создана пользователем.
    if (subgroupIsCreatedByUser) {
      // Присваиваем обработчик кнопке для создания нового слова.
      createWordFAB!!.setOnClickListener { /*Intent createNewWordIntent = new Intent(getContext(),
                            WordActivity.class);
                    createNewWordIntent.putExtra(EXTRA_SUBGROUP_ID, subgroupId);
                    createNewWordIntent.putExtra(WordActivity.EXTRA_START_TO, WordActivity.START_TO_CREATE_WORD);
                    startActivityForResult(createNewWordIntent, REQUEST_CODE_CREATE_NEW_WORD);*/
        // TODO Скорее всего, будем разделять WordFragment на два: один фрагмент
        //  добавляет новое слово, другой - редактирует существующее слово. Тогда
        //  и уберём этот код.
        val navDirections: NavDirections = SubgroupFragmentDirections
          .actionSubgroupDestToWordDest(Word("a", "a", "a"))
          .setStartTo(WordFragment.START_TO_CREATE_WORD)
        onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
      }
    } else {
      // Скрываем fab для создания нового слова.
      createWordFAB!!.isClickable = false
      createWordFAB!!.visibility = View.GONE
    }
  }

  // RecyclerView.
  private fun initRecyclerView() {
    // Создаём вспомогательные вещи для RecyclerView и соединяем их с ним.
    recyclerView!!.layoutManager = LinearLayoutManager(context)
    recyclerView!!.addItemDecoration(
      DividerItemDecoration(
        context,
        DividerItemDecoration.VERTICAL
      )
    )
    if (subgroupIsCreatedByUser) {
      recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          if (dy > 0) {
            createWordFAB!!.hide()
          } else if (dy < 0) {
            createWordFAB!!.show()
          }
        }
      })
    }
  }

  private fun initRecyclerViewAdapter() {
    // Создаём adapter для RecyclerView.
    adapter = WordsRecyclerViewAdapter(requireContext())

    // Присваиваем обработчик нажатия на элемент в RecyclerView (слово).
    adapter!!.setOnEntryClickListener(object : OnEntryClickListener {
      override fun onEntryClick(view: View?, position: Int) {
        val currentWord = adapter!!.getWords()[position]
        /*Intent editExistingWordIntent = new Intent(getContext(),
                        WordActivity.class);
                editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_OBJECT, currentWord);
                editExistingWordIntent.putExtra(WordActivity.EXTRA_START_TO, WordActivity.START_TO_EDIT_WORD);
                //editExistingWordIntent.putExtra(WordActivity.EXTRA_WORD_ID, currentWord.id);
                startActivityForResult(editExistingWordIntent, REQUEST_CODE_EDIT_EXISTING_WORD);*/
        val navDirections: NavDirections = SubgroupFragmentDirections
          .actionSubgroupDestToWordDest(currentWord)
          .setStartTo(WordFragment.START_TO_EDIT_WORD)
        onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
      }
    })
  }

  private fun initSwipeIcons() {
    // Находим иконки, изображаемые при свайпе.
    linkIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_link_white_24dp)
    deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_white_24dp)
  }

  fun createMySimpleCallbackBySubgroup(subgroup: Subgroup): MySimpleCallback {
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
      val wordId = adapter!!.getWordAt(viewHolder.adapterPosition).id
      when (direction) {
        ItemTouchHelper.LEFT -> {
          subgroupViewModel!!.deleteLinkWithSubgroup(wordId)
          Snackbar.make(viewHolder.itemView, R.string.word_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.to_cancel) { subgroupViewModel!!.insertLinkWithSubgroup(wordId) }
            .show()
        }
        ItemTouchHelper.RIGHT -> {
          setAvailableSubgroupsObserver(wordId)
          subgroupViewModel!!.getAvailableSubgroupsToLink(wordId).observe(
            viewLifecycleOwner, availableSubgroupsObserver
          )
          adapter!!.notifyDataSetChanged()
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
      val deleteIconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
      val linkIconMargin = (itemView.height - linkIcon!!.intrinsicHeight) / 2
      if (dX > 0) {
        swipeBackground.color = ContextCompat.getColor(
          context!!,
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
          context!!,
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
        linkIcon!!.setBounds(
          itemView.left + linkIconMargin,
          itemView.top + linkIconMargin,
          itemView.left + linkIconMargin + linkIcon!!.intrinsicWidth,
          itemView.bottom - linkIconMargin
        )
        linkIcon!!.draw(c)
      } else {
        deleteIcon!!.setBounds(
          itemView.right - deleteIconMargin - deleteIcon!!.intrinsicWidth,
          itemView.top + deleteIconMargin,
          itemView.right - deleteIconMargin,
          itemView.bottom - deleteIconMargin
        )
        deleteIcon!!.draw(c)
      }
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
  }

  private fun setAvailableSubgroupsObserver(wordId: Long) {
    availableSubgroupsObserver = Observer { subgroups ->
      Log.d(LOG_TAG, "availableSubgroups onChanged()")
      if (subgroups != null) {
        Log.d(LOG_TAG, "availableSubgroups onChanged() value != null")
        val linkOrDeleteWordDialogFragment = LinkOrDeleteWordDialogFragment()
        val arguments = Bundle()
        arguments.putLong(
          LinkOrDeleteWordDialogFragment.EXTRA_WORD_ID,
          wordId
        )
        arguments.putInt(
          LinkOrDeleteWordDialogFragment.EXTRA_FLAG,
          LinkOrDeleteWordDialogFragment.TO_LINK
        )
        val subgroupsIds = LongArray(subgroups.size)
        val subgroupsNames = arrayOfNulls<String>(subgroups.size)
        for (i in subgroups.indices) {
          val subgroup = subgroups[i]
          subgroupsNames[i] = subgroup.name
          subgroupsIds[i] = subgroup.id
        }
        arguments.putStringArray(
          LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_NAMES,
          subgroupsNames
        )
        arguments.putLongArray(
          LinkOrDeleteWordDialogFragment.EXTRA_AVAILABLE_SUBGROUPS_IDS,
          subgroupsIds
        )
        linkOrDeleteWordDialogFragment.arguments = arguments
        linkOrDeleteWordDialogFragment.show(
          requireActivity().supportFragmentManager,
          DIALOG_LINK_OR_DELETE_WORD
        )
        subgroupViewModel!!.clearAvailableSubgroupsToAndRemoveObserver(availableSubgroupsObserver)
      } else {
        Log.d(LOG_TAG, "availableSubgroups onChanged() value = null")
      }
    }
  }
  // Диалоги.
  /**
   * Принимает сообщение от ResetWordProgressDialogFragment.
   *
   * @param message представляет из себя сообщение.
   */
  override fun resetMessage(message: String?) {
    if (message == ResetProgressDialogFragment.RESET_MESSAGE) {
      subgroupViewModel!!.resetWordsProgress()
    }
  }

  /**
   * Принимает сообщение от DeleteSubgroupDialogFragment.
   *
   * @param message удалять подгруппу или нет.
   */
  override fun deleteMessage(message: String?) {
    if (message == DeleteSubgroupDialogFragment.DELETE_MESSAGE) {
      val currentSubgroup = subgroupViewModel!!.subgroupLiveData.value
      if (currentSubgroup != null) {
        if (currentSubgroup.isCreatedByUser) {
          subgroupViewModel!!.deleteSubgroup()
          deleteFlag = true
          val navDirections = SubgroupFragmentDirections.actionSubgroupDestToGroupsDest()
          onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
        }
      }
    }
  }

  companion object {
    // TODO сделать свою view для отображения прогресса по слову.
    //  Лучше базу брать из той, которая в WordActivity.
    // TODO надо будет отсюда убрать onActivityResult (уже убрал). При этом надо учесть, что
    //  группа (название) должно обновиться. По этому надо подтягивать группу из БД
    //  через LiveData. Могут быть проблемы при удалении подгруппы, проверить этот момент.
    // TODO починить диалоги. Скорее всего, проблема в том, что диалоговые фрагменты ждут слушателя,
    //  но они крепятся к Activity, которая не реализует интерфейса.
    // Ключи для получения аргументов.
    const val EXTRA_SUBGROUP_OBJECT = "SubgroupObject"
    const val EXTRA_SUBGROUP_ID = "SubgroupId"
    const val EXTRA_SUBGROUP_IS_CREATED_BY_USER = "SubgroupIsCreatedByUser"
    const val EXTRA_SUBGROUP_IS_STUDIED = "SubgroupIsStudied"

    // TODO Проверить, что при возвращении на GroupsFragment обновляются подгруппы. Особенно
    //  при удалении текущей.
    /*
    // Ключи для передачи аргументов.
    public static final String EXTRA_DELETE_SUBGROUP = "DeleteSubgroup";*/
    // Коды запросов для startActivityForResult().
    private const val REQUEST_CODE_EDIT_EXISTING_WORD = 1
    private const val REQUEST_CODE_CREATE_NEW_WORD = 2
    private const val REQUEST_CODE_EDIT_SUBGROUP = 3

    // Тег для логирования.
    private const val LOG_TAG = "SubgroupFragment"

    // Теги для диалоговых фрагментов.
    private const val DIALOG_SORT_WORDS = "SortWordsDialogFragment"
    private const val DIALOG_RESET_WORDS_PROGRESS = "ResetWordsProgressDialogFragment"
    private const val DIALOG_DELETE_SUBGROUP = "DeleteSubgroupDialogFragment"
    private const val DIALOG_LINK_OR_DELETE_WORD = "LinkOrDeleteWordDialogFragment"
  }
}