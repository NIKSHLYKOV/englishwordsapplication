package ru.nikshlykov.feature_groups_and_words.di

import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import ru.nikshlykov.data.database.daos.GroupDao
import ru.nikshlykov.data.database.daos.LinkDao
import ru.nikshlykov.data.database.daos.SubgroupDao
import ru.nikshlykov.data.database.daos.WordDao

interface GroupsFeatureDeps {

  val groupDao: GroupDao

  val linkDao: LinkDao

  val subgroupDao: SubgroupDao

  val wordDao: WordDao

  val textToSpeech: TextToSpeech

  val applicationScope: CoroutineScope
}