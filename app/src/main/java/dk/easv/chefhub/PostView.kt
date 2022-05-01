package dk.easv.chefhub

import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View

class PostView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    lateinit var postPhotoUrl: String
    lateinit var postTitle: String
    lateinit var postDescription: String
    var postId: Int = -1
    var authorId: Int = -1


    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.PostView)

            // Get post photo url from PostView attributes
            if(a.hasValue(R.styleable.PostView_postPhotoUrl)) {
                postPhotoUrl = a.getString(R.styleable.PostView_postPhotoUrl).toString()
            }

            // Get post title from PostView attributes
            if(a.hasValue(R.styleable.PostView_postTitle)) {
                postTitle = a.getString(R.styleable.PostView_postTitle).toString()
            }

            // Get post description from PostView attributes
            if(a.hasValue(R.styleable.PostView_postDescription)) {
                postTitle = a.getString(R.styleable.PostView_postDescription).toString()
            }

            // Get post post id from PostView attributes
            if(a.hasValue(R.styleable.PostView_postId)) {
                postTitle = a.getString(R.styleable.PostView_postId).toString()
            }

            // Get post post author id from PostView attributes
            if(a.hasValue(R.styleable.PostView_postAuthorId)) {
                postTitle = a.getString(R.styleable.PostView_postAuthorId).toString()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        // TODO: Maybe make an async call when fetching posts?

    }
}