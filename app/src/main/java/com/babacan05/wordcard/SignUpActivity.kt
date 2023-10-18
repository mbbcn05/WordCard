package com.babacan05.wordcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.babacan05.wordcard.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.babacan05.wordcard.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.imgCreateAccount.setOnClickListener {
            val email = binding.edtUserEmil.text.toString()
            val pass1 = binding.edtUserPass1.text.toString()
            val pass2 = binding.edtUserPass2.text.toString()

            if (email.equals("") || pass1.equals("") || pass2.equals("")) {
                Toast.makeText(applicationContext, "Please enter data", Toast.LENGTH_SHORT).show()
            } else if (!pass1.equals(pass2)) {
                Toast.makeText(applicationContext, "Passwords are not the same", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(applicationContext,"Verify your e-mail account",Toast.LENGTH_SHORT).show()
                                val intent = Intent(applicationContext, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    if (exception != null) {
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.txtGoToLoginScreen.setOnClickListener {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}