package dk.easv.chefhub.models

import org.json.JSONObject

// Used to save the logged in users token, BeUser will primarily to get information of the logged in user
data class LoggedInUser(
    var username: String,
    val token: String,
) {
    constructor(jsonObject: JSONObject) :
            this("placeholder", jsonObject["access_token"] as String)
}