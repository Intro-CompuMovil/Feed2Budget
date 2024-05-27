package com.compumovil.feed2budget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ItemAdapter(context: Context, private val items: List<Item>) :
    ArrayAdapter<Item>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val imageView: ImageView = itemView.findViewById(R.id.listImage)
        val nameTextView: TextView = itemView.findViewById(R.id.listName)
        val timeTextView: TextView = itemView.findViewById(R.id.listTime)
        val precioTextView: TextView = itemView.findViewById(R.id.listPrecio)

        Glide.with(context).load(item!!.imageUri).into(imageView)
        nameTextView.text = item.name
        timeTextView.text = item.time
        precioTextView.text = '$' + item.precio.toString()

        return itemView
    }
}