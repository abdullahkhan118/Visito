package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class AutoCompleteResponse {
    @SerializedName("results")
    var results: List<Result>? = null

    @SerializedName("summary")
    var summary: Summary? = null
    override fun toString(): String {
        return "AutoCompleteResponse{" +
                "mResults=" + results +
                ", mSummary=" + summary +
                '}'
    }
}
