package mciekurs.com.wally.auth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_register.*
import mciekurs.com.wally.mainImages.MainActivity
import mciekurs.com.wally.R
import org.jetbrains.anko.toast

class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        button_register.setOnClickListener {
            register()
        }

        textView_register_goToLogin.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

    }

    private fun register(){
        val name = editText_register_displayName.text.toString()
        val email = editText_register_email.text.toString()
        val password = editText_register_password.text.toString()

        if (name.isEmpty()){
            editText_register_displayName.error = "Display name is needed"
            editText_register_displayName.requestFocus()
            return
        }

        if (name.length < 2){
            editText_register_displayName.error = "Minimum length of name is 2"
            editText_register_displayName.requestFocus()
            return
        }

        if (email.isEmpty()){
            editText_register_email.error = "Email is needed"
            editText_register_email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText_register_email.error = "Enter a valid email"
            editText_register_email.requestFocus()
            return
        }

        if (password.isEmpty()){
            editText_register_password.error = "Password is needed"
            editText_register_password.requestFocus()
            return
        }

        if (password.length < 6){
            editText_register_password.error = "Minimum length of password is 6"
            editText_register_password.requestFocus()
            return
        }

        mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful){

                val profileUpdate = UserProfileChangeRequest
                        .Builder().setDisplayName(name).build()
                mAuth!!.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                    if (it.isSuccessful){
                        //do something
                    }
                }

                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                toast(it.exception?.message.toString())
            }
        }


    }

}
