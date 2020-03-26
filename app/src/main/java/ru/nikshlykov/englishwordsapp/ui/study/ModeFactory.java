package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;

import androidx.fragment.app.Fragment;

// Перечисление режимов.
// Необходимо для того, чтобы возвращать определённый вновь созданный фрагмент по id режима.
public enum ModeFactory {
    FIRST_VIEW(0) {
        @Override
        public Fragment createFragment(Context context) {
            return new FirstShowModeFragment();
        }
    },
    VOC_CARD_ENG_TO_RUS(1) {
        @Override
        public Fragment createFragment(Context context) {
            return new DictionaryCardsModeFragment();
        }
    },
    VOC_CARD_RUS_TO_ENG(2) {
        @Override
        public Fragment createFragment(Context context) {
            return new DictionaryCardsModeFragment();
        }
    },
    WRITE_WORD_BY_VALUE(3) {
        @Override
        public Fragment createFragment(Context context) {
            return new WriteWordByValueModeFragment();
        }
    },
    COLLECT_WORD_BY_LETTERS(4) {
        @Override
        public Fragment createFragment(Context context) {
            return new CollectWordByLettersModeFragment();
        }
    },
    WRITE_WORD_BY_VOICE(5) {
        @Override
        public Fragment createFragment(Context context) {
            return new WriteWordByVoiceModeFragment();
        }
    },
    CHOOSE_FROM_FOUR_VARIANTS(6) {
        @Override
        public Fragment createFragment(Context context) {
            return new ChooseFromFourVariantsModeFragment();
        }
    };

    private final int id;

    ModeFactory(int id) {
        this.id = id;
    }

    public abstract Fragment createFragment(Context context);

    public static ModeFactory byId(int id) {
        for (ModeFactory mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        throw new IllegalStateException();
    }
}
