package com.babacan05.wordcard
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.babacan05.wordcard.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor
    private var durum: Boolean = false
    private var exitToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        GET = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        SET = GET.edit()

        var remember = GET.getBoolean("bool_key", false)
        if (remember) {
            binding.swRememberMe.isChecked = true
            var save_email = GET.getString("sp_email", "")
            var save_pass = GET.getString("sp_pass", "")
            binding.edtUserEmail.setText(save_email)
            binding.edtUserPassword.setText(save_pass)
        } else {
            binding.swRememberMe.isChecked = false
            binding.edtUserEmail.setText("")
            binding.edtUserPassword.setText("")
        }

        binding.imgLogin.setOnClickListener {
            var email = binding.edtUserEmail.text.toString()
            var pass = binding.edtUserPassword.text.toString()

            if (email.equals("") && pass.equals("")) {
                Toast.makeText(applicationContext, "Please enter data", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified!!) {
                            Toast.makeText(applicationContext,"Welcome " + auth.currentUser?.email.toString(),Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext,"Your account not verify",Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext,exception.localizedMessage.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.txtCreateAccountScreen.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.txtGoToResetPassScreen.setOnClickListener {
            val intent = Intent(this, ForgotPassActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.swRememberMe.setOnClickListener {
            durum = binding.swRememberMe.isChecked

            if (durum) {
                SET.putBoolean("bool_key", true)
                SET.putString("sp_email", binding.edtUserEmail.text.toString())
                SET.putString("sp_pass", binding.edtUserPassword.text.toString())
                SET.commit()
            } else {
                SET.putBoolean("bool_key", false)
                SET.putString("sp_email", "")
                SET.commit()
            }
        }
    }

    override fun onBackPressed() {
        if (exitToast == null || exitToast!!.view == null || exitToast!!.view?.windowToken == null) {
            exitToast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_LONG)
            exitToast!!.show()

        } else {
            exitToast!!.cancel()

            super.onBackPressed()
        }
    }

}