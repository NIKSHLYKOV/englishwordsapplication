package ru.nikshlykov.feature_preferences.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import ru.nikshlykov.feature_preferences.R

internal class NotificationTimePreference @JvmOverloads constructor(
  context: Context?, attrs: AttributeSet? = null,
  defStyleAttr: Int = 0, defStyleRes: Int = defStyleAttr
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

  private val LAYOUT_RES_ID = R.layout.notification_time_picker_dialog

  var time = 0
    set(time) {
      field = time
      persistInt(time)
    }

  override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
    return a.getInt(index, DEFAULT_VALUE)
  }

  override fun onSetInitialValue(defaultValue: Any?) {
    super.onSetInitialValue(defaultValue)
    time = getPersistedInt(DEFAULT_VALUE)
  }

  override fun getDialogLayoutResource(): Int {
    return LAYOUT_RES_ID
  }

  companion object {
    private const val DEFAULT_VALUE = 0
  }
}