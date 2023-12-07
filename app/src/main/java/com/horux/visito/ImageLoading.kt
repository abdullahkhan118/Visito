package com.horux.visito

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun loadImage(activity: Activity,imageUrl: String, imageView: ImageView) {
    val options: RequestOptions = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.img_loading)
        .error(R.drawable.img_no_image)
    Glide.with(activity).load(imageUrl).apply(options)
        .into(imageView)
}