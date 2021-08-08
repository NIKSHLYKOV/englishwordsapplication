package ru.nikshlykov.englishwordsapp.ui.activities

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import dagger.android.support.DaggerAppCompatActivity
import ru.nikshlykov.englishwordsapp.App
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.WordsRepository.OnExamplesLoadedListener
import ru.nikshlykov.englishwordsapp.db.example.Example
import ru.nikshlykov.englishwordsapp.db.subgroup.Subgroup
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.ui.adapters.ExamplesRecyclerViewAdapter
import ru.nikshlykov.englishwordsapp.ui.fragments.LinkOrDeleteWordDialogFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.ResetProgressDialogFragment
import ru.nikshlykov.englishwordsapp.ui.fragments.ResetProgressDialogFragment.ResetProgressListener
import ru.nikshlykov.englishwordsapp.ui.viewmodels.WordViewModel
import java.util.*
import javax.inject.Inject

class WordActivity : DaggerAppCompatActivity(), ResetProgressListener, OnExamplesLoadedListener {
  // View элементы.
  private var wordTextInputEditText: TextInputEditText? = null
  private var valueTextInputEditText: TextInputEditText? = null
  private var transcriptionTextInputEditText: TextInputEditText? = null
  private var partOfSpeechTextView: TextView? = null
  private var saveButton: Button? = null
  private var ttsButton: Button? = null
  private var toolbar: Toolbar? = null
  private var progressLinearLayout: LinearLayout? = null
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

  // Observer отвечающий за обработку подгруженных подгрупп для связывания или удаления.
  lateinit var availableSubgroupsObserver: Observer<ArrayList<Subgroup>?>

  // Флаг, который будет передаваться observer'ом в LinkOrDeleteDialogFragment.
  private var linkOrDeleteFlag = 0

  // Синтезатор речи.
  private var textToSpeech: TextToSpeech? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    wordViewModel = viewModelFactory!!.create(WordViewModel::class.java)
    setContentView(R.layout.activity_word)
    // Находим View в разметке.
    findViews()
    initToolbar()
    textToSpeech = (applicationContext as App).textToSpeech
    dataAndPrepareInterface
    initSaveButtonClick()
    initAvailableSubgroupsObserver()
  }

  /**
   * Находит View элементы в разметке.
   */
  private fun findViews() {
    wordTextInputEditText = findViewById(R.id.activity_word___text_input_edit_text___word)
    valueTextInputEditText = findViewById(R.id.activity_word___text_input_edit_text___value)
    transcriptionTextInputEditText =
      findViewById(R.id.activity_word___text_input_edit_text___transcription)
    saveButton = findViewById(R.id.activity_word___button___save_word)
    ttsButton = findViewById(R.id.activity_word___button___tts)
    partOfSpeechTextView = findViewById(R.id.activity_word___text_view___part_of_speech)
    toolbar = findViewById(R.id.activity_word___toolbar)
    progressLinearLayout =
      findViewById(R.id.activity_word___linear_layout___progress_view_background)
    examplesRecyclerView = findViewById(R.id.activity_word___recycler_view___examples)
    addExampleButton = findViewById(R.id.activity_word___button___add_example)
  }

  /**
   * Устанавливает toolbar и его title.
   */
  private fun initToolbar() {
    // Устанавливаем тулбар.
    setSupportActionBar(toolbar)
    supportActionBar!!.title = ""
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
      val arguments = intent.extras
      if (arguments != null) {
        // Получаем id слова, которое было выбрано.
        val startTo = arguments.getInt(EXTRA_START_TO)
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
            val word: Word? = arguments.getParcelable(EXTRA_WORD_OBJECT)
            if (word != null) {
              wordViewModel!!.setWord(word)
            }
            wordViewModel!!.wordMutableLiveData.observe(this, Observer { word ->
              Log.d(LOG_TAG, "word onChanged()")
              if (word != null) {
                setWordToViews(word)

                // Делаем доступными для редактирования поля с параметрами слова.
                if (word.createdByUser == 1) {
                  saveButton!!.visibility = View.VISIBLE
                  findViewById<View>(R.id.activity_word___text_input_layout___word).isEnabled = true
                  findViewById<View>(R.id.activity_word___text_input_layout___transcription).isEnabled =
                    true
                  findViewById<View>(R.id.activity_word___text_input_layout___value).isEnabled =
                    true
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
   * Выводит сообщение об ошибке и закрывает activity.
   */
  private fun errorProcessing() {
    Log.e(LOG_TAG, "Error happened!")
    finish()
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
        wordViewModel!!.setWordParameters(word, transcription, value)
        wordViewModel!!.updateWordInDB()
        //
        /*}*/

        // Закрываем Activity.
        finish()
      } else {
        Toast.makeText(
          this@WordActivity,
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
          linkOrDeleteWordDialogFragment.show(supportFragmentManager, "some tag")
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
    val learnProgressView = View(this)
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
    val progressTextView = findViewById<TextView>(R.id.activity_word___text_view___progress)
    progressTextView.visibility = View.GONE
    ttsButton!!.visibility = View.GONE
    partOfSpeechTextView!!.visibility = View.GONE
    toolbar!!.visibility = View.GONE
    progressLinearLayout!!.visibility = View.GONE
    val examplesTextView = findViewById<TextView>(R.id.activity_word___text_view___examples)
    examplesTextView.visibility = View.GONE
    examplesRecyclerView!!.visibility = View.GONE
    addExampleButton!!.visibility = View.GONE
    findViewById<View>(R.id.activity_word___text_input_layout___word).isEnabled = true
    findViewById<View>(R.id.activity_word___text_input_layout___transcription).isEnabled = true
    findViewById<View>(R.id.activity_word___text_input_layout___value).isEnabled = true
    saveButton!!.visibility = View.VISIBLE
  }

  /**
   * Создаёт меню для тулбара.
   */
  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    //super.onCreateOptionsMenu(menu);
    menuInflater.inflate(R.menu.activity_word_toolbar_menu, menu)
    return true
  }

  /**
   * Обрабатывает нажатия на пункты тулбара.
   */
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val manager = supportFragmentManager

    // Bundle для передачи id слова в диалоговый фрагмент, который вызовется.
    val arguments = Bundle()
    return when (item.itemId) {
      R.id.activity_word___action___linkword -> {
        Log.d(LOG_TAG, "Link word")
        linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_LINK
        // Подписываемся на изменение доступных для связывания подгрупп.
        wordViewModel!!.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(
          this,
          availableSubgroupsObserver!!
        )
        true
      }
      R.id.activity_word___action___resetwordprogress -> {
        Log.d(LOG_TAG, "Reset word progress")
        val resetProgressDialogFragment = ResetProgressDialogFragment()
        arguments.putInt(
          ResetProgressDialogFragment.EXTRA_FLAG,
          ResetProgressDialogFragment.FOR_ONE_WORD
        )
        resetProgressDialogFragment.arguments = arguments
        resetProgressDialogFragment.show(manager, DIALOG_RESET_WORD_PROGRESS)
        true
      }
      R.id.delete_word -> {
        Log.d(LOG_TAG, "Delete word")
        linkOrDeleteFlag = LinkOrDeleteWordDialogFragment.TO_DELETE
        // Подписываемся на изменение доступных для связывания подгрупп.
        wordViewModel!!.getAvailableSubgroupsTo(linkOrDeleteFlag).observe(
          this,
          availableSubgroupsObserver!!
        )
        true
      }
      else                                            -> super.onOptionsItemSelected(item)
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

  override fun onLoaded(examples: List<Example>) {
    examplesRecyclerViewAdapter!!.setExamples(examples)
  }

  companion object {
    // Тег для логирования.
    private const val LOG_TAG = "WordActivity"

    // Extras для получения данных из интента.
    const val EXTRA_WORD_ID = "WordId"
    const val EXTRA_WORD = "word"
    const val EXTRA_TRANSCRIPTION = "Transcription"
    const val EXTRA_VALUE = "Value"
    const val EXTRA_START_TO = "StartPurpose"
    const val EXTRA_WORD_OBJECT = "WordObject"

    // Возможные цели старта Activity.
    const val START_TO_CREATE_WORD = 0
    const val START_TO_EDIT_WORD = 1

    // Теги для диалоговых фрагментов.
    private const val DIALOG_RESET_WORD_PROGRESS = "ResetWordProgressDialogFragment"
    private const val DIALOG_LINK_WORD = "LinkWordDialogFragment"
    private const val DIALOG_DELETE_WORD = "DeleteWordDialogFragment"
  }
}