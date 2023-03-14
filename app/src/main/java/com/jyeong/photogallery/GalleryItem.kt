package com.jyeong.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s") var url: String = "",
    //플리커 자체 Json에서 제공하는 사진 Uri가 없기 때문에 아이디와 사진 아이디를 넣어서 주소를 만들어 ㅈ ㅜㄴ다.
    @SerializedName("owner") var owner : String = ""
){
    val photoPageUri : Uri
    get(){
        return Uri.parse("https://www.flickr.com/photos/")
            .buildUpon()
            .appendPath(owner)
            .appendPath(id)
            .build()
    }
}