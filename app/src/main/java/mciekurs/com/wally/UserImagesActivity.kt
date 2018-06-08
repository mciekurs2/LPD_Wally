package mciekurs.com.wally

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_user_images.*
import kotlinx.android.synthetic.main.dialog_image_edit.*
import kotlinx.android.synthetic.main.dialog_image_upload.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserImagesActivity : AppCompatActivity() {

    private val REQUEST_TAKE_PHOTO = 1
    private val REQUEST_GET_IMAGE = 2
    private var byCamera = true
    private lateinit var storageUri: Uri
    private lateinit var currentFilePath: String
    private lateinit var storageRootRef: StorageReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var downloadUrl: String
    private lateinit var title: String
    private lateinit var bio: String
    private lateinit var timeStamp: String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var adapter: UserImageAdapter
    private lateinit var list: ArrayList<UserImages>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_images)

        mAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        getUserImages()

    }

    private fun addImageToDB(){
        val image = UserImages(timeStamp, downloadUrl, title, bio)
        databaseRef.child("users").child(mAuth.currentUser?.uid!!).child(timeStamp).setValue(image)
    }

    private fun showUserImages(list: ArrayList<UserImages>){
        adapter = UserImageAdapter(list)
        val manager = LinearLayoutManager(this)
        recyclerView_userImages.layoutManager = manager
        recyclerView_userImages.adapter = adapter

    }

    private fun getUserImages(){
        databaseRef.child("users").child(mAuth.currentUser?.uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(ds: DataSnapshot) {
                list = ArrayList()
                for (value in ds.children){
                    val images = value.getValue(UserImages::class.java)
                    list.add(images!!)
                }
                showUserImages(list)
            }
            override fun onCancelled(de: DatabaseError) {}
        })
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
            R.id.item_getPicture -> {
                getImageFromStorage()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO && data != null){
            //uploadImage()
            byCamera = true
            showDialog(Uri.fromFile(File(currentFilePath)))
        }

        if (requestCode == REQUEST_GET_IMAGE && data != null){
            byCamera = false
            storageUri = data.data
            showDialog(storageUri)
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
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_GET_IMAGE)
    }

    private fun uploadImage(byCamera: Boolean){
        val uri: Uri = if (byCamera){
            Uri.fromFile(File(currentFilePath))
        } else { storageUri }
        storageRootRef.putFile(uri).addOnSuccessListener {
            storageRootRef.downloadUrl.addOnSuccessListener {
                downloadUrl = it.toString()
                addImageToDB()
            }
            toast("file uploaded")
        }.addOnFailureListener{
            toast(it.message.toString())
        }
    }

    private fun showDialog(uri: Uri){
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        storageRootRef = FirebaseStorage.getInstance().reference
                .child("images/${mAuth.currentUser?.uid}/$timeStamp")

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_image_upload)
        dialog.imageView_image_upload.setImageURI(uri)

        dialog.ok.setOnClickListener {
            uploadImage(byCamera)
            title = dialog.editText_title.text.toString()
            bio = dialog.editText_bio.text.toString()
            dialog.dismiss()
        }
        dialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}
