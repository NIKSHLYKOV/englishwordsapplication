package ru.nikshlykov.englishwordsapp.utils;

import ru.nikshlykov.englishwordsapp.R;

public class Navigation {
    public static int getModeDestinationId(long modeId) {
        switch ((int) modeId) {
            case 1:
            case 2:
                return R.id.action_global_dictionary_cards_mode_dest;
            case 3:
                return R.id.action_global_write_word_by_value_mode_dest;
            case 4:
                return R.id.action_global_collect_word_by_letters_mode_dest;
            case 5:
                return R.id.action_global_write_word_by_voice_mode_dest;
            default:
                return R.id.action_global_dictionary_cards_mode_dest;
        }
    }
}
