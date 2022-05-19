package dk.easv.chefhub

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import dk.easv.chefhub.databinding.ActivityCreatePostBinding

const val IMAGE_TAG = "post image"
class CreatePostActivity : AppCompatActivity() {

    private val imgResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data;

            val msg = "Photo capture succeeded: ${intent?.data}"
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

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
        imgResult.launch(intent)
    }

}