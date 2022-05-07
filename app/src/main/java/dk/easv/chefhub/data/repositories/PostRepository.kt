package dk.easv.chefhub.data.repositories

import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import dk.easv.chefhub.data.HttpHelper
import dk.easv.chefhub.data.ICallbackPosts
import dk.easv.chefhub.data.Properties

class PostRepository {
    private val baseURl = Properties.BACKEND_URL
    private val httpClient: AsyncHttpClient = AsyncHttpClient()
    private val httpHelper: HttpHelper = HttpHelper()

    constructor(token: String) {
        httpClient.addHeader("Authorization", "Bearer $token")
    }

    fun getPostFeed(callback: ICallbackPosts) {
        httpClient.get("$baseURl/posts/feed", object : AsyncHttpResponseHandler() {
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

}