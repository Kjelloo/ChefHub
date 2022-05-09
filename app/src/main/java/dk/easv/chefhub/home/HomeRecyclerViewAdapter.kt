package dk.easv.chefhub.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dk.easv.chefhub.R
import dk.easv.chefhub.models.BePost

class HomeRecyclerViewAdapter(private val onClick: (BePost) -> Unit):
    ListAdapter<BePost, HomeRecyclerViewAdapter.PostViewHolder>(PostDiffCallback) {

    class PostViewHolder(itemView: View, val onClick: (BePost) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.ivProfilePicture)
        private val postAuthor: TextView = itemView.findViewById(R.id.tvPostAuthor)
        private val postImage: ImageView = itemView.findViewById(R.id.ivPostPhoto)
        private val postTitle: TextView = itemView.findViewById(R.id.tvPostTitle)
        private val postDescription: TextView = itemView.findViewById(R.id.tvPostDescription)

        private var currentPost: BePost? = null

        init {
            // TODO: add click listener when pressed on author and heart image
            postImage.setOnClickListener {
                currentPost?.let {
                    onClick(it)
                }
            }
        }

        fun bind(post: BePost) {
            currentPost = post

            // Load profile picture
            Picasso
                .get()
                .load(post.user.photoUrl)
                .placeholder(R.drawable.person)
                .fit()
                .into(profileImage)

            // Load post
            Picasso
                .get()
                .load(post.photoUrl)
                .error(R.drawable.person)
                .fit()
                .centerCrop()
                .into(postImage)

            // Set post author
            postAuthor.text = post.user.username

            // Set post title
            postTitle.text = post.title

            // Set post description
            postDescription.text = post.desc
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_cell, parent, false)
        return PostViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<BePost>() {
    override fun areItemsTheSame(oldItem: BePost, newItem: BePost): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BePost, newItem: BePost): Boolean {
        return oldItem.id == newItem.id
    }
}