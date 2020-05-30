package ru.nikshlykov.englishwordsapp.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.nikshlykov.englishwordsapp.R;

public class InfoFragment extends Fragment {

    // Ключ для передачи флага фрагменту.
    public static final String KEY_INFO_FLAG = "InfoFlag";

    // Флаги.
    public static final int FLAG_MODES_ARE_NOT_CHOSEN = 1;
    public static final int FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING = 2;

    // Флаг, получаемый из Activity.
    private int flag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("InfoFragment", "onCreate");
        Bundle arguments = getArguments();
        try {
            flag = arguments.getInt(KEY_INFO_FLAG);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("InfoFragment", "onCreateView");
        // Получаем разметку для фрагмента.
        View v = inflater.inflate(R.layout.fragment_info, null);
        // Находим textView для вывода текста.
        TextView infoText = v.findViewById(R.id.fragment_info___text_view___info);
        // Объявляем переменную для текста и находим необходимый для вывода текст.
        String text = "";
        switch (flag){
            case FLAG_MODES_ARE_NOT_CHOSEN:
                text = "Для того, чтобы изучать слова, необходимо выбрать режимы изучения. " +
                        "Сделать это вы можете перейдя во вкладку \"Профиль\" в пункт \"Режимы\"";
                break;
            case FLAG_AVAILABLE_WORDS_ARE_NOT_EXISTING:
                text = "Нет доступных слов на данный момент! " +
                        "Выбери группы, если ты ещё этого не делал или если ты выучил все слова из выбранных групп.";
                break;
        }
        // Устанавливаем текст в TextView.
        infoText.setText(text);
        // Возвращаем View.
        return v;
    }
}