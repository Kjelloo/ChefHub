package dk.easv.chefhub.scrolling

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dk.easv.chefhub.R
import dk.easv.chefhub.data.ICallbackPosts
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.databinding.ActivityScrollingBinding
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.LoggedInUser
import kotlin.Exception

class ScrollingActivity : AppCompatActivity() {

    private lateinit var user: LoggedInUser
    private lateinit var binding: ActivityScrollingBinding
    private lateinit var postsRepo: PostRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user from shared preferences (local storage)
        sharedPrefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        user = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)

        postsRepo = PostRepository(user.token)

        // Loading indicator layout
        val loadingPostLayout = findViewById<RelativeLayout>(R.id.loadingLayout)

        // Fetch post feed from backend
        try {
            postsRepo.getPostFeed(object:ICallbackPosts {
                override fun onPostsReady(posts: ArrayList<BePost>) {
                    if (posts.size > 0) {
                        Log.d("BLA", posts.toString())
                        loadFeed(applicationContext, posts)

                        // Hide the loading indicator
                        loadingPostLayout.visibility  = View.GONE
                    }
                }
            })
        } catch (e: Exception) {
            Log.d("ERRORS", e.message!!)
            Toast.makeText(applicationContext, "Cannot fetch posts: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun loadFeed(context: Context, posts: ArrayList<BePost>) {
        val adapter = PostAdapter(context, posts)
        val lvPosts = findViewById<ListView>(R.id.lvPosts)

        // Set list view visibility when loading is done
        lvPosts.visibility = View.VISIBLE
        lvPosts.adapter = adapter
    }

    internal class PostAdapter(context: Context, private val posts: ArrayList<BePost>): ArrayAdapter<BePost>(context, 0, posts) {
        override fun getView(position: Int, v: View?, parent: ViewGroup): View {
            var v1: View? = v

            if (v1 == null) {
                val mInflater = LayoutInflater.from(context)
                v1 = mInflater.inflate(R.layout.cell_extended, null)
            }

            val resView: View = v1!!

            val postAuthorProfilePicture = resView.findViewById<ImageView>(R.id.ivProfilePicture)
            val postPhotoView = resView.findViewById<ImageView>(R.id.ivPostPhoto)
            val postAuthor = resView.findViewById<TextView>(R.id.tvPostAuthor)
            val postTitle = resView.findViewById<TextView>(R.id.tvPostTitle)
            val postDescription = resView.findViewById<TextView>(R.id.tvPostDescription)

            // Load profile picture
            Picasso
                .get()
                .load(posts[position].user.photoUrl)
                .placeholder(R.drawable.person)
                .fit()
                .into(postAuthorProfilePicture)

            // Load post picture
            Picasso
                .get()
                .load(posts[position].photoUrl)
                .placeholder(R.color.accentBackGround)
                .fit()
                .into(postPhotoView)

            // Set post author
            postAuthor.text = posts[position].user.username

            // Set post title
            postTitle.text = posts[position].title

            // Set post description
            postDescription.text = posts[position].desc

            return resView
        }
    }
}