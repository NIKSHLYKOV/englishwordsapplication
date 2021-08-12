package ru.nikshlykov.englishwordsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.android.support.DaggerFragment
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.ui.adapters.ExamplesRecyclerViewAdapter
import ru.nikshlykov.englishwordsapp.ui.flowfragments.OnChildFragmentInteractionListener
import ru.nikshlykov.englishwordsapp.ui.fragments.ResetProgressDialogFragment.ResetProgressListener
import ru.nikshlykov.englishwordsapp.ui.viewmodels.WordViewModel
import java.util.*
import javax.inject.Inject

class WordFragment : DaggerFragment(), ResetProgressListener {
  // View элементы.
  private var wordTextInputLayout: TextInputLayout? = null
  private var valueTextInputLayout: TextInputLayout? = null
  private var transcriptionTextInputLayout: TextInputLayout? = null
  private var wordTextInputEditText: TextInputEditText? = null
  private var valueTextInputEditText: TextInputEditText? = null
  private var transcriptionTextInputEditText: TextInputEditText? = null
  private var partOfSpeechTextView: TextView? = null
  private var saveButton: Button? = null
  private var ttsButton: Button? = null
  private var toolbar: Toolbar? = null
  private var progressLinearLayout: LinearLayout? = null
  private var progressTextView: TextView? = null
  private var examplesTextView: TextView? = null
  private var addExampleButton: Button? = null
  private var examplesRecyclerView: RecyclerView? = null
  private val examplesRecyclerViewAdapter: ExamplesRecyclerViewAdapter? = null

  /*// id слова, для которого открылось Activity.
    // Будет равно 0, если открыто для создания нового слова.
    private long wordId = 0L;*/
  // ViewModel для работы с БД.
  private var wordViewModel: WordViewModel? = null

  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null
  private var onChildFragmentInteractionListener: OnChildFragmentInteractionListener? = null

  // Observer отвечающий за обработку подгруженных подгрупп для связывания или удаления.
  lateinit var availableSubgroupsObserver: Observer<ArrayList<Subgroup>?>

  // Флаг, который будет передаваться observer'ом в LinkOrDeleteDialogFragment.
  private var linkOrDeleteFlag = 0

  // Синтезатор речи.
  private var textToSpeech: TextToSpeech? = null
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
    wordViewModel = viewModelFactory!!.create(WordViewModel::class.java)
    textToSpeech = (requireActivity().applicationContext as App).textToSpeech
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val v = inflater.inflate(R.layout.fragment_word, container, false)
    setHasOptionsMenu(true)
    return v
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    findViews(view)
    initToolbar()
    dataAndPrepareInterface
    initSaveButtonClick()
    initAvailableSubgroupsObserver()
  }

  /**
   * Находит View элементы в разметке.
   */
  private fun findViews(v: View) {
    wordTextInputEditText = v.findViewById(R.id.fragment_word___text_input_edit_text___word)
    valueTextInputEditText = v.findViewById(R.id.fragment_word___text_input_edit_text___value)
    transcriptionTextInputEditText =
      v.findViewById(R.id.fragment_word___text_input_edit_text___transcription)
    saveButton = v.findViewById(R.id.fragment_word___button___save_word)
    ttsButton = v.findViewById(R.id.fragment_word___button___tts)
    partOfSpeechTextView = v.findViewById(R.id.fragment_word___text_view___part_of_speech)
    toolbar = v.findViewById(R.id.fragment_word___toolbar)
    progressLinearLayout =
      v.findViewById(R.id.fragment_word___linear_layout___progress_view_background)
    examplesRecyclerView = v.findViewById(R.id.fragment_word___recycler_view___examples)
    addExampleButton = v.findViewById(R.id.fragment_word___button___add_example)
    wordTextInputLayout = v.findViewById(R.id.fragment_word___text_input_layout___word)
    transcriptionTextInputLayout =
      v.findViewById(R.id.fragment_word___text_input_layout___transcription)
    valueTextInputLayout = v.findViewById(R.id.fragment_word___text_input_layout___value)
    progressTextView = v.findViewById(R.id.fragment_word___text_view___progress)
    examplesTextView = v.findViewById(R.id.fragment_word___text_view___examples)
  }

  /**
   * Устанавливает toolbar и его title.
   */
  private fun initToolbar() {
    // Устанавливаем тулбар.
    (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
    (activity as AppCompatActivity?)!!.supportActionBar!!.title = ""
  }// Делаем доступными для редактирования поля с параметрами слова.

  /*wordViewModel.getExamples(WordActivity.this);*/

  // Присваиваем обработчик нажатия на кнопку воспроизведения слова.
/*RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                        WordActivity.this);
                examplesRecyclerView.setLayoutManager(layoutManager);
                examplesRecyclerViewAdapter = new ExamplesRecyclerViewAdapter(WordActivity.this);
                examplesRecyclerView.setAdapter(examplesRecyclerViewAdapter);*/
  /*addExampleButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             examplesRecyclerViewAdapter.addExample();
             examplesRecyclerView.smoothScrollToPosition(examplesRecyclerViewAdapter
                     .getItemCount() - 1);
         }
     });*/// Получаем id слова, которое было выбрано.
  /**
   * Получает id слова из Extras и, в зависимости от него, либо скрывает некоторые элементы,
   * чтобы создать новое слово, либо устанавливает параметры уже существующего слова в наши View.
   */
  private val dataAndPrepareInterface: Unit
    private get() {
      val arguments = arguments
      if (arguments != null) {
        // Получаем id слова, которое было выбрано.
        val startTo = WordFragmentArgs.fromBundle(requireArguments()).startTo
        Log.i(LOG_TAG, "startTo = $startTo")
        when (startTo) {
          START_TO_CREATE_WORD -> prepareInterfaceForNewWordCreating()
          START_TO_EDIT_WORD -> {
            /*RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
WordActivity.this);
examplesRecyclerView.setLayoutManager(layoutManager);
examplesRecyclerViewAdapter = new ExamplesRecyclerViewAdapter(WordActivity.this);
examplesRecyclerView.setAdapter(examplesRecyclerViewAdapter);*/
            /*addExampleButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       examplesRecyclerViewAdapter.addExample();
                       examplesRecyclerView.smoothScrollToPosition(examplesRecyclerViewAdapter
                               .getItemCount() - 1);
                   }
               });*/
            val word = WordFragmentArgs.fromBundle(requireArguments()).word
            wordViewModel!!.setWord(word)
            wordViewModel!!.wordMutableLiveData.observe(viewLifecycleOwner, Observer { word ->
              Log.d(LOG_TAG, "word onChanged()")
              if (word != null) {
                setWordToViews(word)

                // Делаем доступными для редактирования поля с параметрами слова.
                if (word.createdByUser == 1) {
                  saveButton!!.visibility = View.VISIBLE
                  wordTextInputLayout!!.isEnabled = true
                  transcriptionTextInputLayout!!.isEnabled = true
                  valueTextInputLayout!!.isEnabled = true
                }

                /*wordViewModel.getExamples(WordActivity.this);*/
              }
            })

            // Присваиваем обработчик нажатия на кнопку воспроизведения слова.
            ttsButton!!.setOnClickListener {
              textToSpeech!!.speak(
                wordTextInputEditText!!.text.toString(),
                TextToSpeech.QUEUE_ADD, null, "1"
              )
            }
          }
          else                 -> errorProcessing()
        }
      } else {
        errorProcessing()
      }
    }

  /**
   * Выводит сообщение об ошибке и закрывает fragment.
   */
  private fun errorProcessing() {
    Log.e(LOG_TAG, "Error happened!")
    // TODO Сделать потом, наверное, через нажатие кнопки назад программно.
    val navDirections: NavDirections = WordFragmentDirections
      .actionWordDestToSubgroupDest(Subgroup("Testing subgroup"))
    onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
  }

  /**
   * Присваивает обработчик нажатия на кнопку сохранения слова.
   */
  private fun initSaveButtonClick() {
    // Присваиваем обработчик нажатия на кнопку сохранения слова.
    saveButton!!.setOnClickListener {
      // Получаем строки из EditText'ов.
      val word = wordTextInputEditText!!.text.toString()
      val value = valueTextInputEditText!!.text.toString()
      val transcription = transcriptionTextInputEditText!!.text.toString()

      // Проверяем, что поля слова и перевода не пустые
      if (!word.isEmpty() && !value.isEmpty()) {
        /*if (wordId != 0) {
                          wordViewModel.update(wordId, word, transcription, value);
                      } else {*/

        // Считываем данные из EditText'ов и отправляем их обратно в SubgroupActivity.
        /*Intent wordData = new Intent();
                      wordData.putExtra(EXTRA_WORD_ID, wordId);
                      wordData.putExtra(EXTRA_WORD, word);
                      wordData.putExtra(EXTRA_TRANSCRIPTION, transcription);
                      wordData.putExtra(EXTRA_VALUE, value);*/

        //wordData.putExtra(EXTRA_WORD_OBJECT, )
        //setResult(RESULT_OK, wordData);

        // TODO сделать обработку добавления нового слова.
        //  Скорее всего, надо разнести код на отдельные фрагменты для слова и для его добавления.
        wordViewModel!!.setWordParameters(word, transcription, value)
        wordViewModel!!.updateWordInDB()
        //
        /*}*/

        // Закрываем fragment.
        val navDirections: NavDirections = WordFragmentDirections
          .actionWordDestToSubgroupDest(Subgroup("Testing subgroup"))
        onChildFragmentInteractionListener!!.onChildFragmentInteraction(navDirections)
      } else {
        Toast.makeText(
          context,
          R.string.error_word_saving, Toast.LENGTH_LONG
        ).show()
      }
    }
  }
  // TODO Что делать с сохранением прогресса, если пользователь не нажал сохранить
  // Наверное, просто апдейтить его в самом диалоге сбрасывания прогресса.
  /**
   * Инициализирует availableSubgroupsObserver.
   */
  private fun initAvailableSubgroupsObserver() {
    availableSubgroupsObserver = Observer { subgroups ->
      Log.d(LOG_TAG, "availableSubgroups onChanged()")
      if (subgroups != null) {
        Log.d(LOG_TAG, "availableSubgroups onChanged() value != null")
        val linkOrDeleteWordDialogFragment = LinkOrDeleteWordDialogFragment()
        val arguments = Bundle()
        val wordId = wordViewModel!!.wordId
        if (wordId != 0L) {
          arguments.putLong(
            LinkOrDeleteWordDialogFragment.EXTRA_WORD_ID,
            wordId
          )
          arguments.putInt(
            LinkOrDeleteWordDialogFragment.EXTRA_FLAG,
            linkOrDeleteFlag
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
          linkOrDeleteWordDialogFragment.show(requireActivity().supportFragmentManager, "some tag")
          wordViewModel!!.clearAvailableSubgroupsToAndRemoveObserver(availableSubgroupsObserver)
        }
      } else {
        Log.d(LOG_TAG, "availableSubgroups onChanged() value = null")
      }
    }
  }

  /**
   * Устанавливаем параметры слова (слово, транскрипция, перевод, часть речи, прогресс в разные View.
   */
  private fun setWordToViews(word: Word) {
    // Устанавливаем параметры слова в EditText'ы.
    wordTextInputEditText!!.setText(word.word)
    valueTextInputEditText!!.setText(word.value)
    transcriptionTextInputEditText!!.setText(word.transcription)

    // Устанавливаем часть речи, если она указана.
    if (word.partOfSpeech != null) {
      partOfSpeechTextView!!.text = word.partOfSpeech
    } else {
      partOfSpeechTextView!!.visibility = View.GONE
    }

    // Устанавливаем прогресс.
    val learnProgressView = View(context)
    val progressViewIndex = 0
    when (word.learnProgress) {
      -1 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(0), dpToPx(10))
      }
      0 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_0)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(10))
      }
      1 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_1)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(50), dpToPx(10))
      }
      2 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_2)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(75), dpToPx(10))
      }
      3 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_3)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(100), dpToPx(10))
      }
      4 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_4)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(125), dpToPx(10))
      }
      5 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_5)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(150), dpToPx(10))
      }
      6 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_6)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(175), dpToPx(10))
      }
      7, 8 -> {
        learnProgressView.setBackgroundResource(R.drawable.shape_progress_7)
        learnProgressView.layoutParams = LinearLayout.LayoutParams(dpToPx(200), dpToPx(10))
      }
    }
    if (progressLinearLayout!!.getChildAt(progressViewIndex) != null) {
      progressLinearLayout!!.removeViewAt(progressViewIndex)
    }
    progressLinearLayout!!.addView(learnProgressView, progressViewIndex)
  }

  /**
   * Скрывает некоторые View при создании нового слова.
   */
  private fun prepareInterfaceForNewWordCreating() {
    progressTextView!!.visibility = View.GONE
    ttsButton!!.visibility = View.GONE
    partOfSpeechTextView!!.visibility = View.GONE
    toolbar!!.visibility = View.GONE
    progressLinearLayout!!.visibility = View.GONE
    examplesTextView!!.visibility = View.GONE
    examplesRecyclerView!!.visibility = View.GONE
    addExampleButton!!.visibility = View.GONE
    wordTextInputLayout!!.isEnabled = true
    transcriptionTextInputLayout!!.isEnabled = true
    valueTextInputLayout!!.isEnabled = true
    saveButton!!.visibility = View.VISIBLE
  }

  /**
   * Создаёт меню для тулбара.
   */
  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.fragment_word_toolbar_menu, menu)
  }

  /**
   * Обрабатывает нажатия на пункты тулбара.
   */
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val manager = requireActivity().supportFragmentManager

    // Bundle для передачи id слова в диалоговый фрагмент, который вызовется.
    val arguments = Bundle()
    return when (item.itemId) {
      R.id.fragment_word___action___link_word -> {
        Log.d(LOG_TAG, "Link word")
        linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_LINK
        // Подписываемся на изменение доступных для связывания подгрупп.
        wordViewModel!!.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(
          this,
          availableSubgroupsObserver!!
        )
        true
      }
      R.id.fragment_word___action___reset_word_progress -> {
        Log.d(LOG_TAG, "Reset word progress")
        val resetProgressDialogFragment = ResetProgressDialogFragment()
        resetProgressDialogFragment.setResetProgressListener(this)
        arguments.putInt(
          ResetProgressDialogFragment.EXTRA_FLAG,
          ResetProgressDialogFragment.FOR_ONE_WORD
        )
        resetProgressDialogFragment.arguments = arguments
        resetProgressDialogFragment.show(manager, DIALOG_RESET_WORD_PROGRESS)
        true
      }
      R.id.fragment_word___action___delete_word -> {
        Log.d(LOG_TAG, "Delete word")
        linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_DELETE
        // Подписываемся на изменение доступных для связывания подгрупп.
        wordViewModel!!.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(
          this,
          availableSubgroupsObserver!!
        )
        true
      }
      else                                              -> super.onOptionsItemSelected(item)
    }
  }

  /**
   * Принимает сообщение от ResetWordProgressDialogFragment.
   *
   * @param message представляет из себя сообщение.
   */
  override fun resetMessage(message: String?) {
    if (message == ResetProgressDialogFragment.RESET_MESSAGE) {
      wordViewModel!!.resetProgress()
    }
  }

  // ПОСМОТРЕТЬ, КАК МОЖНО ОТ ЭТОГО ИЗБАВИТЬСЯ
  fun dpToPx(dp: Int): Int {
    val density = this.resources.displayMetrics.density
    return Math.round(dp.toFloat() * density)
  }

  companion object {
    // Тег для логирования.
    private const val LOG_TAG = "WordFragment"

    // Extras для получения данных из интента.
    const val EXTRA_WORD_ID = "WordId"
    const val EXTRA_WORD = "word"
    const val EXTRA_TRANSCRIPTION = "Transcription"
    const val EXTRA_VALUE = "Value"

    // Возможные цели старта Fragment.
    const val START_TO_CREATE_WORD = 0
    const val START_TO_EDIT_WORD = 1

    // Теги для диалоговых фрагментов.
    private const val DIALOG_RESET_WORD_PROGRESS = "ResetWordProgressDialogFragment"
    private const val DIALOG_LINK_WORD = "LinkWordDialogFragment"
    private const val DIALOG_DELETE_WORD = "DeleteWordDialogFragment"
  }
}