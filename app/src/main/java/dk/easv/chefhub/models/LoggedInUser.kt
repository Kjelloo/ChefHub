package dk.easv.chefhub.models

import org.json.JSONObject

data class LoggedInUser(
    val id: Int,
    val username: String,
    val token: String,
) {
    constructor(jsonObject: JSONObject) :
            this(-1,
                "placeholder name",
                jsonObject["access_token"] as String)
}