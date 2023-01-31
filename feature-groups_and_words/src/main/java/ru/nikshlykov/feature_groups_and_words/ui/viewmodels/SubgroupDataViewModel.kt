package ru.nikshlykov.feature_groups_and_words.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.nikshlykov.data.database.models.Subgroup
import ru.nikshlykov.feature_groups_and_words.domain.interactors.AddSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.GetSubgroupInteractor
import ru.nikshlykov.feature_groups_and_words.domain.interactors.UpdateSubgroupInteractor
import java.io.IOException

internal class SubgroupDataViewModel(
  private val getSubgroupInteractor: GetSubgroupInteractor,
  private val addSubgroupInteractor: AddSubgroupInteractor,
  private val updateSubgroupInteractor: UpdateSubgroupInteractor
) : ViewModel() {
  private val _subgroup: MutableLiveData<Subgroup> = MutableLiveData()
  val subgroup: LiveData<Subgroup> = _subgroup

  private val _subgroupImage: MutableStateFlow<Bitmap?> = MutableStateFlow(null)
  val subgroupImage: StateFlow<Bitmap?> = _subgroupImage

  private var subgroupNewImage: Bitmap? = null

  private val _subgroupIsInsertedOrUpdated: MutableLiveData<Boolean> = MutableLiveData()
  val subgroupIsInsertedOrUpdated: LiveData<Boolean> = _subgroupIsInsertedOrUpdated

  fun loadSubgroupAndPhoto(context: Context, subgroupId: Long) {
    viewModelScope.launch {
      _subgroup.value = getSubgroupInteractor.getSubgroupById(subgroupId)

      val files = context.filesDir.listFiles()
      val photos =
        files?.filter { it.canRead() && it.isFile && it.name.equals(_subgroup.value?.imageName) }
          ?.map {
            val bytes = it.readBytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
          } ?: listOf()
      if (photos.isNotEmpty()) {
        _subgroupImage.emit(photos.last())
      }
    }
  }

  fun setSubgroupNewImage(bitmap: Bitmap) {
    subgroupNewImage = bitmap
    viewModelScope.launch {
      _subgroupImage.emit(subgroupNewImage)
    }
  }

  // TODO прописать удаление старого фото при добавлении нового
  fun addOrUpdateSubgroup(subgroupName: String, context: Context) {
    val subgroupToUpdate = subgroup.value
    // TODO сделать проверку уникальности имени (иначе одно и то же фото будет для нескольких
    //  подгрупп). Да и зачем несколько подгрупп с одним именем
    viewModelScope.launch {
      var isNewImageExists = false
      val newImageName = subgroupName + System.currentTimeMillis() + ".jpg"

      // Сохраняем фото
      val photoSavingIsSuccessfully =
        subgroupNewImage?.let {
          isNewImageExists = true
          savePhotoToInternalStorage(newImageName, it, context)
        } ?: true

      // Сохраняем группу
      val subgroupSavingIsSuccessfully =
        if (subgroupToUpdate != null) {
          subgroupToUpdate.name = subgroupName
          if (isNewImageExists){
            subgroupToUpdate.imageName = newImageName
          }
          updateSubgroupInteractor.updateSubgroup(subgroupToUpdate) == 1
        } else {
          addSubgroupInteractor.addSubgroup(subgroupName, newImageName) != 0L
        }

      // TODO СРОЧНО ИСПРАВИТЬ. МОЖЕТ БЫТЬ ОШИБКА, КОГДА БД ОБНОВИТСЯ, А ФОТО - НЕТ. ИЛИ НАОБОРОТ
      if (subgroupSavingIsSuccessfully && photoSavingIsSuccessfully) {
        _subgroupIsInsertedOrUpdated.postValue(true)
      }
    }
  }

  private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap, context: Context): Boolean {
    return try {
      context.openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
        if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
          throw IOException("Couldn't save bitmap.")
        }
      }
      true
    } catch (e: IOException) {
      e.printStackTrace()
      false
    }
  }
}