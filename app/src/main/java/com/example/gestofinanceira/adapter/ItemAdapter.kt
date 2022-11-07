package com.example.gestofinanceira.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestofinanceira.R
import com.example.gestofinanceira.model.items

class ItemAdapter(val items: List<items>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var listener :ItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.itemTipo.text = items[position].tipo
        holder.itemName.text = items[position].name
        holder.itemValor.text = items[position].valor.toString()

        if(items[position].tipo == "Receita"){
            holder.itemColor.setBackgroundColor(Color.rgb(80, 80,255))
        }
        if(items[position].tipo == "Despesa"){
            holder.itemColor.setBackgroundColor(Color.rgb(255, 80,80))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItemListener(listener: ItemListener?){
        this.listener = listener
    }

    class ItemViewHolder(itemView: View, listener: ItemListener?): RecyclerView.ViewHolder(itemView){
        val itemTipo:TextView = itemView.findViewById(R.id.item_textview_tipo)
        val itemName:TextView = itemView.findViewById(R.id.item_textview_nome)
        val itemValor:TextView = itemView.findViewById(R.id.item_textview_valor)
        val itemColor:View = itemView.findViewById(R.id.item_view_tipo_color)

        init {
            itemView.setOnClickListener{
                listener?.onClick(it, adapterPosition)
            }

            itemView.setOnLongClickListener{
                listener?.onLongClick(it, adapterPosition)
                true
            }
        }
    }
}