package ru.netology.nmedia.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepositoryImpl

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    suspend fun updateUser(login: String, pass: String): AuthState {
        return repository.updateUser(login, pass)
    }

    suspend fun setNewUser(login: String, pass: String, name: String): AuthState {
        return repository.registerUserNoPhoto(login, pass, name)
    }
}
