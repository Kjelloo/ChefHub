package dk.easv.chefhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.squareup.picasso.Picasso
import dk.easv.chefhub.databinding.ActivityScrollingBinding
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.BeUser

class ScrollingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        val user1 = BeUser(1, "Kjell", "Kjell Schoke", "", "blabla")

        // TODO: fix images urls
        // TODO: Fetch posts from backend endpoint
        val post1 = BePost(1, user1, "First post", "My cool first recipe!", "src/main/res/drawable/placeholder.png")
        val post2 = BePost(2, user1, "Second post", "My cool second recipe!", "src/main/res/drawable/placeholder.png")
        val post3 = BePost(3, user1, "Third post", "My cool third recipe!", "src/main/res/drawable/placeholder.png")

        val posts: ArrayList<BePost> = ArrayList()
        posts.add(post1)
        posts.add(post2)
        posts.add(post3)

        val adapter = PostAdapter(this, posts)
        val lvPosts = findViewById<ListView>(R.id.lvPosts)

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

            val postPhotoView = resView.findViewById<ImageView>(R.id.ivPostPhoto)
            val postAuthor = resView.findViewById<TextView>(R.id.tvPostAuthor)
            val postTitle = resView.findViewById<TextView>(R.id.tvPostTitle)
            val postDescription = resView.findViewById<TextView>(R.id.tvPostDescription)

            if(postPhotoView != null) {

                // TODO: Change placeholder image
                // Load picture
                Picasso
                    .get()
                    .load("https://i.imgur.com/DvpvklR.png")
                    .error(R.drawable.placeholder)
                    .resize(postPhotoView.width, postPhotoView.maxHeight)
                    .onlyScaleDown()
                    .into(postPhotoView)
            }

            postAuthor.text = posts[position].user.username
            postTitle.text = posts[position].title
            postDescription.text = posts[position].desc

            return resView
        }
    }
}