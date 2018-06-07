package mciekurs.com.wally

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Window
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.dialog_image_upload.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserImagesActivity : AppCompatActivity() {

    private val REQUEST_TAKE_PHOTO = 1
    private lateinit var currentFilePath: String
    private lateinit var storageRootRef: StorageReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_images)

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        mAuth = FirebaseAuth.getInstance()
        storageRootRef = FirebaseStorage.getInstance().reference
                .child("images/${mAuth.currentUser?.uid}/$timeStamp")



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user_images, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.item_takePicture -> {
                takePicture()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO){
            //uploadImage()
            showDialog(Uri.fromFile(File(currentFilePath)))
        }

    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val fileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(fileName, ".jpg", storageDir)
        currentFilePath = image.absolutePath

        return image
    }

    private fun takePicture(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null){
           var file: File? = null
            try {
                file = createImageFile()
            } catch (ex: IOException){
                //error
            }

            if(file != null){
                val photoUri = FileProvider.getUriForFile(this, "mciekurs.com.wally", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            }


        }



    }

    private fun getImageFromStorage(){
        val intent = Intent()
        intent.type = "image/*"
    }

    private fun uploadImage(){
        val uri = Uri.fromFile(File(currentFilePath))
        storageRootRef.putFile(uri).addOnSuccessListener {
            toast("file uploaded")
        }.addOnFailureListener{
            toast(it.message.toString())
        }
    }

    private fun showDialog(uri: Uri){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_image_upload)
        //dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        dialog.imageView_image_upload.setImageURI(uri)

        dialog.ok.setOnClickListener {
            uploadImage()
            dialog.dismiss()
        }
        dialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

}
