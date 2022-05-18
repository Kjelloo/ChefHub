package dk.easv.chefhub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.easv.chefhub.databinding.ActivityCreatePostBinding

const val IMAGE_TAG = "post image"
class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.ibCamera.setOnClickListener { openCameraActivity() }
        binding.ibGallery.setOnClickListener { openGallery() }

    }

    private fun openGallery() {
        // TODO: Add gallery
    }

    private fun openCameraActivity() {
        val intent = Intent(this, CameraActivity()::class.java)
        startActivity(intent)
    }

}