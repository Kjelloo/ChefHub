package dk.easv.chefhub.data

import dk.easv.chefhub.models.LoggedInUser

interface ICallbackLogin {
    fun onLoginReady(user: LoggedInUser)
    fun onError(error: String)
}