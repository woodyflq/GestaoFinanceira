package com.example.gestofinanceira.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestofinanceira.R
import com.example.gestofinanceira.adapter.ItemAdapter
import com.example.gestofinanceira.adapter.ItemListener
import com.example.gestofinanceira.model.items
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), View.OnClickListener, ItemListener {

    private lateinit var mItemRecycleView: RecyclerView
    private lateinit var mItemAdd: FloatingActionButton
    private lateinit var itemAdapter: ItemAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var mItemList = mutableListOf<items>()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    var countDespesa = 0
    var countReceita = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mItemRecycleView = findViewById(R.id.main_recycleview)

        mItemAdd = findViewById(R.id.main_floatingbutton_add_item)
        mItemAdd.setOnClickListener(this)

//////////////////////////////////////////////////////

////////////////////////////////////////////////////
    }

    override fun onStart() {
        super.onStart()


/////////////////////////////////////////////
        val query1 = mDatabase.reference.child("users/${mAuth.uid}/ReceitasEDespesas").orderByKey()
        query1.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                countDespesa = 0
                countReceita = 0

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
//////////////////////////////////////////////

        val dialog = ProgressDialog(LoginActivity@this)
        dialog.setTitle("Aguarde...")
        dialog.setMessage("Recuperando bando de dados")
        dialog.isIndeterminate = true
        dialog.show()

        val query = mDatabase.reference.child("users/${mAuth.uid}/ReceitasEDespesas").orderByKey()
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                mItemList.clear()

                snapshot.children.forEach {
                    val item = it.getValue(items::class.java)
                    if (item?.tipo == "Receita"){
                        if(countReceita in 0..5){
                            mItemList.add(item!!)
                        }
                        countReceita-=1
                    }
                }

                snapshot.children.forEach {
                    val item = it.getValue(items::class.java)
                    if (item?.tipo == "Despesa"){
                        if(countDespesa in 0..5){
                            mItemList.add(item!!)
                        }
                        countDespesa-=1
                    }
                }


                handler.post {
                    dialog.dismiss()
                    itemAdapter = ItemAdapter(mItemList)
                    itemAdapter.setItemListener(this@MainActivity)
                    val llm = LinearLayoutManager(applicationContext)

                    mItemRecycleView.apply {
                        adapter = itemAdapter
                        layoutManager = llm
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.main_floatingbutton_add_item -> {
                val it = Intent(applicationContext, ItemFormActivity::class.java)
                startActivity(it)
            }
        }
    }


//removi essa opção pois não é pedido e atrapalha na ordem de disposição dos itens, pois da forma que eu fiz não é por tempo, e sim por
//sequencia, logo se fosse editado, apareceria no lugar que tinha sido criado antigamente
    override fun onClick(v: View, position: Int) {
//        val it = Intent(applicationContext, ItemFormActivity::class.java)
//        it.putExtra("itemId", mItemList[position].id)
//        startActivity(it)
    }

    override fun onLongClick(v: View, position: Int) {
        val dialog = AlertDialog.Builder(this).setTitle("Gestao Financeira")
            .setMessage("Você deseja excluir este item ${mItemList[position].name}?")
            .setPositiveButton("Sim") {dialog, _ ->

                val ref = mDatabase.getReference("users/${mAuth.uid}/ReceitasEDespesas/${mItemList[position].id}")
                ref.removeValue().addOnCompleteListener {
                    if(it.isSuccessful) {
                        handler.post {
                            dialog.dismiss()
                            itemAdapter.notifyItemRemoved(position)
                            recreate()
                        }
                    } else {
                        Toast.makeText(MainActivity@this, "Ocorreu um erro, tente novamente!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Não") {dialog, _ -> dialog.dismiss()}
            .create()
        dialog.show()
    }
}