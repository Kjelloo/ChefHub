package dk.easv.chefhub.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dk.easv.chefhub.R
import dk.easv.chefhub.models.BePost

class ProfileRecyclerViewAdapter(private val onClick: (BePost) -> Unit):
    ListAdapter<BePost, ProfileRecyclerViewAdapter.PostViewHolder>(PostDiffCallback) {

    class PostViewHolder(itemView: View, val onClick: (BePost) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val postImageView: ImageView = itemView.findViewById(R.id.ivRecyclerPost)
        private var currentPost: BePost? = null

        init {
            itemView.setOnClickListener {
                currentPost?.let {
                    onClick(it)
                }
            }
        }

        fun bind(post: BePost) {
            currentPost = post

            // Load image
            Picasso
                .get()
                .load(post.photoUrl)
                .placeholder(R.drawable.person)
                .fit()
                .centerCrop()
                .into(postImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_cell, parent, false)
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