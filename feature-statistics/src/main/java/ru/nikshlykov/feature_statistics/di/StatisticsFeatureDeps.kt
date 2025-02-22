package ru.nikshlykov.feature_statistics.di

import ru.nikshlykov.data.database.daos.RepeatDao

interface StatisticsFeatureDeps {

    val repeatDao: RepeatDao
}