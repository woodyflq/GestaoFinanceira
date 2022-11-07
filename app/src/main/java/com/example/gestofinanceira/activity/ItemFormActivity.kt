package com.example.gestofinanceira.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.*
import com.example.gestofinanceira.R
import com.example.gestofinanceira.model.items
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ItemFormActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mItemFormTitle: TextView
    private lateinit var mItemFormName: EditText
    private lateinit var mItemFormValor: EditText
    private lateinit var mItemFormIsReceita: RadioButton
    private lateinit var mItemFormIsDespesa: RadioButton
    private lateinit var mItemFormAdicionar: Button

    var countReceita = 0
    var countDespesa = 0

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    private val handler = Handler(Looper.getMainLooper())

    private var mItemId = ""
    var tipo = "Receita"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_form)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mItemFormTitle = findViewById(R.id.item_add_textview_form_name)
        mItemFormName = findViewById(R.id.item_add_edittext_name)
        mItemFormValor = findViewById(R.id.item_add_edittext_valor)
        mItemFormIsReceita = findViewById(R.id.item_add_radiobutton_receita)
        mItemFormIsDespesa = findViewById(R.id.item_add_radiobutton_despesa)

        mItemFormAdicionar = findViewById(R.id.item_add_button_save)
        mItemFormAdicionar.setOnClickListener(this)

        mItemId = intent.getStringExtra("itemId") ?: ""

        val query = mDatabase.reference.child("users/${mAuth.uid}/ReceitasEDespesas").orderByKey()
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach() {
                    val item = it.getValue(items::class.java)
                    if (item?.tipo == "Receita"){
                        countReceita+=1
                    }
                }
                snapshot.children.forEach {
                    val item = it.getValue(items::class.java)
                    if (item?.tipo == "Despesa"){
                        countDespesa+=1
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        if(mItemId.isNotEmpty()) {

            val query = mDatabase.reference.child("users/${mAuth.uid}/ReceitasEDespesas/${mItemId}").orderByKey()
            query.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val item = snapshot.getValue(items::class.java)

                    handler.post{
                        mItemFormTitle.text = "Editar Receita ou Despesa"
                        mItemFormName.text = Editable.Factory.getInstance().newEditable(item?.name)
                        mItemFormValor.text = Editable.Factory.getInstance().newEditable(item?.valor)

                        if (item?.tipo == "Receita"){
                            mItemFormIsReceita.isChecked = true
                            mItemFormIsDespesa.isChecked = false
                            tipo = "Receita"
                        } else {
                            mItemFormIsReceita.isChecked = false
                            mItemFormIsDespesa.isChecked = true
                            tipo = "Despesa"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.item_add_button_save -> {

                val name = mItemFormName.text.toString()
                val valor = mItemFormValor.text.toString()

                if(name.isEmpty()) {
                    mItemFormName.error = "Este campo não pode estar vazio"
                    return
                }

//                if(mItemFormIsReceita.isChecked){
//                    if(countReceita > 4){
//                        mItemFormName.error = "O número maximo de Receitas foi atingido, apague ou edite uma receita existente e tente novamente"
//                        return
//                    }
//                }
//
//                if(mItemFormIsDespesa.isChecked){
//                    if(countDespesa > 4){
//                        mItemFormName.error = "O número maximo de Despesa foi atingido, apague ou edite uma receita existente e tente novamente"
//                        return
//                    }
//                }



                if(mItemId.isEmpty()) {
                    val itemId = mDatabase.reference.child("users/${mAuth.uid}/ReceitasEDespesas").push().key
                    val ref = mDatabase.getReference("users/${mAuth.uid}/ReceitasEDespesas/${itemId}")

                    val item = items(itemId!!, tipo, name, valor)
                    ref.setValue(item)
                    finish()
                } else {
                    val ref = mDatabase.getReference("users/${mAuth.uid}/ReceitasEDespesas/${mItemId}")
                    val item = items(mItemId, tipo, name, valor)

                    ref.setValue(item)
                    finish()
                }
            }
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.item_add_radiobutton_receita ->
                    if (checked) {
                        tipo = "Receita"
                    }
                R.id.item_add_radiobutton_despesa ->
                    if (checked) {
                        tipo = "Despesa"
                    }
            }
        }
    }
}