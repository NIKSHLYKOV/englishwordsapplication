package ru.nikshlykov.englishwordsapp.preferences

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import ru.nikshlykov.englishwordsapp.R

class NewWordsCountPreference  /*private NumberPicker wordsCountNumberPicker;*/
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
  /*public NewWordsNumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.number_picker_dialog);
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancel);

        setDialogIcon(null);
    }*/
  /**
   * Вызывается, когда закрывается диалог.
   * /@param positiveResult флаг, указывающий на то, какая кнопка была нажата для выхода из диалога.
   */
  /*
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            // Сохраняем новое значение параметра.
            persistInt(newWordsCount);
        }
    }*/
  /**
   * Устанавливает значение при вызове диалога.
   * /@param restorePersistedValue флаг, указывающий на то, было ли сохранено до этого значение.
   * /@param defaultValue значение по умолчанию для того, чтобы
   */
  /*
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Достаём значение, если оно уже было сохранено.
            newWordsCount = getPersistedInt(DEFAULT_VALUE);
        } else {
            // Устанавливаем значение по умолчанию, если оно ещё не выставлялось.
            newWordsCount = (Integer) defaultValue;
            persistInt(newWordsCount);
        }
    }*/
  /**
   * Возвращает default value, который мы сами устанавливаем.
   * @param a массив атрибутов в xml.
   * @param index индекс элемента defaultValue в xml.
   * @return значение по умолчанию.
   */
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
  } /*@Override
    protected View onCreateDialogView() {
        int max = 100;
        int min = 1;

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_picker_dialog, null);

        wordsCountNumberPicker = view.findViewById(R.id.number_picker_dialog___number_picker);

        // Initialize state
        wordsCountNumberPicker.setMaxValue(max);
        wordsCountNumberPicker.setMinValue(min);
        wordsCountNumberPicker.setValue(this.getPersistedInt(DEFAULT_VALUE));
        wordsCountNumberPicker.setWrapSelectorWheel(false);

        return view;
    }*/

  // Код из статьи о настройках на developer.android.com.
  /*private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readInt();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent,
            // use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.value = newWordsCount;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        // Set this Preference's widget to reflect the restored state
        wordsCountNumberPicker.setValue(myState.value);
    }*/
  companion object {
    const val DEFAULT_VALUE = 5
  }
}