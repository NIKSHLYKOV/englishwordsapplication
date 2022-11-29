package ru.nikshlykov.feature_study.di

import android.app.Application
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.daos.WordDao
import ru.nikshlykov.feature_study.navigation.StudyFeatureRouter

interface StudyFeatureDeps {

  val application: Application

  val studyFeatureRouter: StudyFeatureRouter

  val textToSpeech: TextToSpeech

  val modeDao: ModeDao

  val wordDao: WordDao

  val repeatDao: RepeatDao

  val applicationScope: CoroutineScope
}