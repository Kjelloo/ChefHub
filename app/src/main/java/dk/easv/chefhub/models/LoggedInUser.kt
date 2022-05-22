package dk.easv.chefhub.models

import org.json.JSONObject

data class LoggedInUser(
    val id: Int,
    var username: String,
    val token: String,
) {
    constructor(jsonObject: JSONObject) :
            this(-1,
                "",
                jsonObject["access_token"] as String)
}