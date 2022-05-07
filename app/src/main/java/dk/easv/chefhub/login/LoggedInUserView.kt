package dk.easv.chefhub.login

data class LoggedInUserView(
    val id: Int,
    val username: String,
    val token: String
)