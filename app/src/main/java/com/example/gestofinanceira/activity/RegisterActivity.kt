package com.example.gestofinanceira.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.gestofinanceira.R
import com.example.gestofinanceira.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.UnknownServiceException

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mFirstName: EditText
    private lateinit var mLastName: EditText
    private lateinit var mTelefone: EditText
    private lateinit var mEmail: EditText
    private lateinit var mSenha: EditText
    private lateinit var mRepetirSenha: EditText
    private lateinit var mSignUp: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mFirstName = findViewById(R.id.register_edittext_firstname)
        mLastName = findViewById(R.id.register_edittext_lastname)
        mTelefone = findViewById(R.id.register_edittext_telefone)
        mEmail = findViewById(R.id.register_edittext_email)
        mSenha = findViewById(R.id.register_edittext_senha)
        mRepetirSenha = findViewById(R.id.register_edittext_repetirsenha)

        mSignUp = findViewById(R.id.register_button_signup)
        mSignUp.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.register_button_signup -> {

                val firstName = mFirstName.text.toString()
                val lastName = mLastName.text.toString()
                val telefone = mTelefone.text.toString()
                val email = mEmail.text.toString()
                val senha = mSenha.text.toString()
                val repetirSenha = mRepetirSenha.text.toString()

                var isFormFilled = true

                if(firstName.isEmpty()){
                    mFirstName.error = "Este campo deve ser preenchido"
                    isFormFilled = false
                }
                if(lastName.isEmpty()){
                    mLastName.error = "Este campo deve ser preenchido"
                    isFormFilled = false
                }
                if(telefone.isEmpty()){
                    mTelefone.error = "Este campo deve ser preenchido"
                    isFormFilled = false
                }
                if(email.isEmpty()){
                    mEmail.error = "Este campo deve ser preenchido"
                    isFormFilled = false
                }
                if(senha.isEmpty()){
                    mSenha.error = "Este campo deve ser preenchido"
                    isFormFilled = false
                }
                if(repetirSenha.isEmpty()){
                    mRepetirSenha.error = "Este campo deve ser preenchido"
                    isFormFilled = false
                }

                if(isFormFilled) {

                    if(senha != repetirSenha){
                        mRepetirSenha.error = "As senhas devem ser iguais"
                        return
                    }

                    val dialog = ProgressDialog(this)
                    dialog.setTitle("Aguarde...")
                    dialog.setMessage("Registrando suas informações no bando de dados")
                    dialog.isIndeterminate = true
                    dialog.show()

                    mAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener {
                            dialog.dismiss()
                            val handler = Handler(Looper.getMainLooper())
                            if(it.isSuccessful) {

                                val user = User(firstName, lastName, telefone, email)

                                val ref = mDatabase.getReference("users/${mAuth.uid!!}")
                                ref.setValue(user)

                                handler.post{
                                Toast.makeText(applicationContext,
                                    "Usuário cadastrado com sucesso!",
                                    Toast.LENGTH_SHORT)
                                    .show()
                                    finish()
                                }

                            } else {
                                handler.post{
                                    Toast.makeText(applicationContext,
                                        it.exception?.message,
                                        Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                }
            }
        }
    }
}