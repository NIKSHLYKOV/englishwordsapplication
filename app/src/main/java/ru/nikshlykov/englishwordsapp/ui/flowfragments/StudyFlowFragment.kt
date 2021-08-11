package ru.nikshlykov.englishwordsapp.ui.flowfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.DaggerFragment
import ru.nikshlykov.englishwordsapp.R
import ru.nikshlykov.englishwordsapp.db.WordsRepository.OnAvailableToRepeatWordLoadedListener
import ru.nikshlykov.englishwordsapp.db.WordsRepository.OnWordUpdatedListener
import ru.nikshlykov.englishwordsapp.db.word.Word
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener
import ru.nikshlykov.englishwordsapp.ui.fragments.FirstShowModeFragment.FirstShowModeReportListener
import ru.nikshlykov.englishwordsapp.ui.viewmodels.StudyViewModel
import ru.nikshlykov.englishwordsapp.utils.Navigation
import javax.inject.Inject

class StudyFlowFragment : DaggerFragment(),
  OnAvailableToRepeatWordLoadedListener, OnWordUpdatedListener, RepeatResultListener,
  FirstShowModeReportListener {
  // ViewModel для работы с БД.
  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null
  private var studyViewModel: StudyViewModel? = null
  private var navController: NavController? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Создаём ViewModel для работы с БД.
    studyViewModel = viewModelFactory!!.create(StudyViewModel::class.java)

    // Получаем выбранные пользователем режимы.
    studyViewModel!!.getSelectedModes(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.flow_fragment_study, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val navHostFragment =
      childFragmentManager.findFragmentById(R.id.flow_fragment_study___nav_host) as NavHostFragment?
    navController = navHostFragment!!.navController
  }

  /**
   * Показывает пришедшее для повтора слово либо в режиме первого просмотра,
   * либо в выбранном пользователем режиме.
   *
   * @param word слово для повтора/первого показа.
   */
  override fun onAvailableToRepeatWordLoaded(word: Word) {
    Log.i(
      LOG_TAG, "word = " + word.word + "; learnProgress = " + word.learnProgress +
        "; lastRepetitionDate = " + word.lastRepetitionDate
    )

    // Создаём Bundle для отправки id слова фрагменту.
    val arguments = Bundle()
    arguments.putParcelable(EXTRA_WORD_OBJECT, word)

    // В зависимости от прогресса по слову показываем для него FirstShowModeFragment или
    // другой ModeFragment.
    if (word.learnProgress == -1) {
      // Показываем фрагмент режима.
      //navController!!.navigate(R.id.action_global_first_show_mode_dest, arguments)
    } else {
      // Получаем id рандомного режимы из выбранных, если слово показывается не первый раз.
      val randomModeId = studyViewModel!!.randomSelectedModeId()
      Log.i(LOG_TAG, "randomModeId = $randomModeId")
      val destinationId = Navigation.getModeDestinationId(randomModeId)
      //navController!!.navigate(destinationId, arguments)
      // TODO РАЗОБРАТЬСЯ, ПОЧЕМУ В MODESFRAGMENTS получаем word null, и раскоментить навигацию.
    }
  }

  /**
   * Запрашивает следующее слово для повтора, если предыдущее обновилось.
   *
   * @param isUpdated показывает, обновилось ли слово.
   */
  override fun onWordUpdated(isUpdated: Int) {
    if (isUpdated == 1) {
      // Делаем проверку на то, что пользователь ещё находится во вкладке изучение,
      // т.к. ответ может прийти позже, чем пользователь сменит вкладку.

      //TODO Если и тут делать проверку, то уже на navController из MainActivity.
      /* if (navController.findFragmentByTag(TAG_STUDY_OR_INFO_FRAGMENT) != null)*/
      studyViewModel!!.getNextAvailableToRepeatWord(this)
    } else {
      Toast.makeText(context, R.string.sorry_error_happened, Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * Обрабатывает результат первого показа слова.
   *
   * @param wordId id слова, которое показывалось.
   * @param result результат (0 - пропусить, 1 - изучать, 2 - знаю).
   */
  override fun firstShowModeResult(wordId: Long, result: Int) {
    Log.i(LOG_TAG, "firstShowModeResult()")
    Log.i(LOG_TAG, "result = $result")
    studyViewModel!!.firstShowProcessing(wordId, result, this)
  }

  /**
   * Обрабытывает результаты повторов (кроме первого показа).
   *
   * @param wordId id повторяемого слова.
   * @param result результат повтора (0 - неверно, 1 - верно).
   */
  override fun repeatResult(wordId: Long, result: Int) {
    studyViewModel!!.repeatProcessing(wordId, result, this)
  }

  companion object {
    // Тег для логирования.
    private val LOG_TAG = StudyFlowFragment::class.java.canonicalName
    const val EXTRA_WORD_OBJECT = "WordObject"
  }
}