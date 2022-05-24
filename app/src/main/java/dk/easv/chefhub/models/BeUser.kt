package dk.easv.chefhub.models

import org.json.JSONObject

data class BeUser(val id: Int, val username: String, val name: String, val photoUrl: String) {
    constructor(jsonObject: JSONObject) :
            this(
                jsonObject["id"] as Int,
                jsonObject["username"] as String,
                jsonObject["name"] as String,
                jsonObject["photoUrl"] as String
            )
}