package dk.easv.chefhub.data.repositories

import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import dk.easv.chefhub.data.HttpHelper
import dk.easv.chefhub.data.callbacks.ICallbackPost
import dk.easv.chefhub.data.callbacks.ICallbackPosts
import dk.easv.chefhub.data.Properties
import dk.easv.chefhub.data.entities.CreatePostDto
import java.io.File
import java.io.FileNotFoundException

class PostRepository(token: String) {
    private val httpClient: AsyncHttpClient = AsyncHttpClient()
    private val httpHelper: HttpHelper = HttpHelper()

    init {
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
                return callback.onPostReady(httpHelper.getPostByIdFromResponse(String(responseBody!!))!!)
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

    fun createPost(createPostDto: CreatePostDto, postImage: File) {
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
            ) {}

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