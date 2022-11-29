package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository
import java.util.*
import kotlin.collections.HashSet

internal class GetAvailableSubgroupsInteractor(
  private val subgroupsRepository: SubgroupsRepository,
  private val linksRepository: LinksRepository
) {

  /**
   * Находит список подгрупп доступных для добавления в них слова или удаления слова из них.
   *
   * @param wordId id слова, для которого ищутся данные подгруппы.
   * @param flagTo флаг для удаления или добавления.
   *
   * @return доступные подгруппы или пустой список, если таковых нет.
   */
  suspend fun getAvailableSubgroups(wordId: Long, flagTo: Int): List<Subgroup> {
    val createdByUserSubgroups = subgroupsRepository.getSubgroupsCreatedByUser()
    return if (createdByUserSubgroups.isNotEmpty()) {
      // Получаем все связи нашего слова и проверяем, что они вообще есть.
      // На данный момент они точно будут, но если мы будем делать что-то типа словаря,
      // в котором слово не обязательно залинковано с подгруппой, то эта проверка потребуется.
      val linksWithWord = linksRepository.getLinksByWordId(wordId)
      if (linksWithWord.isNotEmpty()) {

        // Делаем коллекцию из id связанных с нашим словом подгрупп и заполняем её.
        val linkedWithWordSubgroupsIds = HashSet<Long>()
        for (link in linksWithWord) {
          linkedWithWordSubgroupsIds.add(link.subgroupId)
        }

        // Делаем коллекцию доступных подгрупп и заполняем её пока созданными группами.
        val availableSubgroupsIds: HashSet<Long> = HashSet(createdByUserSubgroups.size)
        for (subgroup in createdByUserSubgroups) {
          availableSubgroupsIds.add(subgroup.id)
        }
        if (flagTo == TO_LINK) {
          // Удаляем из коллекции те id подгрупп, с которыми уже связано слово.
          availableSubgroupsIds.removeAll(linkedWithWordSubgroupsIds)
        } else if (flagTo == TO_DELETE) {
          // Оставляем в коллекции те id подгрупп, с которыми уже связано слово.
          availableSubgroupsIds.retainAll(linkedWithWordSubgroupsIds)
        }

        // Теперь коллекция действительно содержит id доступных подгрупп.
        val availableSubgroups: ArrayList<Subgroup>
        // Проверяем, что коллекция не пустая
        if (availableSubgroupsIds.size != 0) {
          // Заполняем список доступных подгрупп.
          availableSubgroups = ArrayList(availableSubgroupsIds.size)
          for (availableSubgroupId in availableSubgroupsIds) {
            availableSubgroups.add(subgroupsRepository.getSubgroupById(availableSubgroupId))
          }
          availableSubgroups
        } else {
          ArrayList()
        }
      } else {
        if (flagTo == TO_LINK) {
          ArrayList(createdByUserSubgroups)
        } else {
          ArrayList()
        }
      }
    } else {
      ArrayList()
    }
  }

  companion object {
    const val TO_LINK = 1
    const val TO_DELETE = 2
  }
}