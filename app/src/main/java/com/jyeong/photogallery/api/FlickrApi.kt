package com.jyeong.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface FlickrApi {
    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=776bb4360dbe8d6213e3feb5bde54699" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )

    fun fetchPhotos(): Call<FlickrResponse>
}