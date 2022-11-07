package com.example.gestofinanceira.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.gestofinanceira.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLoginEmail: EditText
    private lateinit var mLoginSenha: EditText
    private lateinit var mLoginSignIn: Button
    private lateinit var mRegister: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        mLoginEmail = findViewById(R.id.login_edittext_email)
        mLoginSenha = findViewById(R.id.login_edittext_senha)

        mLoginSignIn = findViewById(R.id.login_button_signin)
        mLoginSignIn.setOnClickListener(this)

        mRegister = findViewById(R.id.login_textview_register)
        mRegister.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.login_textview_register -> {
                val it = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(it)
            }

            R.id.login_button_signin -> {

                val email = mLoginEmail.text.toString()
                val senha = mLoginSenha.text.toString()

                var isLoginFormFilled = true

                if(email.isEmpty()) {
                    mLoginEmail.error = "Este campo deve ser preenchido!"
                    isLoginFormFilled = false
                }

                if(senha.isEmpty()) {
                    mLoginSenha.error = "Este campo deve ser preenchido!"
                    isLoginFormFilled = false
                }

                if(isLoginFormFilled) {

                    val dialog = ProgressDialog(LoginActivity@this)
                    dialog.setTitle("Aguarde...")
                    dialog.setMessage("Efetuando login")
                    dialog.isIndeterminate = true
                    dialog.show()

                    mAuth.signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener{
                            dialog.dismiss()
                            if(it.isSuccessful){
                                val it = Intent(applicationContext, MainActivity::class.java)
                                startActivity(it)
                                finish()
                            } else {
                                showMessage()
                            }
                        }
                }
            }
        }
    }

    private fun showMessage(){
        val handler = Handler(Looper.getMainLooper())
        handler.post{
            Toast.makeText(applicationContext,
                "Usuário ou senha inválidos!",
                Toast.LENGTH_SHORT).show()
        }
    }
}