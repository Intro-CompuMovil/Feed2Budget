package com.compumovil.feed2budget

import android.net.Uri

data class Item(
    var imageUri: Uri,
    var name: String,
    var time: String,
    var precio: Double,
    var uid: String
)