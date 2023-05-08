package ru.netology.nmedia.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.PostRepositoryImpl

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    suspend fun updateUser(login: String, pass: String): AuthState {
        try {
            return repository.updateUser(login, pass)
        } catch (e: ApiError) {
            throw e
        }
    }
}
