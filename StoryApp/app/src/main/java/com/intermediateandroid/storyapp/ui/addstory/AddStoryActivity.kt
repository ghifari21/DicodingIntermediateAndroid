package com.intermediateandroid.storyapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.intermediateandroid.storyapp.R
import com.intermediateandroid.storyapp.databinding.ActivityAddStoryBinding
import com.intermediateandroid.storyapp.ui.main.MainActivity
import com.intermediateandroid.storyapp.utils.Result
import com.intermediateandroid.storyapp.utils.ViewModelFactory
import com.intermediateandroid.storyapp.utils.createTempFile
import com.intermediateandroid.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var getFile: File? = null
    private var userLocation: Location? = null

    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.ivAddPhoto.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg: Uri = it?.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.ivAddPhoto.setImageURI(selectedImg)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    binding.swLocation.isChecked = false
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_add_story)

        askPermission()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupAction()
    }

    private fun askPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    userLocation = it
                } else {
                    binding.swLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)
            createTempFile(application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@AddStoryActivity,
                    "com.intermediateandroid.storyapp",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }

        binding.swLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLastLocation()
            } else {
                userLocation = null
            }
        }

        binding.btnUpload.setOnClickListener {
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)
                val description = binding.edAddDescription.text
                    .toString()
                    .trim()
                    .toRequestBody("text/plain".toMediaType())
                val lat =
                    userLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
                val lon =
                    userLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                uploadStory(imageMultipart, description, lat, lon)
            } else {
                Toast.makeText(this, getString(R.string.empty_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        viewModel.getAccount().observe(this) {
            if (it.token != "") {
                viewModel.uploadStory(it.token, file, description, lat, lon).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                loadingState(true)
                            }
                            is Result.Success -> {
                                loadingState(false)
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.success))
                                    setMessage(getString(R.string.success_upload))
                                    setPositiveButton(getString(R.string.to_homepage)) { _, _ ->
                                        val intent =
                                            Intent(this@AddStoryActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                            is Result.Error -> {
                                loadingState(false)
                                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun reduceFileImage(file: File): File {
        return file
    }

    private fun loadingState(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                pbLoading.visibility = View.VISIBLE
                tvLoading.visibility = View.VISIBLE
            } else {
                pbLoading.visibility = View.GONE
                tvLoading.visibility = View.GONE
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}