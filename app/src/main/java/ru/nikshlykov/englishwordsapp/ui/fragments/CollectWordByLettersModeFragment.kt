package ru.nikshlykov.englishwordsapp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.Random;

import ru.nikshlykov.englishwordsapp.R;
import ru.nikshlykov.englishwordsapp.db.word.Word;
import ru.nikshlykov.englishwordsapp.ui.activities.MainActivity;
import ru.nikshlykov.englishwordsapp.ui.RepeatResultListener;

public class CollectWordByLettersModeFragment extends Fragment {

    // Тег для логирования.
    private static final String LOG_TAG = "CollectWordByLettersMF";

    private Context context;

    // View.
    private TextView valueTextView;
    private ImageButton removeLetterImageButton;
    private TextView userVariantTextView;
    private GridLayout lettersGridLayout;
    private ImageView resultImageView;

    // ViewModel для работы с БД и словом.
    //private WordViewModel wordViewModel;
    private Word word;

    // Интерфейс для передачи результата повтора.
    private RepeatResultListener repeatResultListener;

    private Handler handler;

    // Стек, в который помещаются кнопки, чтобы можно было удалять последнюю букву в слове.
    private ArrayList<View> invisibleButtons;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        repeatResultListener = (RepeatResultListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем id слова.
        word = getArguments().getParcelable(MainActivity.EXTRA_WORD_OBJECT);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                repeatResultListener.repeatResult(word.id, msg.what);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_word_by_letters_mode, null);
        findViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        removeLetterImageButton.setEnabled(false);

        // Устанавливаем перевод.
        valueTextView.setText(word.value);

        // Получаем слово на английском и находим его длину.
        String wordOnEnglish = word.word;
        final int lettersCount = wordOnEnglish.length();

        invisibleButtons = new ArrayList<>(lettersCount);

        ArrayList<Character> shuffleLetters = getShuffleCharacters(wordOnEnglish);

        // Закидываем случайно расставленные буквы в кнопки по порядку.
        for (int i = 0; i < lettersCount; i++) {
            Button button = initCharButton(word, shuffleLetters.get(i));
            lettersGridLayout.addView(button);
        }

        initRemoveButton();
    }

    /**
     * Присваивает обработчик кнопке удаления последнего символа.
     */
    private void initRemoveButton() {
        removeLetterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invisibleButtons.size() != 0) {
                    String currentText = userVariantTextView.getText().toString();
                    String newText = currentText.substring(0, currentText.length() - 1);
                    userVariantTextView.setText(newText);

                    invisibleButtons.remove(invisibleButtons.size() - 1)
                            .setVisibility(View.VISIBLE);

                    if (invisibleButtons.size() == 0) {
                        removeLetterImageButton.setEnabled(false);
                    }
                }
            }
        });
    }

    /**
     * Создаёт кнопку для символа. Задаёт ей стиль, параметры высоты и ширины, а также отступы.
     *
     * @param word   слово, для которого вызвался режим.
     * @param letter символ, который будет текстом на кнопке (как правило, это буква).
     * @return кнопку.
     */
    private Button initCharButton(final Word word, Character letter) {
        final int lettersCount = word.word.length();

        Button button = new Button(new ContextThemeWrapper(context,
                R.style.BorderlessButton), null, 0);
        button.setBackgroundResource(R.drawable.shape_white_color_primary_15dp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPx(50), dpToPx(50));
        layoutParams.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        button.setLayoutParams(layoutParams);
        button.setText(letter.toString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                String letter = ((TextView) v).getText().toString();
                String currentText = userVariantTextView.getText().toString();
                String newText = currentText + letter;
                userVariantTextView.setText(newText);

                invisibleButtons.add(v);
                if (invisibleButtons.size() == 1)
                    removeLetterImageButton.setEnabled(true);

                if (invisibleButtons.size() == lettersCount) {

                    // Скрывает View элементы.
                    valueTextView.setVisibility(View.GONE);
                    removeLetterImageButton.setVisibility(View.GONE);
                    userVariantTextView.setVisibility(View.GONE);
                    lettersGridLayout.setVisibility(View.GONE);

                    ConstraintLayout mainLayout = (ConstraintLayout) v.getParent().getParent()
                            .getParent();

                    // Расчитываем результат (верно/не верно). В зависомости от этого выводим
                    // определённый фон и значок.
                    int result = 0;
                    String userVariantOfWord = userVariantTextView.getText().toString();
                    if (userVariantOfWord.equals(word.word)) {
                        result = 1;
                        resultImageView.setImageResource(R.drawable.ic_done_white_48dp);
                        mainLayout.setBackgroundResource(R.color.progress_4);
                    } else {
                        resultImageView.setImageResource(R.drawable.ic_clear_white_48dp);
                        mainLayout.setBackgroundResource(R.color.progress_1);
                    }
                    resultImageView.setVisibility(View.VISIBLE);

                    // Закидываем handler'у отложенное сообщение, чтобы фон и значок немного повисели.
                    handler.sendEmptyMessageDelayed(result, 1000);
                }
            }
        });
        return button;
    }

    /**
     * Перемешивает символы в строке.
     *
     * @param string строка для перемешивания.
     * @return список из случайно добавленных в него символов.
     */
    private ArrayList<Character> getShuffleCharacters(String string) {
        int lettersCount = string.length();
        // Делаем список букв слова.
        ArrayList<Character> letters = new ArrayList<>(lettersCount);
        for (int i = 0; i < lettersCount; i++) {
            letters.add(string.charAt(i));
        }
        // Делаем список случайно расставленных букв слова.
        ArrayList<Character> shuffleLetters = new ArrayList<>(lettersCount);
        while (letters.size() != 0) {
            Random random = new Random();
            int removeLetterIndex = random.nextInt(letters.size());
            shuffleLetters.add(letters.remove(removeLetterIndex));
        }
        return shuffleLetters;
    }

    /**
     * Находит View элементы в разметке.
     *
     * @param v корневой элемент разметки.
     */
    private void findViews(View v) {
        userVariantTextView = v.findViewById(R.id.fragment_collect_word_by_letters_mode___text_view___user_variant);
        removeLetterImageButton = v.findViewById(R.id.fragment_collect_word_by_letters_mode___image_button___remove_letter);
        valueTextView = v.findViewById(R.id.fragment_collect_word_by_letters_mode___text_view___value);
        lettersGridLayout = v.findViewById(R.id.fragment_collect_word_by_letters_mode___grid_layout___letters);
        resultImageView = v.findViewById(R.id.fragment_collect_word_by_letters_mode___image_view___result);
    }

    // ПОСМОТРЕТЬ, КАК МОЖНО ОТ ЭТОГО ИЗБАВИТЬСЯ
    public int dpToPx(int dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
