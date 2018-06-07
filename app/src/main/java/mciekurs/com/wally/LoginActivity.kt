package mciekurs.com.wally

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        button_login.setOnClickListener {
            loginIn()
        }

        textView_login_goToRegister.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        if(mAuth?.currentUser != null){
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun loginIn(){
        val email = editText_login_email.text.toString()
        val password = editText_login_password.text.toString()

        if (email.isEmpty()){
            editText_login_email.error = "Email is needed"
            editText_login_email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText_login_email.error = "Enter a valid email"
            editText_login_email.requestFocus()
            return
        }

        if (password.isEmpty()){
            editText_login_password.error = "Password is needed"
            editText_login_password.requestFocus()
            return
        }

        if (password.length < 6){
            editText_login_password.error = "Minimum length of password is 6"
            editText_login_password.requestFocus()
            return
        }

        mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful){
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                toast(it.exception?.message.toString())
            }

        }

    }


}
