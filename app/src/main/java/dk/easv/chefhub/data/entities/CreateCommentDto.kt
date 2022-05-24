package dk.easv.chefhub.data.entities

data class CreateCommentDto(val msg: String, val userId: Int, val postId: Int)
