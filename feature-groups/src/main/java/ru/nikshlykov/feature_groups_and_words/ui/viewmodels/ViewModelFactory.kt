package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddNewWordToSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddWordToSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.DeleteSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.DeleteWordFromSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetAvailableToLinkSubgroupsInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetGroupsWithSubgroupsInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetWordsFromSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.ResetWordsProgressFromSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.UpdateSubgroupInteractor
import javax.inject.Inject

internal class ViewModelFactory @Inject constructor(
    private val getGroupsWithSubgroupsInteractor: GetGroupsWithSubgroupsInteractor,
    private val addSubgroupInteractor: AddSubgroupInteractor,
    private val updateSubgroupInteractor: UpdateSubgroupInteractor,
    private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
    private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor,
    private val getSubgroupInteractor: GetSubgroupInteractor,
    private val deleteSubgroupInteractor: DeleteSubgroupInteractor,
    private val getWordsFromSubgroupInteractor: GetWordsFromSubgroupInteractor,
    private val resetWordsProgressFromSubgroupInteractor: ResetWordsProgressFromSubgroupInteractor,
    private val getAvailableToLinkSubgroupsInteractor: GetAvailableToLinkSubgroupsInteractor,
    private val addNewWordToSubgroupInteractor: AddNewWordToSubgroupInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            GroupsViewModel::class.java -> {
                GroupsViewModel(
                    getGroupsWithSubgroupsInteractor,
                    updateSubgroupInteractor
                ) as T
            }

            SubgroupViewModel::class.java -> {
                SubgroupViewModel(
                    getSubgroupInteractor,
                    addWordToSubgroupInteractor,
                    deleteWordFromSubgroupInteractor,
                    updateSubgroupInteractor,
                    deleteSubgroupInteractor,
                    getWordsFromSubgroupInteractor,
                    resetWordsProgressFromSubgroupInteractor,
                    getAvailableToLinkSubgroupsInteractor
                ) as T
            }

            WordDialogsViewModel::class.java -> {
                WordDialogsViewModel(
                    addWordToSubgroupInteractor
                ) as T
            }

            SubgroupDataViewModel::class.java -> {
                SubgroupDataViewModel(
                    getSubgroupInteractor,
                    addSubgroupInteractor,
                    updateSubgroupInteractor
                ) as T
            }

            AddWordViewModel::class.java -> {
                AddWordViewModel(addNewWordToSubgroupInteractor) as T
            }

            else -> {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}