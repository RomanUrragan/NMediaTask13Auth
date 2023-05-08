package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.viewmodel.SignInViewModel

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val binding = _binding!!
        with(binding) {
            buttonSignIn.setOnClickListener {
                if (editLogin.text.isBlank() || editPassword.text.isBlank()) {
                    textError.text = getString(R.string.emptyLogInFieldsError)
                    textError.visibility = View.VISIBLE
                } else {
                    textError.visibility = View.GONE
                    val login = editLogin.text.toString()
                    val pass = editPassword.text.toString()
                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            val user = viewModel.updateUser(login, pass)
                            AppAuth.getInstance().setAuth(user.id, user.token!!)
                            findNavController().navigateUp()
                        } catch (e: ApiError) {
                            if (e.status == ApiError.USER_NOT_FOUND) {
                                textError.text = getString(R.string.incorrectLoginOrPassword)
                                textError.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
