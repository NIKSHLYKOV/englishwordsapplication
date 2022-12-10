package ru.nikshlykov.feature_preferences.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import ru.nikshlykov.feature_preferences.R

internal class NewWordsCountPreference
@JvmOverloads constructor(
  context: Context?, attrs: AttributeSet? = null,
  defStyleAttr: Int = 0, defStyleRes: Int = defStyleAttr
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

  private val LAYOUT_RES_ID = R.layout.number_picker_dialog

  var newWordsCount = 0
    set(newWordsCount) {
      field = newWordsCount
      persistInt(newWordsCount)
    }

  override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
    // Как я понимаю, если в xml мы укажем defaultValue, то возьмётся оно. Иначе возмётся
    // DEFAULT_VALUE, указанное в классе.
    return a.getInteger(index, DEFAULT_VALUE)
  }

  override fun onSetInitialValue(defaultValue: Any?) {
    super.onSetInitialValue(defaultValue)
    newWordsCount = getPersistedInt(DEFAULT_VALUE)
  }

  override fun getDialogLayoutResource(): Int {
    return LAYOUT_RES_ID
  }

  companion object {
    const val DEFAULT_VALUE = 5
  }
}