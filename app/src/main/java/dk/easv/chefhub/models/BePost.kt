package dk.easv.chefhub.models

import org.json.JSONArray
import org.json.JSONObject

data class BePost(var id: Int, var user: BeUser,var title: String, var desc: String, var photoUrl: String) {

    constructor(jsonObject: JSONObject) :
            this((jsonObject["id"] as Int),
                if (jsonObject.has("user")) {
                    BeUser(
                        jsonObject.getJSONObject("user").getInt("id"),
                        jsonObject.getJSONObject("user").getString("username"),
                        jsonObject.getJSONObject("user").getString("name"),
                        jsonObject.getJSONObject("user").getString("photoUrl"))
                } else {
                BeUser(-1, "", "", "")
                },
                jsonObject["title"] as String,
                jsonObject["desc"] as String,
                jsonObject["photoUrl"] as String)

}