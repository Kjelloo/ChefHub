package dk.easv.chefhub.postdetail

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
import dk.easv.chefhub.models.BeComment

class PostDetailRecyclerViewAdapter :
    ListAdapter<BeComment, PostDetailRecyclerViewAdapter.PostViewHolder>(CommentDiffCallback) {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentAuthorPicture: ImageView = itemView.findViewById(R.id.ivRvProfilePicture)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tvRvAuthor)
        private val tvComment: TextView = itemView.findViewById(R.id.tvRvComment)

        private var currentComment: BeComment? = null

        fun bind(comment: BeComment) {
            currentComment = comment

            tvAuthor.text = comment.user.username
            tvComment.text = comment.msg

            // Load profile image
            Picasso
                .get()
                .load(comment.user.photoUrl)
                .error(R.drawable.person)
                .fit()
                .centerCrop()
                .into(commentAuthorPicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_detail_cell, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val beComment = getItem(position)
        holder.bind(beComment)
    }

}

object CommentDiffCallback : DiffUtil.ItemCallback<BeComment>() {
    override fun areItemsTheSame(oldItem: BeComment, newItem: BeComment): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: BeComment, newItem: BeComment): Boolean {
        return oldItem.id == newItem.id
    }
}