package dk.easv.chefhub.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import dk.easv.chefhub.R
import dk.easv.chefhub.data.callbacks.ICallbackUser
import dk.easv.chefhub.data.callbacks.ICallbackUsers
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.data.repositories.UserRepository
import dk.easv.chefhub.databinding.FragmentSearchBinding
import dk.easv.chefhub.models.BeUser
import dk.easv.chefhub.models.LoggedInUser
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var user: LoggedInUser
    private lateinit var postsRepo: PostRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()

    private lateinit var userRepository: UserRepository

    private lateinit var searchRecyclerViewAdapter: SearchRecyclerViewAdapter
    private lateinit var searchBar: EditText

    private var timer: Timer = Timer()
    private val DELAY: Long = 500 // in ms

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setLoggedInUser()
        userRepository = UserRepository(user.token)
        searchBar = _binding!!.etSearchUser

        searchRecyclerViewAdapter = SearchRecyclerViewAdapter { user -> followUser(user) }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                if (timer != null) timer.cancel()
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    updateAdapter(ArrayList())
                } else {
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            userRepository.searchUser(object : ICallbackUsers {
                                override fun onUsersReady(users: ArrayList<BeUser>) {
                                    updateAdapter(users)
                                }

                                override fun onError(error: String) {
                                    Log.d("ERRORS", "Could not fetch users: $error")
                                }
                            }, s.toString())
                        }
                    }, DELAY)
                }
            }
        })

        return binding.root
    }

    private fun updateAdapter(users: ArrayList<BeUser>) {
        activity?.runOnUiThread {
            val rv = _binding?.rvSearchUser
            searchRecyclerViewAdapter.submitList(users)

            rv?.adapter = searchRecyclerViewAdapter
        }
    }

    private fun followUser(userToFollow: BeUser){
        userRepository.followUser(object: ICallbackUser {
            override fun onUserReady(user: BeUser) {
                Toast.makeText(context, "Now following user: ${user.username}", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: String) {
                Toast.makeText(context, "Error following user: $error", Toast.LENGTH_LONG).show()
            }

        }, userToFollow.username)
    }

    private fun setLoggedInUser() {
        // Get user from shared preferences (local storage)
        sharedPrefs = this.requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        user = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)

        postsRepo = PostRepository(user.token)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}