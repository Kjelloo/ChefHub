package dk.easv.chefhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dk.easv.chefhub.data.entities.CreatePostDto
import dk.easv.chefhub.data.repositories.PostRepository
import dk.easv.chefhub.databinding.ActivityCreatePostBinding
import dk.easv.chefhub.models.LoggedInUser
import java.io.File

class CreatePostActivity : AppCompatActivity() {
    private var imageUri: Uri = Uri.EMPTY
    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var postRepository: PostRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()
    private lateinit var user: LoggedInUser

    private val imgResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            imageUri = intent?.data!!

            // Load image into preview
            Picasso
                .get()
                .load(imageUri)
                .rotate(90F)
                .fit()
                .into(binding.imagePreview)

//            val msg = "Photo capture succeeded: ${intent?.data}"
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setLoggedInUser()

        postRepository = PostRepository(user.token)

        binding.ibCamera.setOnClickListener { openCameraActivity() }
        binding.ibGallery.setOnClickListener { openGallery() }
        binding.btnCreatePost.setOnClickListener { createPost() }
    }

    private fun createPost() {
        val title = binding.postTitleNew.text.toString()
        val desc = binding.postDescriptionNew.text.toString()

        val cursor: Cursor? = this.contentResolver.query(
            imageUri,
            arrayOf(MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )
        cursor?.moveToFirst()
        val path = cursor?.getString(0)
        cursor?.close()

        val file = File(path)

        Log.d("ERRORS", user.username)
        Log.d("ERRORS", path.toString())
        Log.d("ERRORS", file.exists().toString())

        val postDto = CreatePostDto(title, desc)

        if (title.isNotEmpty() && desc.isNotEmpty() && imageUri != Uri.EMPTY) {
            postRepository.createPost(postDto, file, user.username)
        } else if (title.isEmpty() && desc.isEmpty()){
            Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_LONG).show()
        } else if (imageUri == Uri.EMPTY) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show()
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(Environment.DIRECTORY_PICTURES, "ChefHub-Image")

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    private fun getContentFileName(uri: Uri): String? = runCatching {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
        }
    }.getOrNull()

    private fun openGallery() {
        // TODO: Add gallery
    }

    private fun openCameraActivity() {
        val intent = Intent(this, CameraActivity()::class.java)
        imgResult.launch(intent)
    }

    private fun setLoggedInUser() {
        // Get user from shared preferences (local storage)
        sharedPrefs = this
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor = sharedPrefs.edit()
        user = gson.fromJson(sharedPrefs.getString("user", ""), LoggedInUser::class.java)
    }
}