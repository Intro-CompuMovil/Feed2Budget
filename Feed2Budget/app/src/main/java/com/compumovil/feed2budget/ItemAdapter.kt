package com.compumovil.feed2budget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ItemAdapter(context: Context, private val items: List<Item>) :
    ArrayAdapter<Item>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Obtener el ítem para la posición actual
        val item = getItem(position)

        // Verificar si la vista existente se puede reutilizar, de lo contrario inflar una nueva
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        // Encontrar los componentes en el layout
        val imageView: ImageView = itemView.findViewById(R.id.listImage)
        val nameTextView: TextView = itemView.findViewById(R.id.listName)
        val timeTextView: TextView = itemView.findViewById(R.id.listTime)

        // Asignar los valores del ítem a los componentes
        imageView.setImageResource(item!!.imageResId)
        nameTextView.text = item.name
        timeTextView.text = item.time

        return itemView
    }
}