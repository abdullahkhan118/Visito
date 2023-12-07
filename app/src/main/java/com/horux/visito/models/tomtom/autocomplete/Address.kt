package com.horux.visito.models.tomtom.autocomplete

import com.google.gson.annotations.SerializedName

class Address {
    @SerializedName("country")
    var country: String? = null

    @SerializedName("countryCode")
    var countryCode: String? = null

    @SerializedName("countryCodeISO3")
    var countryCodeISO3: String? = null

    @SerializedName("countrySubdivision")
    var countrySubdivision: String? = null

    @SerializedName("freeformAddress")
    var freeformAddress: String? = null

    @SerializedName("localName")
    var localName: String? = null

    @SerializedName("municipality")
    var municipality: String? = null
}
