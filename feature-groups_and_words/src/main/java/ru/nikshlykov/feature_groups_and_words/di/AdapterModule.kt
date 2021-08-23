package ru.nikshlykov.feature_groups_and_words.di

import android.speech.tts.TextToSpeech
import dagger.Module
import dagger.Provides
import ru.nikshlykov.feature_groups_and_words.ui.adapters.WordsRecyclerViewAdapter

@Module
internal class AdapterModule {

  @Provides
  @GroupsFeatureScope
  fun provideWordsRecyclerViewAdapter(textToSpeech: TextToSpeech): WordsRecyclerViewAdapter{
    return WordsRecyclerViewAdapter(textToSpeech)
  }
}