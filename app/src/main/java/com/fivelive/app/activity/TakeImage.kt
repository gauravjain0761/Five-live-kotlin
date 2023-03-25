package com.fivelive.app.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.fivelive.app.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TakeImage constructor() : AppCompatActivity() {
    var fileProvider: Uri? = null
    var photoFile: File? = null
    var photoFileName: String = "photo.jpg"
    val APP_TAG: String = "TakeImage"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_take_image);
        showCameraPreview()
    }

    private fun showCameraPreview() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already available, start camera preview
            onLaunchCamera()
        } else {
            // Permission is missing and must be requested.
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showCameraDescriptionDialog()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA
            )
        }
    }

    fun showCameraDescriptionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Permission denied")
        builder.setMessage("Without this permission app is unable to use Camera.Are you sure want to denied this permission.")
        builder.setPositiveButton("I'M SURE", object : DialogInterface.OnClickListener {
            public override fun onClick(dialogInterface: DialogInterface, i: Int) {
                ActivityCompat.requestPermissions(
                    this@TakeImage,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
                // goToSettingScreen();
            }
        })
        builder.setNegativeButton("RETRY", object : DialogInterface.OnClickListener {
            public override fun onClick(dialogInterface: DialogInterface, i: Int) {
                ActivityCompat.requestPermissions(
                    this@TakeImage,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
                // goToSettingScreen();
            }
        })
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun goToSettingScreen() {
        val intent: Intent = Intent()
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.setData(uri)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //startActivityForResult(intent,SETTING);
        startActivity(intent)
        //finish();
    }

    public override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                onLaunchCamera()
            } else {
                // Permission request was denied.
                // showCameraDescriptionDialog();
                // goToSettingScreen();
                requestCameraPermission()
            }
        } else {
            showCameraDescriptionDialog()
        }
    }

    fun onLaunchCamera() {

        // create Intent to take a picture and return control to the calling application
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName)
        //  photoFile = getGalleryFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        fileProvider =
            FileProvider.getUriForFile(this@TakeImage, BuildConfig.APPLICATION_ID, photoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir: File =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        // Return the file target for the photo based on filename
        val file: File = File(mediaStorageDir.getPath() + File.separator + timeStamp + fileName)
        return file
    }

    fun getGalleryFileUri(fileName: String?): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val name: String = "5Live_" + timeStamp + ".jpg"
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        // Return the file target for the photo based on filename
        val file: File = File(storageDir.getPath() + File.separator + name)
        return file
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //  Uri uri = data.getData();
                val takenImage: Bitmap = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                //Bitmap rotatedBitmap = AppUtil.getRotatedImage(photoFile.getAbsolutePath(), takenImage);
                //SparkApp.getInstance().imageSavedPath = AppUtil.BitmapToFile(rotatedBitmap, MerchantDetails.this);
                println()
                //  SparkApp.getInstance().rotatedBitmap = rotatedBitmap;
                val intent: Intent = Intent()
                intent.putExtra("photoFile", photoFile!!.getAbsolutePath())
                intent.putExtra("imageUri", fileProvider.toString())
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (requestCode == SETTING) {
            when (resultCode) {
                RESULT_OK -> {
                    Log.d("TAG", "SignUpActivity")
                    onLaunchCamera()
                }
                RESULT_CANCELED -> goToSettingScreen()
            }
        }
    }

    companion object {
        val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: Int = 1034
        private val PERMISSION_REQUEST_CAMERA: Int = 0
        val SETTING: Int = 5
    }
}