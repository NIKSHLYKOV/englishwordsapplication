package ru.nikshlykov.feature_modes.di

import ru.nikshlykov.data.database.daos.ModeDao
import ru.nikshlykov.feature_modes.navigation.ModesRouterSource

interface ModesFeatureDeps {

  val modesRouterSource: ModesRouterSource

  val modeDao: ModeDao
}