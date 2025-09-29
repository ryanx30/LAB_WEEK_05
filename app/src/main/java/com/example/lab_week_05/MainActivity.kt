package com.example.lab_week_05

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_week_05.api.CatApiService
import com.example.lab_week_05.model.ImageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }

    private val catApiService by lazy {
        RetrofitInstance.retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy {
        findViewById(R.id.api_response)
    }

    private val imageResultView: ImageView by lazy {
        findViewById(R.id.image_result)
    }

    private val imageLoader: ImageLoader by lazy {
        GlideLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(MAIN_ACTIVITY, "onCreate: Activity started")
        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        Log.d(MAIN_ACTIVITY, "getCatImageResponse: Requesting cat image from API...")

        val call = catApiService.searchImages(1, "full", 1)
        call.enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "onFailure: Failed to get response", t)
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<List<ImageData>>,
                response: Response<List<ImageData>>
            ) {
                Log.d(MAIN_ACTIVITY, "onResponse: Raw response received")

                if (response.isSuccessful) {
                    val imageList = response.body()
                    Log.d(MAIN_ACTIVITY, "onResponse: Image list size = ${imageList?.size ?: 0}")

                    val firstImage = imageList?.firstOrNull()
                    val imageUrl = firstImage?.url.orEmpty()
                    val breedName = firstImage?.breeds?.firstOrNull()?.name ?: "Unknown"

                    Log.d(MAIN_ACTIVITY, "onResponse: First image URL = $imageUrl")
                    Log.d(MAIN_ACTIVITY, "onResponse: Breed name = $breedName")

                    if (imageUrl.isNotBlank()) {
                        imageLoader.loadImage(imageUrl, imageResultView)
                        Log.d(MAIN_ACTIVITY, "onResponse: Image loaded successfully into ImageView")
                    } else {
                        Log.d(MAIN_ACTIVITY, "onResponse: Missing image URL")
                    }

                    apiResponseView.text = getString(R.string.breed_placeholder, breedName)
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    Log.e(MAIN_ACTIVITY, "onResponse: Failed with error = $errorBody")
                }
            }
        })
    }
}
