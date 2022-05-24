package dk.easv.chefhub.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.easv.chefhub.data.repositories.LoginRepository

import dk.easv.chefhub.R
import dk.easv.chefhub.data.callbacks.ICallbackLogin
import dk.easv.chefhub.models.LoggedInUser

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        loginRepository.login(username, password,
            object: ICallbackLogin {
                override fun onLoginReady(user: LoggedInUser) {
                    _loginResult.value = LoginResult(success = user)
                    Log.d("XYZ", user.token)

                }

                override fun onError(error: String) {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
            })
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = "Username must not be blank")
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = "Password must not be blank")
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }
}