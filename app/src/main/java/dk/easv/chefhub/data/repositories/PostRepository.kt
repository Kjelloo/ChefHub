package dk.easv.chefhub.data.repositories

import android.util.Log
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import dk.easv.chefhub.data.HttpHelper
import dk.easv.chefhub.data.ICallbackPost
import dk.easv.chefhub.data.ICallbackPosts
import dk.easv.chefhub.data.Properties

class PostRepository {
    private val httpClient: AsyncHttpClient = AsyncHttpClient()
    private val httpHelper: HttpHelper = HttpHelper()

    constructor(token: String) {
        httpClient.addHeader("Authorization", "Bearer $token")
    }

    fun getPostFeed(callback: ICallbackPosts) {
        httpClient.get("${Properties.BACKEND_URL}/posts/feed", object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val posts = httpHelper.getPostsFromResponse(String(responseBody!!))
                return callback.onPostsReady(posts)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("ERRORS", "Error fetching posts: ${error?.message}")
            }
        })
    }

    fun getUserPosts(callback: ICallbackPosts) {
        httpClient.get("${Properties.BACKEND_URL}/posts", object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val posts = httpHelper.getPostsFromResponse(String(responseBody!!))
                return callback.onPostsReady(posts)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("ERRORS", "Error fetching posts: ${error?.message}")
            }
        })
    }

    fun getPostById(postId: Int, callback: ICallbackPost) {
        httpClient.get("${Properties.BACKEND_URL}/posts/$postId", object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                return callback.onPostReady(httpHelper.getPostById(String(responseBody!!))!!)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("ERRORS", "Error fetching post: ${error?.message}")
            }
        })
    }

}