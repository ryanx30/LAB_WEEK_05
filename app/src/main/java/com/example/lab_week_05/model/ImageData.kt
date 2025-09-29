package com.example.lab_week_05.model

import com.squareup.moshi.Json

data class ImageData(
    @field:Json(name = "url")
    val url: String?,
    @field:Json(name = "breeds")
    val breeds: List<CatBreedData>?
)