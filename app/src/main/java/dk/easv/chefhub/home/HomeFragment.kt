package dk.easv.chefhub.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dk.easv.chefhub.R
import dk.easv.chefhub.data.ICallbackPosts
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.databinding.FragmentHomeBinding
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.LoggedInUser

class HomeFragment : Fragment() {

    private lateinit var user: LoggedInUser
    private lateinit var postsRepo: PostRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Get user from shared preferences (local storage)
        sharedPrefs = this.requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        user = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)

        postsRepo = PostRepository(user.token)
        //postsRepo = PostRepository("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImNvYm8iLCJpYXQiOjE2NTE5NjA5ODcsImV4cCI6MTY1MTk2NDU4N30.xosE6S0-9eo0qVbjtiiQg28jCYiu-zLDeNxyXUmsISo")

        // Loading indicator layout
        val loadingPostLayout = view?.findViewById<RelativeLayout>(R.id.loadingLayout)

        // Fetch post feed from backend
        try {
            postsRepo.getPostFeed(object: ICallbackPosts {
                override fun onPostsReady(posts: ArrayList<BePost>) {
                    if (posts.size > 0) {
                        loadFeed(requireContext(), posts)

                        // Hide the loading indicator
                        loadingPostLayout?.visibility  = View.GONE
                    }
                }
            })
        } catch (e: Exception) {
            Log.d("ERRORS", e.message!!)
            Toast.makeText(requireContext(), "Cannot fetch posts: " + e.message, Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    private fun loadFeed(context: Context, posts: ArrayList<BePost>) {
        val adapter = PostAdapter(context, posts)
        val lvPosts = this.requireActivity().findViewById<ListView>(R.id.lvPosts)

        // Set list view visibility when loading is done
        lvPosts.visibility = View.VISIBLE
        lvPosts.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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