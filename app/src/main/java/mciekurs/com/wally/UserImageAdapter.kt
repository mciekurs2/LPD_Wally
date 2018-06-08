package mciekurs.com.wally

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_image_edit.*
import kotlinx.android.synthetic.main.single_layout_images.view.*
import org.jetbrains.anko.toast

class UserImageAdapter(private val images: ArrayList<UserImages>): RecyclerView.Adapter<UserImageAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_layout_images, parent, false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val images = images[position]

        val options = RequestOptions()
        options.fitCenter().centerCrop()

        holder.view.textView_title.text = images.title
        holder.view.textView_bio.text = images.bio
        Glide.with(holder.view.context)
                .load(images.image_url).into(holder.view.imageView_single_layout)

        holder.view.setOnClickListener {
            //UserImagesActivity().showEdit(images.image_url)
            showEdit(images.image_url, images.id, holder.view.context)
        }
    }

    class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view)

    private fun showEdit(link: String, id: String, context: Context){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_image_edit)
        Glide.with(context).load(link).into(dialog.imageView_edit)

        dialog.remove_edit.setOnClickListener {
            removeValue(id, context)
            dialog.dismiss()
        }
        dialog.cancel_edit.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun removeValue(id: String, context: Context){
        val ref = FirebaseDatabase.getInstance().reference
        val mAuth = FirebaseAuth.getInstance()
        ref.child("users").child(mAuth.currentUser?.uid!!).child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                context.toast("Image removed")
            } else {
                context.toast("Error happened")
            }
        }
    }

}