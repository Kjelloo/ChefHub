package dk.easv.chefhub.data

import dk.easv.chefhub.models.BePost

interface ICallbackPosts {
    fun onPostsReady(posts: ArrayList<BePost>)
    fun onError(error: String)
}