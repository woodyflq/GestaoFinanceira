package com.example.gestofinanceira.adapter

import android.view.View

interface ItemListener {

    fun onClick(v: View, position: Int)

    fun onLongClick(v: View, position: Int)

}