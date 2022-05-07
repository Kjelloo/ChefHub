package dk.easv.chefhub.login

import dk.easv.chefhub.models.LoggedInUser

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
     val success: LoggedInUser? = null,
     val error:Int? = null
)