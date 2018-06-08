package mciekurs.com.wally.userProfile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_profile.*
import mciekurs.com.wally.mainImages.MainActivity
import mciekurs.com.wally.R
import org.jetbrains.anko.toast

class ProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()

        editText_nickname.setText(mAuth.currentUser?.displayName)
        editText_email_profile.setText(mAuth.currentUser?.email)

        button_back.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        button_update.setOnClickListener {
            updateUser()
        }
    }

    private fun updateUser(){

        val displayName = editText_nickname.text.toString()
        val email = editText_email_profile.text.toString()
        val password = editText_password_profile.text.toString()

        if (!displayName.isEmpty() && displayName.length > 2){
            val profileUpdate = UserProfileChangeRequest
                    .Builder().setDisplayName(displayName).build()
            mAuth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                if (it.isSuccessful){
                    toast("Updated")
                }
            }
        }

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mAuth.currentUser?.updateEmail(email)?.addOnCompleteListener {
                if (it.isSuccessful){
                    toast("Updated")
                }
            }
        }

        if (!password.isEmpty() && password.length > 5){
            mAuth.currentUser?.updatePassword(password)?.addOnCompleteListener {
                if (it.isSuccessful){
                    toast("Updated")
                }
            }
        }
    }


}
