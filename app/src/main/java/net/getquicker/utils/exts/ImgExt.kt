package net.getquicker.utils.exts

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImage(data: Any) {
    Glide.with(context).load(data).into(this)
}