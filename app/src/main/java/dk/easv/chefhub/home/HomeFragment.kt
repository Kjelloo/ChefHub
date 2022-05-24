package dk.easv.chefhub.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import dk.easv.chefhub.postdetail.PostDetailActivity
import dk.easv.chefhub.R
import dk.easv.chefhub.data.callbacks.ICallbackPosts
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.databinding.FragmentHomeBinding
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.LoggedInUser


const val POST_ID = "post id"

class HomeFragment : Fragment() {

    private lateinit var user: LoggedInUser
    private lateinit var postsRepo: PostRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()
    private lateinit var postFeedAdapter: HomeRecyclerViewAdapter

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setLoggedInUser()

         postFeedAdapter = HomeRecyclerViewAdapter {post -> adapterOnClick(post)}

        // Fetch post feed from backend
        try {
            postsRepo.getPostFeed(object: ICallbackPosts {
                override fun onPostsReady(posts: ArrayList<BePost>) {
                    Log.d("XYZ", posts.size.toString())
                    if (posts.size > 0) {
                        // Load data into adapter
                        postFeedAdapter.submitList(posts)
                        loadFeed()

                        // Hide the loading indicator
                        binding.loadingLayout?.visibility  = View.GONE
                    }
                }

                override fun onError(error: String) {
                    binding.cpiPostsLoading.visibility = View.GONE
                    binding.tvLoadingPosts.text = "Currently no posts can be shown..."
                }
            })
        } catch (e: Exception) {
            Log.d("ERRORS", e.message!!)
            Toast.makeText(requireContext(), "Cannot fetch posts: " + e.message, Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    private fun loadFeed() {
        val rvPosts = _binding?.rvPosts

        // Set list view visibility when loading is done
        rvPosts?.visibility = View.VISIBLE
        rvPosts?.adapter = postFeedAdapter
    }

    // When a post is clicked
    private fun adapterOnClick(post: BePost) {
        val intent = Intent(requireContext(), PostDetailActivity()::class.java)
        intent.putExtra(POST_ID, post.id)
        startActivity(intent)
    }

    private fun setLoggedInUser() {
        // Get user from shared preferences (local storage)
        sharedPrefs = this.requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        user = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)

        postsRepo = PostRepository(user.token)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}