package dk.easv.chefhub.data.repositories

import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import dk.easv.chefhub.data.HttpHelper
import dk.easv.chefhub.data.Properties
import dk.easv.chefhub.data.callbacks.ICallbackUser
import dk.easv.chefhub.data.callbacks.ICallbackUsers

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
                Log.d("ERRORS", "Error fetching user: ${error?.message}")
                return callback.onError(error?.message.toString())
            }
        })
    }

    fun searchUser(callback: ICallbackUsers, username: String) {
        httpClient.get("${Properties.BACKEND_URL}/users/search/${username}", object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val users = httpHelper.getUsersFromResponse(String(responseBody!!))
                return callback.onUsersReady(users!!)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                return callback.onError(error?.message.toString())
            }

            override fun getUseSynchronousMode(): Boolean {
                return false
            }
        })
    }

    fun followUser(callback: ICallbackUser, username: String) {
        httpClient.post("${Properties.BACKEND_URL}/users/follow/${username}", object : AsyncHttpResponseHandler() {
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
                Log.d("ERRORS", "Error following user: ${error?.message}")
                return callback.onError(error?.message.toString())
            }
        })
    }
}