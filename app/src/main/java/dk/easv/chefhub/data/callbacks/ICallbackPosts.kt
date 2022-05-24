package dk.easv.chefhub.data.callbacks

import dk.easv.chefhub.models.BePost

interface ICallbackPosts {
    fun onPostsReady(posts: ArrayList<BePost>)
    fun onError(error: String)
}