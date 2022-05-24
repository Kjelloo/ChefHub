package dk.easv.chefhub.data.callbacks

import dk.easv.chefhub.models.BeComment
import dk.easv.chefhub.models.BePost

interface ICallbackPost {
    fun onPostReady(post: BePost)
}