package dk.easv.chefhub.data.callbacks

import dk.easv.chefhub.models.BeUser

interface ICallbackUser {
    fun onUserReady(user: BeUser)
    fun onError(error: String)
}