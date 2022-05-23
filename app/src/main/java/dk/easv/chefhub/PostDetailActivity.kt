package dk.easv.chefhub

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dk.easv.chefhub.data.ICallbackPost
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.home.POST_ID
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.LoggedInUser

class PostDetailActivity : AppCompatActivity() {

    private lateinit var postRepository: PostRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()
    private lateinit var user: LoggedInUser

    private lateinit var authorName: TextView
    private lateinit var authorProfileImage: ImageView
    private lateinit var postImage: ImageView
    private lateinit var postTitle: TextView
    private lateinit var postDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        setLoggedInUser()
        supportActionBar?.hide()

        // Back arrow
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.postDetailToolbar)

        // Close activity when back arrow is pressed
        toolbar.setNavigationOnClickListener { super.onBackPressed() }

        authorName = findViewById(R.id.tvPostAuthorDetail)
        authorProfileImage = findViewById(R.id.ivProfilePictureDetail)
        postImage = findViewById(R.id.ivPostDetail)
        postTitle = findViewById(R.id.tvPostTitleDetail)
        postDescription = findViewById(R.id.tvPostDescriptionDetail)

        postRepository = PostRepository(user.token)

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
                        updateUi(post)
                    }
                }
            })
        }
    }

    private fun updateUi(post: BePost) {
        // Author username
        authorName.text = post.user.username

        // Author profile picture
        Picasso
            .get()
            .load(post.user.photoUrl)
            .placeholder(R.color.accentBackGround)
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

    private fun setLoggedInUser() {
        // Get user from shared preferences (local storage)
        sharedPrefs = this
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        user = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)
    }
}