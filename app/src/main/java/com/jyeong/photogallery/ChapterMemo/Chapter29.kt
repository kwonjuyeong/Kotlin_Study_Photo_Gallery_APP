package com.jyeong.photogallery.ChapterMemo

/*
Chapter 29
웹과 WebView의 브라우징

목표 : 사용자가 사진을 선택하면 Flicker 웹 페이지를 볼 수 있도록 변경한다.
1. 장치의 브라우저 앱을 사용하는 방법
2. WebView 클래스를 사용해서 PhotoGallery 내부에서 웹 콘텐츠를 보여주는 방법

암시적 인텐트 활용
- 암시적 인텐트를 사용해서 URL을 브라우징하며, 이 인텐트는 사진의 URL을 사용해서 장치의 웹 브라우저를 시작시킨다.
- RecyclerView의 항목을 누르면 앱이 응답하도록 한다.
- 플리커 자체 Json에서 제공하는 사진 Uri가 없기 때문에 아이디와 사진 아이디를 넣어서 Uri주소를 만들어 준다.
data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s") var url: String = "",

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
- inner 키워드를 Holder에 추가하면 외부 클래스의 속성과 함수를 바로 사용할 수 있다. 여기서는 PhotoHolder를 GalleryItem에 바인딩한다.
    //inner 키워드 추가, View.OnClickListener 사용하여 클릭 시 보여준다.
    private inner class PhotoHolder(private val itemImageView: ImageView)
        : RecyclerView.ViewHolder(itemImageView), View.OnClickListener {

        private lateinit var galleryItem: GalleryItem

        //init에서 클릭 리스너를 정의한다.
        init {
            itemView.setOnClickListener(this)
        }
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable

        //아이템 바인딩 함수를 만든다.
        fun bindGalleryItem(item : GalleryItem){
            galleryItem = item
        }
        //클릭 시 Uri를 이용해 암시적 인텐트를 실행한다.
        override fun onClick(v: View?) {
            val intent = Intent(Intent.ACTION_VIEW, galleryItem.photoPageUri)
            startActivity(intent)
        }
        }

    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>)
        : RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PhotoHolder {
            val view = layoutInflater.inflate(R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]

            val placeholder: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.wait) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
            //갤러리 아이템을 웹뷰에 띄어주는 함수를 바인딩한다.
            holder.bindGalleryItem(galleryItem)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }

WebView 활용
- 브라우저를 실행하는 대신 액티비티 내부에서 웹 콘텐츠를 보여줄 때 사용한다.(더 어려운 방법이다.)
- 예를 들어, 우리가 생성한 HTML을 보여준다거나, 웹 브라우저의 사용을 피하고 싶을 경우다.
(도움말 웹 페이지를 웹 브라우저로 열면 전문화된 문서처럼 보이지 않으며, 도움말을 보여주는 처리는 커스터마이징 하기 어렵고 도움말 웹 페이지를 앱의 UI로 통합할 수도 없다.)
- UI 내부에 웹 콘텐츠를 보여주려면 WebView 클래스를 사용한다.
1. WebView를 보여줄 새로운 액티비티와 프래그먼트를 생성한다.
2. Fragment를 호스팅하는 액티비티를 생성한다.(FrameLayout 액티비티는 생성할 필요 없다.)
3. 사진을 로드하기 위한 URL를 알려준다.
4. 자바스크립트를 사용할 수 있게 한다.
- 기본적으로 WebView는 자바스크립트 사용이 비활성화 되어 있다.(CSS 공격을 염려해 경고 메시지가 나온다.)
- onCreateView() 함수에 @SuppressLint("SetJavaScriptEnabled") 어노테이션을 지정한다.
5. WebViwClient 클래스 인스턴스를 WebView에 설정한다.
- URL의 페이지 로딩은 WebView를 구성한 후에 처리되어야 하므로 맨 나중에 처리한다.
- WebViewClient 인스턴스를 생성해서 이것의 참조를 WebView의 webViewClient 속성에 저장한다.
- 새 URL 페이지는 현재 페이지에서 다른 URL의 페이지로 이동한다고 알려주거나 사용자가 링크를 클릭하면 로드된다.
이 때 WebViewClient가 없다면 WebView는 새 URL의 페이지를 로드하기 위해 액티비티 매니저에게 적합한 액티비티를 찾아달라고 암시적 인텐트를 사용해서 요청한다.
- 이와는 달리 WebViewClient를 사용하면 다르게 처리된다. 액티비티 매니저에게 요청하지 않고 WebViewClient에 요청하므로 WebView에 새 URL 페이지가 나타나게 된다.

WebView사용 시의 장치 회전 처리
- 웹 페이지가 나타나고 화면을 회전했을 때, WebView는 웹 페이지를 다시 로드한다.
- WebView가 너무 많은 데이터를 갖고 있어서 onSaveInstanceState()에서 모든 데이터를 저장할 수 없기 때문이다.
- 유보(retain)하는 것이 이 문제의 가장 쉬운 방법이라고 생각할 수 있지만 아니다. WebView는 뷰 계층 구조의 일부이므로 장치의 방향을 바꾸면 소멸하였다 다시 생성하기 때문이다.
- WebView나 VideoView같은 클래스에서는 매니페스트에 android:configChanges 속성을 지정해 액티비티 내부에서 구성 변경을 처리하게 할 수 있다.
- 이 떄 WebView는 자신의 데이터를 다시 로드하지 않아도 되는데. 구성 변경이 생겨도 액티비티가 소멸하지 않고 뷰만 변경된 화면에 맞추어 바꾸기 때문이다.
<activity android:name=".PhotoPageActivity"
     android:configChanges="keyboardHidden|orientation|screenSize"/>

구성 변경 처리의 위험성
- 리소스 수식자 기반의 구성 변경이 더 이상 자동으로 작동하지 않는다. 따라서 구성 변경이 생기면 우리가 뷰를 직접 다시 로드해야 하는데, 생각보다 복잡하다.
- 액티비티에서 구성 변경을 처리하면, Activity.onSavedInstanceState()를 오버라이드해 일시적인 UI 상태 정보를 저장하는 일에 소홀해질 수 있다.
- 메모리가 부족할 경우 액티비티가 소멸 및 재생성되는 것을 대비해야하기 때문에 장치 회전에 걸쳐 액티비티가 유보한다 해도 onSavedInstanceState()를 오버라이드 해야한다.

자바 스크립트 객체 주입하기
- WebView 자체에 포함된 문서(웹 페이지)에 임의의 자바스크립트 객체를 추가하면 더 많은 일을 할 수 있다.
- addJavascriptInterface(Any, String) 함수가 있다.
- 이 함수를 사용하면 우리가 지정한 이름의 문서에 임의의 객체를 주입할 수 있다.
WebView.addJavascriptInterface(object : Any(){
    @JavascriptInterface
    fun send(message : String){
        Log.i(TAG, "Received message : $message")
        }
  }, "androidObject")

사용하기
<input type = "button" value= "In WebView!"
    onClick="sendToAndroid('In Android land')"/>
<script type = "text/javascript">
    funtion sendToAndroid(message){
            androidObject.send(message);
    }
</script>

- 여기서 고려할 사항은
1. send(String) 함수를 호출할 때 이 함수가 main 스레드에서 호출되지 않고 WebView가 갖는 스레드에서 호출된다.
- 따라서 안드로이드 UI를 변경하려면 Handler를 사용해서 main 스레드에 제어를 넘겨주어야 한다.
2. 데이터 타입을 많이 지원하지 못한다.
- String과 기본 타입만 가능하다. 따라서 더 복잡한 타입에서는 String을 JSON으로 변환해서 전송하고, 받을 때는 파싱한다.




 */