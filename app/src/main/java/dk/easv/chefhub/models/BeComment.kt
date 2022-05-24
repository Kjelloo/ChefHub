package dk.easv.chefhub.models

import org.json.JSONObject

data class BeComment(val id: Int, val msg: String, val user: BeUser, val post: BePost) {

    constructor(jsonObject: JSONObject) :
            this((jsonObject["id"] as Int),
                (jsonObject["msg"] as String),
                BeUser(
                    jsonObject.getJSONObject("user").getInt("id"),
                    jsonObject.getJSONObject("user").getString("username"),
                    jsonObject.getJSONObject("user").getString("name"),
                    jsonObject.getJSONObject("user").getString("photoUrl")),
                if (jsonObject.has("post")) {
                    BePost(jsonObject.getJSONObject("post"))
                } else {
                    BePost(
                        -1,
                        BeUser(
                        jsonObject.getJSONObject("user").getInt("id"),
                        jsonObject.getJSONObject("user").getString("username"),
                        jsonObject.getJSONObject("user").getString("name"),
                        jsonObject.getJSONObject("user").getString("photoUrl")),
                        "", "", "", ArrayList())
                })
}
