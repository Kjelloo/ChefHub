package dk.easv.chefhub.data

import android.util.Log
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.LoggedInUser
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class HttpHelper {
    fun getPostsFromResponse(response: String?) : ArrayList<BePost> {
        val res = ArrayList<BePost>()

        if (response!!.startsWith("error")) {
            Log.d("ERRORS", "Error: $response")
            return res
        }

        if (response == null) {
            Log.d("ERRORS", "Error: NO RESULT")
        }

        var array: JSONArray?

        try {
            array = JSONArray(response)

            for (i in 0 until array.length()) {
                res.add(BePost(array.getJSONObject(i)))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return res
    }

    fun getLoggedInUserFromResponse(response: String?, username: String): LoggedInUser? {
        lateinit var res: LoggedInUser

        if (response!!.startsWith("error")) {
            Log.d("ERRORS", "Error: $response")
        }

        if (response == null) {
            Log.d("ERRORS", "Error: NO RESULT")
            return null
        }

        try {
            res = LoggedInUser(JSONObject(response))
            res.username = username
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }

        return res
    }

    fun getPostById(response: String?): BePost? {
        lateinit var res: BePost

        if (response!!.startsWith("error")) {
            Log.d("ERRORS", "Error: $response")
        }

        if (response == null) {
            Log.d("ERRORS", "Error: NO RESULT")
            return null
        }

        try {
            res = BePost(JSONObject(response))
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }

        return res
    }
}