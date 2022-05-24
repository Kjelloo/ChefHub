package dk.easv.chefhub.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import dk.easv.chefhub.postdetail.PostDetailActivity

import dk.easv.chefhub.R
import dk.easv.chefhub.data.callbacks.ICallbackPosts
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.databinding.FragmentProfileBinding
import dk.easv.chefhub.home.POST_ID
import dk.easv.chefhub.models.BePost
import dk.easv.chefhub.models.LoggedInUser

class ProfileFragment : Fragment() {

    private lateinit var user: LoggedInUser
    private var _binding: FragmentProfileBinding? = null
    private lateinit var postsRepo: PostRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()
    private var myPosts: ArrayList<BePost> = ArrayList()
    private var savedPosts: ArrayList<BePost> = ArrayList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Get user from shared preferences (local storage)
        setLoggedInUser()

        postsRepo = PostRepository(user.token)

        val myPostsAdapter = ProfileRecyclerViewAdapter { post -> adapterOnClick(post) }
        val savedPostsAdapter = ProfileRecyclerViewAdapter { post -> adapterOnClick(post) }

        val recyclerView: RecyclerView = binding.imageGallery
        recyclerView.layoutManager = GridLayoutManager(context, 4)

        // Fetch post feed from backend
        try {
            postsRepo.getUserPosts(object: ICallbackPosts {
                override fun onPostsReady(posts: ArrayList<BePost>) {
                    if (posts.size > 0) {
                        myPosts = posts
                        // Add data
                        myPostsAdapter.submitList(myPosts)

                        recyclerView.adapter = myPostsAdapter
                    }
                }

                override fun onError(error: String) {
                    Log.d("ERROR", "No posts to show...")
                }
            })
        } catch (e: Exception) {
            Log.d("ERRORS", e.message!!)
            Toast.makeText(requireContext(), "Cannot fetch posts: " + e.message, Toast.LENGTH_LONG).show()
        }

        // Tab layout
        val tabLayout: TabLayout = binding.tabLayout

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    // When "my recipes" is selected
                    0 -> {
                        if(myPosts.size > 0) {
                            recyclerView.adapter = myPostsAdapter
                        }
                    }
                    // When "saved recipes" is selected
                    1 ->  {
                        if (savedPosts.size > 0) {
                            // TODO: Fetch saved posts
                        }
                        recyclerView.adapter = savedPostsAdapter
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        return binding.root
    }

    private fun setLoggedInUser() {
        sharedPrefs = this.requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        user = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)
    }

    // When a post is clicked
    private fun adapterOnClick(post: BePost) {
        val intent = Intent(requireContext(), PostDetailActivity()::class.java)
        intent.putExtra(POST_ID, post.id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}