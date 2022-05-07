package dk.easv.chefhub.models

import org.json.JSONArray
import org.json.JSONObject

data class BePost(val id: Int, val user: BeUser,val title: String, val desc: String, val photoUrl: String) {

    // TODO: Change constructor to take the logged in user as the author of the post
    // TODO: Maybe change to init {}
    constructor(jsonObject: JSONObject) :
            this((jsonObject["id"] as Int),
                BeUser(jsonObject.getJSONObject("user").getInt("id"),
                       jsonObject.getJSONObject("user").getString("username"),
                       jsonObject.getJSONObject("user").getString("name"),
                       jsonObject.getJSONObject("user").getString("photoUrl")),
                jsonObject["title"] as String,
                jsonObject["desc"] as String,
                jsonObject["photoUrl"] as String)
}