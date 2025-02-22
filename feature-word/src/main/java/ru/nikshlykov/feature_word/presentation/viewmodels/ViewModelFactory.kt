package ru.nikshlykov.feature_word.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.nikshlykov.feature_word.domain.interactors.AddWordToSubgroupInteractor
import ru.nikshlykov.feature_word.domain.interactors.DeleteWordFromSubgroupInteractor
import ru.nikshlykov.feature_word.domain.interactors.GetAvailableSubgroupsInteractor
import ru.nikshlykov.feature_word.domain.interactors.GetWordInteractor
import ru.nikshlykov.feature_word.domain.interactors.ResetWordProgressInteractor
import ru.nikshlykov.feature_word.domain.interactors.UpdateWordInteractor
import javax.inject.Inject

internal class ViewModelFactory @Inject constructor(
    private val addWordToSubgroupInteractor: AddWordToSubgroupInteractor,
    private val deleteWordFromSubgroupInteractor: DeleteWordFromSubgroupInteractor,
    private val updateWordInteractor: UpdateWordInteractor,
    private val resetWordProgressInteractor: ResetWordProgressInteractor,
    private val getWordInteractor: GetWordInteractor,
    private val getAvailableSubgroupsInteractor: GetAvailableSubgroupsInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            WordDialogsViewModel::class.java -> {
                WordDialogsViewModel(
                    addWordToSubgroupInteractor,
                    deleteWordFromSubgroupInteractor
                ) as T
            }

            WordViewModel::class.java -> {
                WordViewModel(
                    getWordInteractor,
                    updateWordInteractor,
                    resetWordProgressInteractor,
                    getAvailableSubgroupsInteractor
                ) as T
            }

            else -> {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}