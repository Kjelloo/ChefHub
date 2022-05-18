package dk.easv.chefhub.data.repositories

import android.util.Log
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestHandle
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import dk.easv.chefhub.data.*
import dk.easv.chefhub.models.LoggedInUser
import kotlin.Result

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository() {
    private val httpClient: AsyncHttpClient = AsyncHttpClient()
    private val httpHelper: HttpHelper = HttpHelper()

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
    }

    fun login(username: String, password: String, callback: ICallbackLogin) {
        // Body params
        var params = RequestParams()
        params.put("username", username)
        params.put("password", password)

        httpClient.post("${Properties.BACKEND_URL}/auth/login", params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                return callback.onLoginReady(httpHelper.getLoggedInUserFromResponse(String(responseBody!!))!!)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("ERRORS", "Error logging in: ${error?.message}")
                return callback.onError(error?.message.toString())
            }
        })
    }
}