package com.jyeong.photogallery.FlickerApp.api

import com.jyeong.photogallery.FlickerApp.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}