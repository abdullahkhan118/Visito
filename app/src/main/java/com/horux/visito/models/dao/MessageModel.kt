package com.horux.visito.models.dao

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageModel(
    var message: String,
    var id: String,
): Parcelable
