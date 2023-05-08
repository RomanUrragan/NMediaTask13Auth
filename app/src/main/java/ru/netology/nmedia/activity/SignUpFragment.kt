package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val viewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val binding = _binding!!

        viewModel.photo.observe(viewLifecycleOwner) {
            if (viewModel.photo.value?.uri != null) {
                binding.imageAvatar.setImageURI(viewModel.photo.value?.uri)
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> viewModel.changePhoto(it.data?.data)
                }
            }

        binding.imageAvatar.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        with(binding) {

            buttonSignUp.setOnClickListener {
                if (editName.text.isBlank() || editLogin.text.isBlank() ||
                    editPassword.text.isBlank() || editPasswordConfirm.text.isBlank()
                ) {
                    textError.text = getString(R.string.emptyLogInFieldsError)
                    textError.visibility = View.VISIBLE
                } else if (editPasswordConfirm.text.toString() != editPassword.text.toString()) {
                    textError.text = getString(R.string.passwords_dont_match)
                    textError.visibility = View.VISIBLE
                } else {

                    textError.visibility = View.GONE
                    val name = editName.text.toString()
                    val login = editLogin.text.toString()
                    val pass = editPassword.text.toString()
                    val avatarUri = viewModel.photo.value?.uri
                    if (avatarUri != null) {
                        lifecycleScope.launch {
                            try {
                                val user = viewModel.setNewUserWithPhoto(
                                    login,
                                    pass,
                                    name,
                                    MediaUpload(avatarUri.toFile())
                                )
                                viewModel.removePhoto()
                                AppAuth.getInstance().setAuth(user.id, user.token!!)
                                findNavController().navigateUp()
                            } catch (e: ApiError) {
                                if (e.status == ApiError.USER_NOT_FOUND) {
                                    textError.text = getString(R.string.loginIsTaken)
                                    textError.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        lifecycleScope.launch {
                            try {
                                val user = viewModel.setNewUserNoPhoto(login, pass, name)
                                AppAuth.getInstance().setAuth(user.id, user.token!!)
                                findNavController().navigateUp()
                            } catch (e: ApiError) {
                                if (e.status == ApiError.USER_NOT_FOUND) {
                                    textError.text = getString(R.string.loginIsTaken)
                                    textError.visibility = View.VISIBLE
                                }
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
        viewModel.removePhoto()
        _binding = null
    }
}
