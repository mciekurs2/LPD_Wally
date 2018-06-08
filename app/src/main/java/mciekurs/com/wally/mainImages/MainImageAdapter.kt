package mciekurs.com.wally.mainImages

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.single_layout_main.view.*
import mciekurs.com.wally.R
import mciekurs.com.wally.userImages.UserImages

class MainImageAdapter(private val images: ArrayList<UserImages>): RecyclerView.Adapter<MainViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_layout_main, parent, false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val images = images[position]

        holder.view.textView_title_main.text = images.title
        Glide.with(holder.view.context).load(images.image_url).into(holder.view.imageView_main)
        holder.view.textView_bio_main.text = images.bio
    }

}

class MainViewHolder(val view: View): RecyclerView.ViewHolder(view)