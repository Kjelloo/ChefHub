package dk.easv.chefhub.data.repositories

import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import dk.easv.chefhub.data.HttpHelper
import dk.easv.chefhub.data.Properties
import dk.easv.chefhub.data.callbacks.ICallbackUser

class UserRepository(token: String) {
    private val httpClient: AsyncHttpClient = AsyncHttpClient()
    private val httpHelper: HttpHelper = HttpHelper()

    init {
        httpClient.addHeader("Authorization", "Bearer $token")
    }

    fun getUser(callback: ICallbackUser, username: String) {
        val params = RequestParams()

        params.put("username", username)

        httpClient.get("${Properties.BACKEND_URL}/users", object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val user = httpHelper.getUserFromResponse(String(responseBody!!))
                return callback.onUserReady(user!!)
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
}