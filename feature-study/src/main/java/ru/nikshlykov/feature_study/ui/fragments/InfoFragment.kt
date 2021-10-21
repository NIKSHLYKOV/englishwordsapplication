package ru.nikshlykov.feature_study.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import ru.nikshlykov.feature_study.R
import ru.nikshlykov.feature_study.navigation.StudyFragmentNavigation

// TODO сделать перепроверку режимов после взаимодействия с фичей режимов.
//  Возможно, надо просто в подписке на режимы прописать startStudying.
//  Проверить, как работает этот метод, не будет ли ошибок каких-то.
internal class InfoFragment : Fragment() {
  // Флаг, получаемый из Activity.
  private var flag = 0

  private var studyFragmentNavigation: StudyFragmentNavigation? = null

  override fun onAttach(context: Context) {
    Log.d("ProfileFragment", "onAttach()")
    super.onAttach(context)
    studyFragmentNavigation =
      if (requireParentFragment().parentFragment is StudyFragmentNavigation) {
        requireParentFragment().parentFragment as StudyFragmentNavigation?
      } else {
        throw RuntimeException(
          requireParentFragment().parentFragment.toString()
            + " must implement ProfileFragmentNavigation"
        )
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.i("InfoFragment", "onCreate")
    flag = InfoFragmentArgs.fromBundle(requireArguments()).infoFlag
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Log.d("InfoFragment", "onCreateView")
    // Получаем разметку для фрагмента.
    val v = inflater.inflate(R.layout.fragment_info, null)
    // Находим textView для вывода текста.
    val infoText = v.findViewById<TextView>(R.id.fragment_info___text_view___info)
    // Объявляем переменную для текста и находим необходимый для вывода текст.
    var text = ""
    when (flag) {
      FLAG_MODES_ARE_NOT_CHOSEN -> {
        text =
          "Для того, чтобы изучать слова, необходимо выбрать режимы изучения. " +
            "Сделать это вы можете перейдя во вкладку \"Профиль\" в пункт \"Режимы\""
        val goToModesButton: MaterialButton =
          v.findViewById(R.id.fragment_info___material_button___to_modes)
        goToModesButton.visibility = View.VISIBLE
        goToModesButton.setOnClickListener {
          studyFragmentNavigation?.openModes()
        }
      }
      FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING -> text = "Нет доступных слов на данный момент! " +
        "Выбери группы, если ты ещё этого не делал или если ты выучил все слова из выбранных групп."
    }
    // Устанавливаем текст в TextView.
    infoText.text = text
    // Возвращаем View.
    return v
  }

  companion object {
    // Ключ для передачи флага фрагменту.
    const val KEY_INFO_FLAG = "infoFlag"

    // Флаги.
    const val FLAG_MODES_ARE_NOT_CHOSEN = 1
    const val FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING = 2
  }
}