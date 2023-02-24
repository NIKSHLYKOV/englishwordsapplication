package ru.nikshlykov.feature_word.di

import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.daos.SubgroupDao
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.feature_word.navigation.WordFeatureRouter

interface WordFeatureDeps {

  val linkDao: LinkDao

  val subgroupDao: SubgroupDao

  val wordDao: WordDao

  val textToSpeech: TextToSpeech

  val applicationScope: CoroutineScope

  val wordFeatureRouter: WordFeatureRouter
}