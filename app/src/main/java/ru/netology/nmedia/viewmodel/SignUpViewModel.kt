package ru.netology.nmedia.viewmodel


import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepositoryImpl

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    private val noPhoto = PhotoModel()
    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    suspend fun setNewUserNoPhoto(login: String, pass: String, name: String): AuthState {
        return repository.registerUserNoPhoto(login, pass, name)
    }

    suspend fun setNewUserWithPhoto(login: String, pass: String, name: String, upload: MediaUpload): AuthState {
        return repository.registerUserWithPhoto(login, pass, name, upload)
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

    fun removePhoto() {
        _photo.value = PhotoModel()
    }
}
