package mciekurs.com.wally.mainImages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_main.view.*
import mciekurs.com.wally.R
import mciekurs.com.wally.auth.LoginActivity
import mciekurs.com.wally.userImages.UserImages
import mciekurs.com.wally.userImages.UserImagesActivity
import mciekurs.com.wally.userProfile.ProfileActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var list: ArrayList<UserImages>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var adapter: MainImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        getUserImages()

        //uzstāda toolbar
        setSupportActionBar(toolbar_main)
        navigationView_main.setNavigationItemSelectedListener(this)

        val header = navigationView_main.getHeaderView(0)
        header.textView_name_drawer.text = mAuth.currentUser?.displayName
        header.textView_email_drawer.text = mAuth.currentUser?.email

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)
        }

    }

    private fun getUserImages(){
        databaseRef.child("users").child(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
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

    private fun showUserImages(list: ArrayList<UserImages>){
        adapter = MainImageAdapter(list)
        val manager = LinearLayoutManager(this)
        recyclerView_main.layoutManager = manager
        recyclerView_main.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null){
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                drawerLayout_main.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item_logout -> {
                drawerLayout_main.closeDrawer(GravityCompat.START)
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
                mAuth.signOut()
                true
            }
            R.id.item_userImages -> {
                drawerLayout_main.closeDrawer(GravityCompat.START)
                startActivity(Intent(applicationContext, UserImagesActivity::class.java))
                true
            }
            R.id.item_profile -> {
                drawerLayout_main.closeDrawer(GravityCompat.START)
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                true
            }
            else -> return true
        }
    }



}
