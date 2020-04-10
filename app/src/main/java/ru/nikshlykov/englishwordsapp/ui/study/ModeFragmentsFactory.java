package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;

import androidx.fragment.app.Fragment;

// Перечисление режимов.
// Необходимо для того, чтобы возвращать определённый вновь созданный фрагмент по id режима.
public enum ModeFragmentsFactory {
    VOC_CARD_ENG_TO_RUS(1L) {
        @Override
        public Fragment createFragment(Context context) {
            DictionaryCardsModeFragment fragment = new DictionaryCardsModeFragment();
            fragment.setFlag(DictionaryCardsModeFragment.FLAG_ENG_TO_RUS);
            return fragment;
        }
    },
    VOC_CARD_RUS_TO_ENG(2L) {
        @Override
        public Fragment createFragment(Context context) {
            DictionaryCardsModeFragment fragment = new DictionaryCardsModeFragment();
            fragment.setFlag(DictionaryCardsModeFragment.FLAG_RUS_TO_ENG);
            return fragment;
        }
    },
    WRITE_WORD_BY_VALUE(3L) {
        @Override
        public Fragment createFragment(Context context) {
            return new WriteWordByValueModeFragment();
        }
    },
    COLLECT_WORD_BY_LETTERS(4L) {
        @Override
        public Fragment createFragment(Context context) {
            return new CollectWordByLettersModeFragment();
        }
    },
    WRITE_WORD_BY_VOICE(5L) {
        @Override
        public Fragment createFragment(Context context) {
            return new WriteWordByVoiceModeFragment();
        }
    },
    CHOOSE_FROM_FOUR_VARIANTS(6L) {
        @Override
        public Fragment createFragment(Context context) {
            return new ChooseFromFourVariantsModeFragment();
        }
    };

    private final long id;

    ModeFragmentsFactory(long id) {
        this.id = id;
    }

    public abstract Fragment createFragment(Context context);

    public static ModeFragmentsFactory byId(long id) {
        for (ModeFragmentsFactory mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        throw new IllegalStateException();
    }
}
