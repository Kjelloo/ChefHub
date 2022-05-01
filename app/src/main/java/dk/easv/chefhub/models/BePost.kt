package dk.easv.chefhub.models

data class BePost(val id: Int, val user: BeUser,val title: String, val desc: String, val photoUrl: String) {
}