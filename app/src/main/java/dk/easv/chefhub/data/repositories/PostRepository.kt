package dk.easv.chefhub.data.repositories

import android.util.Log
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import dk.easv.chefhub.data.HttpHelper
import dk.easv.chefhub.data.ICallbackPost
import dk.easv.chefhub.data.ICallbackPosts
import dk.easv.chefhub.data.Properties
import dk.easv.chefhub.data.entities.CreatePostDto
import dk.easv.chefhub.models.BeUser
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception

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
                return callback.onError(error?.message.toString())
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
        httpClient.get("${Properties.BACKEND_URL}/posts/get/$postId", object : AsyncHttpResponseHandler() {
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

    fun createPost(createPostDto: CreatePostDto, postImage: File, username: String) {
        var params = RequestParams()

        params.put("title", createPostDto.title)
        params.put("desc", createPostDto.desc)
        try {
            params.put("file", postImage, "image/jpeg")
        } catch (e: FileNotFoundException) {
            Log.d("ERRORS", e.stackTraceToString())
            return
        }

        httpClient.post("${Properties.BACKEND_URL}/posts/upload", params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                Log.d("XYZ", "Post created: ${responseBody.toString()}")
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("ERROR", "Post creation failed")
            }
        })
    }

}