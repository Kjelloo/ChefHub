package dk.easv.chefhub.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dk.easv.chefhub.R
import dk.easv.chefhub.models.BeUser

class SearchRecyclerViewAdapter(private val onClick: (BeUser) -> Unit):
    ListAdapter<BeUser, SearchRecyclerViewAdapter.UserViewHolder>(UserDiffCallback) {

    class UserViewHolder(itemView: View, val onClick: (BeUser) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val userProfileImage: ImageView = itemView.findViewById(R.id.ivRvProfilePicture)
        private val username: TextView = itemView.findViewById(R.id.tvRvAuthor)
        private val followButton: Button = itemView.findViewById(R.id.btnFollow)

        private var currentUser: BeUser? = null

        init {
            followButton.setOnClickListener {
                currentUser?.let {
                    onClick(it)
                }
            }
        }

        fun bind(user: BeUser) {
            currentUser = user

            username.text = user.username

            // Load user profile image
            Picasso
                .get()
                .load(user.photoUrl)
                .error(R.drawable.person)
                .fit()
                .centerCrop()
                .into(userProfileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_search_cell, parent, false)
        return UserViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

object UserDiffCallback : DiffUtil.ItemCallback<BeUser>() {
    override fun areItemsTheSame(oldItem: BeUser, newItem: BeUser): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BeUser, newItem: BeUser): Boolean {
        return oldItem.id == newItem.id
    }
}