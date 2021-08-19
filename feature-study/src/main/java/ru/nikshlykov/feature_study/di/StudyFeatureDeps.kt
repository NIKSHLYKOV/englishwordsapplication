package ru.nikshlykov.feature_study.di

import android.app.Application
import android.speech.tts.TextToSpeech
import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.data.database.daos.RepeatDao
import ru.nikshlykov.data.database.daos.WordDao

interface StudyFeatureDeps {

  val application: Application

  val textToSpeech: TextToSpeech

  val modeDao: ModeDao

  val wordDao: WordDao

  val repeatDao: RepeatDao
}