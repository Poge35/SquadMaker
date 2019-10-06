package com.example.squadmaker.model.network.comicsresponse

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("limit") val limit: String,
    @SerializedName("total") val total: String,
    @SerializedName("results") val results: List<ComicsDetails>
)