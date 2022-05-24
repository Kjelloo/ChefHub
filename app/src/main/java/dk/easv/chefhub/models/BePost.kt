package dk.easv.chefhub.models

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class BePost(var id: Int, var user: BeUser,var title: String, var desc: String, var photoUrl: String, val comments: ArrayList<BeComment>) {
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
                jsonObject["photoUrl"] as String,
                if (jsonObject.has("comments")) {
                    getComments(jsonObject.getJSONArray("comments"))
                } else{
                    ArrayList<BeComment>()
                })
}

fun getComments(jsonArray: JSONArray): ArrayList<BeComment> {
    val res = ArrayList<BeComment>()

    var array: JSONArray?

    try {
        array = jsonArray

        for (i in 0 until array.length()) {
            res.add(BeComment(array.getJSONObject(i)))
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    return res
}