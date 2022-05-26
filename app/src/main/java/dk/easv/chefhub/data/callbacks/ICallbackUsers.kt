package dk.easv.chefhub.data.callbacks

import dk.easv.chefhub.models.BeUser

interface ICallbackUsers {
    fun onUsersReady(users: ArrayList<BeUser>)
    fun onError(error: String)
}