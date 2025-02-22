package ru.nikshlykov.feature_groups_and_words.domain.interactors

import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.repositories.LinksRepository
import ru.nikshlykov.feature_groups_and_words.domain.repositories.SubgroupsRepository

internal class GetAvailableToLinkSubgroupsInteractor(
    private val subgroupsRepository: SubgroupsRepository,
    private val linksRepository: LinksRepository
) {

    /**
     * Находит список подгрупп доступных для добавления в них слова.
     *
     * @param wordId id слова, для которого ищутся данные подгруппы.
     *
     * @return доступные подгруппы или пустой список, если таковых нет.
     */
    suspend fun getAvailableToLinkSubgroups(wordId: Long): List<Subgroup> {
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
                // Удаляем из коллекции те id подгрупп, с которыми уже связано слово.
                availableSubgroupsIds.removeAll(linkedWithWordSubgroupsIds)

                // Теперь коллекция действительно содержит id доступных подгрупп.
                val availableSubgroups: ArrayList<Subgroup>
                // Проверяем, что коллекция не пустая
                if (availableSubgroupsIds.size != 0) {
                    // Заполняем список доступных подгрупп.
                    availableSubgroups = ArrayList(availableSubgroupsIds.size)
                    for (availableSubgroupId in availableSubgroupsIds) {
                        availableSubgroups.add(
                            subgroupsRepository.getSubgroupById(
                                availableSubgroupId
                            )
                        )
                    }
                    availableSubgroups
                } else {
                    ArrayList()
                }
            } else {
                ArrayList(createdByUserSubgroups)
            }
        } else {
            ArrayList()
        }
    }
}