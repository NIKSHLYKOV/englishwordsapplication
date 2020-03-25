package ru.nikshlykov.englishwordsapp.ui.study;

import android.content.Context;

import androidx.fragment.app.Fragment;

// Перечисление режимов.
// Необходимо для того, чтобы возвращать определённый вновь созданный фрагмент по id режима.
public enum ModeFactory {
    FIRST_VIEW(0) {
        @Override
        public Fragment createFragment(Context context) {
            return new Mode0Fragment();
        }
    },
    VOC_CARD_ENG_TO_RUS(1) {
        @Override
        public Fragment createFragment(Context context) {
            return new Mode1Fragment();
        }
    },
    VOC_CARD_RUS_TO_ENG(2) {
        @Override
        public Fragment createFragment(Context context) {
            return new Mode2Fragment();
        }
    },
    WRITE_WORD_FROM_VALUE(3) {
        @Override
        public Fragment createFragment(Context context) {
            return new Mode3Fragment();
        }
    },
    COLLECT_WORD_BY_LETTERS(4) {
        @Override
        public Fragment createFragment(Context context) {
            return new Mode4Fragment();
        }
    },
    WRITE_WORD_FROM_VOICE(5) {
        @Override
        public Fragment createFragment(Context context) {
            return new Mode5Fragment();
        }
    },
    CHOOSE_FROM_FOUR_VARIANTS(6) {
        @Override
        public Fragment createFragment(Context context) {
            return new Mode6Fragment();
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
