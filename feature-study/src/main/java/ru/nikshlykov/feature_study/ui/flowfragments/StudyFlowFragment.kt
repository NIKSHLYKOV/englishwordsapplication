package ru.nikshlykov.feature_study.ui.flowfragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.nikshlykov.data.database.models.Word
import ru.nikshlykov.feature_study.NavigationStudyDirections
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.domain.interactors.GetAvailableToRepeatWordInteractor
import ru.nikshlykov.feature_study.ui.fragments.InfoFragment
import ru.nikshlykov.feature_study.ui.fragments.modesfragments.FirstShowModeFragment.FirstShowModeReportListener
import ru.nikshlykov.feature_study.ui.fragments.modesfragments.RepeatResultListener
import ru.nikshlykov.feature_study.ui.viewmodels.StudyFeatureComponentViewModel
import ru.nikshlykov.feature_study.ui.viewmodels.StudyViewModel
import ru.nikshlykov.feature_study.utils.ModesNavigation
import javax.inject.Inject

class StudyFlowFragment : Fragment(),
  GetAvailableToRepeatWordInteractor.OnAvailableToRepeatWordLoadedListener, RepeatResultListener,
  FirstShowModeReportListener {

  private val studyFeatureComponentViewModel: StudyFeatureComponentViewModel by viewModels()
  // Убрал BackPressed, т.к. он всё равно не тот бы был, что в app модуле.

  // ViewModel для работы с БД.
  @JvmField
  @Inject
  var viewModelFactory: ViewModelProvider.Factory? = null
  private var studyViewModel: StudyViewModel? = null
  private var navController: NavController? = null

  override fun onAttach(context: Context) {
    studyFeatureComponentViewModel.modesFeatureComponent.inject(this)
    super.onAttach(context)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Создаём ViewModel для работы с БД.
    studyViewModel = viewModelFactory!!.create(StudyViewModel::class.java)

    // Получаем выбранные пользователем режимы.
    studyViewModel!!.startStudying(this)
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
    studyViewModel!!.modesSelected.observe(viewLifecycleOwner, { modesSelected ->
      if (modesSelected == false) {
        val navDirections = NavigationStudyDirections.actionGlobalInfoDest()
          .setInfoFlag(InfoFragment.FLAG_MODES_ARE_NOT_CHOSEN)
        navController!!.navigate(navDirections)
      }
    })
  }

  /**
   * Показывает пришедшее для повтора слово либо в режиме первого просмотра,
   * либо в выбранном пользователем режиме.
   *
   * @param word слово для повтора/первого показа.
   */
  override fun onAvailableToRepeatWordLoaded(word: Word?) {
    if (word == null) {
      val navDirections = NavigationStudyDirections.actionGlobalInfoDest()
        .setInfoFlag(InfoFragment.FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING)
      navController!!.navigate(navDirections)
    } else {
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
        val navDirections = NavigationStudyDirections.actionGlobalFirstShowModeDest(word)
        navController!!.navigate(navDirections)
      } else {
        // Получаем id рандомного режимы из выбранных, если слово показывается не первый раз.
        val randomModeId = studyViewModel!!.randomSelectedModeId()
        Log.i(LOG_TAG, "randomModeId = $randomModeId")
        // Переходим к рандомно выбранному режиму
        val navDirections = ModesNavigation.getRandomModeNavDirections(randomModeId, word)
        navController!!.navigate(navDirections)
      }
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