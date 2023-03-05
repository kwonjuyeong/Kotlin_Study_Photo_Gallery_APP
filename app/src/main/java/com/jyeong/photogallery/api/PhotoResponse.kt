package com.jyeong.photogallery.api

import com.google.gson.annotations.SerializedName
import com.jyeong.photogallery.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}