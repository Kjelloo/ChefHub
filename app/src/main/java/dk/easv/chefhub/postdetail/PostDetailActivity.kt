package dk.easv.chefhub.postdetail

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dk.easv.chefhub.R
import dk.easv.chefhub.data.Properties
import dk.easv.chefhub.data.callbacks.ICallbackPost
import dk.easv.chefhub.data.callbacks.ICallbackUser
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.data.repositories.UserRepository
import dk.easv.chefhub.databinding.ActivityPostDetailBinding
import dk.easv.chefhub.home.POST_ID
import dk.easv.chefhub.models.BeComment
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.BeUser
import dk.easv.chefhub.models.LoggedInUser
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject


class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()

    private lateinit var postRepository: PostRepository

    private lateinit var userRepository: UserRepository
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var commentRecyclerView: RecyclerView

    // Need a "whole" user object of the person logged in
    private lateinit var commentingUser: BeUser

    private lateinit var currentPost: BePost
    private lateinit var authorName: TextView
    private lateinit var authorProfileImage: ImageView
    private lateinit var postImage: ImageView
    private lateinit var postTitle: TextView
    private lateinit var postDescription: TextView

    private lateinit var postCommentsAdapter: PostDetailRecyclerViewAdapter
    private lateinit var postNoComments: TextView
    private lateinit var postSendCommentButton: Button

    private lateinit var postWriteCommentAuthorPicture: ImageView
    private lateinit var postWriteComment: EditText

    private var socket: Socket = IO.socket(Properties.BACKEND_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        socket.connect()

        setLoggedInUser()
        supportActionBar?.hide()

        // Back arrow
        val toolbar = binding.postDetailToolbar

        // Close activity when back arrow is pressed
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        authorName = binding.tvPostAuthorDetail
        authorProfileImage = binding.ivProfilePictureDetail
        postImage = binding.ivPostDetail
        postTitle = binding.tvPostTitleDetail
        postDescription = binding.tvPostDescriptionDetail

        postNoComments = binding.tvNoComments

        postCommentsAdapter = PostDetailRecyclerViewAdapter()
        commentRecyclerView = binding.rvComments
        commentRecyclerView.visibility = View.GONE

        postWriteCommentAuthorPicture = binding.commentAuthorPicture
        postWriteComment = binding.commentEditText
        postSendCommentButton = binding.btnSendComment

        postRepository = PostRepository(loggedInUser.token)
        userRepository = UserRepository(loggedInUser.token)

        postSendCommentButton.setOnClickListener { sendComment() }

        // Get user object from backend.
        userRepository.getUser(object : ICallbackUser {
            override fun onUserReady(user: BeUser) {
                commentingUser = user
                updateWritingCommentUi()
            }

            override fun onError(error: String) {
                Log.d("ERRORS", error)
            }
        }, loggedInUser.username)

        var postId: Int? = null

        // Get post id from previous fragment
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            postId = bundle.getInt(POST_ID)
        }

        postId.let {
            postRepository.getPostById(postId!!, object: ICallbackPost {
                override fun onPostReady(post: BePost) {
                    if(post != null) {
                        updatePostUi(post)
                        currentPost = post

                        newCommentListener()

                        if (post.comments.size > 0) {
                            setCommentUi(post.comments)
                        }
                    }
                }
            })
        }
    }

    private fun newCommentListener() {
        // Listen for new comments
        socket.on("post:${currentPost.id}") { args ->
            var comment: BeComment

            if (args[0] != null) {
                comment = BeComment(args[0] as JSONObject)

                runOnUiThread {
                    // Update UI according to how many comments it has

                    // If post has no previous comments
                    if (currentPost.comments.size == 0) {
                        val comments = ArrayList<BeComment>()
                        comments.add(comment)
                        setCommentUi(comments)
                        currentPost.comments.add(comment)
                    } else {
                        // If post already has posts
                        updateCommentUI(comment)
                    }
                }
            }
        }
    }

    // Send comment to backend with socket io
    private fun sendComment() {

        val hm = mutableMapOf<String, String>()
        hm["msg"] = postWriteComment.text.toString()
        hm["userId"] = commentingUser.id.toString()
        hm["postId"] = currentPost.id.toString()

        val comment = JSONObject((hm as Map<*, *>?)!!)

        socket.emit("createComment", comment)
    }

    // Logged in user comment ui
    private fun updateWritingCommentUi() {
        Picasso
            .get()
            .load(commentingUser.photoUrl)
            .placeholder(R.drawable.person)
            .fit()
            .into(postWriteCommentAuthorPicture)
    }

    private fun updatePostUi(post: BePost) {
        // Author username
        authorName.text = post.user.username

        // Author profile picture
        Picasso
            .get()
            .load(post.user.photoUrl)
            .placeholder(R.drawable.person)
            .fit()
            .into(authorProfileImage)

        // Post image
        Picasso
            .get()
            .load(post.photoUrl)
            .placeholder(R.drawable.person)
            .error(R.drawable.person)
            .fit()
            .centerCrop()
            .into(postImage)

        // Post title
        postTitle.text = post.title

        // Post description
        postDescription.text = post.desc
    }

    private fun setCommentUi(comments: ArrayList<BeComment>) {
        postCommentsAdapter.submitList(comments)

        postNoComments.visibility = View.GONE
        commentRecyclerView.visibility = View.VISIBLE
        commentRecyclerView.adapter = postCommentsAdapter
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun updateCommentUI(comment: BeComment) {
        val commentList = ArrayList<BeComment>()
        commentList.addAll(postCommentsAdapter.currentList)
        commentList.add(comment)
        postCommentsAdapter.submitList(commentList)
    }

    private fun setLoggedInUser() {
        // Get user from shared preferences (local storage)
        sharedPrefs = this
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        loggedInUser = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Disconnect socket and remove listener
        socket.disconnect()
        socket.off("post:${currentPost.id}")
    }
}