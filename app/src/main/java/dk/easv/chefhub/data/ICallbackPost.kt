package dk.easv.chefhub.data

import dk.easv.chefhub.models.BePost

interface ICallbackPost {
    fun onPostReady(post: BePost)
}