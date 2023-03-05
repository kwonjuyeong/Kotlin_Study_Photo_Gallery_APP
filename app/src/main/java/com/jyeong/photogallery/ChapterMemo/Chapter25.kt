package com.jyeong.photogallery.ChapterMemo

/*
Chapter 25
Looper, Handler, HandlerThread

사진 이미지를 내려받아 사용자에게 보여주는 작업을 진행한다.
사진을 동적으로 내려받아 보여주기 위해 Looper, Handler, Handlerthread를 사용한다.

이미지를 보여주기 위해 RecyclerView 준비하기
- 이 앱에서는 Rest API에서 받아온 이미지 정보를 ImageView에 사진을 띄어준다
- 테스트를 위해 null 값이 들어가 있으면 기본 이미지를 띄어준다.
 val placeholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bill_up_close
            ) ?: ColorDrawable()
            holder.bindDrawable(placeholder)

URL로부터 이미지 내려받기 준비하기
- URL 문자열을 인자로 받아 실행 가능한 Call 객체르르 반환하는 함수를 추가한다.
- fetchPhoto 함수를 추가해서 인자로 전달된 URL로부터 데이터를 가져와서 Bitmap으로 변환한다.
- 웹 요청을 실행하기 위해 Call.execute()를 사용한다. 안드로이드는 main 스레드에서 네트워크 작업을 할 수 없다.
- 바이트 스트림을 통해 Bitmap 객체를 생성하는데, 응답과 바이트 스트림은 반드시 정상적으로 닫혀야 한다.

내려받기 관련 고려사항
- PhotoGalleryViewModel에서 FlickrFechr().fetchPhotos()르르 호출해서 플리커로부터 JSON 데이터를 내려받는다.
- 이 떄 FlickrFechr는 빈 LiveData<List<GalleryItems>> 객체를 즉시 반환하고 플리커로부터 데이터를 가져오기 위해 비동기 Retrofit 요청을 하며, 이 네트워크 요청으 백그라운드 스레드에서 실행된다.
- 데이터의 내려받기가 끝나면 FlickrFetchr가 JSon 데이터를 GalleryItems이 저장된 List로 파싱해서 LiveData 객체로 전달한다.
- 이로써 섬네일 크기의 사진이 있는 URL을 각 GalleryItem이 갖게 된다.
- 그 다음 섬네일 사진들을 가져와야 한다. FlickrFetchr는 기본적으로 100개의 URL을 받으므로 GalleryItem이 저장된 List는 최대 100개의 URL을 저장한다.
- 그러나 섬네일 이미지를 한꺼번에 다운로드 하는 것은
1. 시간이 오래 걸릴 수 있어서 내려받기가 끝나기 전에 UI가 변경되지 않을 수 있다. 그리고 속도가 느린 네트워크 연결에서는 사용자가 오랫동안 이미지를 볼 수 없다.
2. 이미지 전체를 장치에 저장하는 데 따른 비용 문제가 있다. 100개는 메모리에 쉽게 저장할 수 있지만, 더 많은 데이터라면 메모리가 부족하게 된다.
- 이런 문제에 대해 이미지가 화면에 나타날 필요가 있을 떄만 다운로드 한다. 어뎁터가 onBindViewHolder 구현에서 이미지 다운로드를 시작시키면 된다.

백그라운드 스레드 만들기
- ThumbnailDownloader라는 클래스를 생성한 후 queueThumbnail() 함수를 만들고 스레드를 종료하기 위한 quit() 함수를 오버라이딩 한다.
- 제너릭 타입 매개변수를 사용한다면 다양한 타입의 객체에 ThumbnailDownloader 클래스를 사용할 수 있기 떄문에 코드가 유연해진다.

생명주기 인식 스레드 만들기
- ThumnailDownloader의 목적은 이미지르르 내려받아 PhotoGalleryFragment에 제공하는 것이므로 백그라운드 스레드의 생애를 사용자가 인식하는 프래그먼트 생애와 연동해야 한다.
- 사용자가 프래그먼트 화면을 시작하면 스레드를 실행하고 화면을 끝내면 스레드를 중단해야 한다. 사용자가 장치를 회전해서 구성 변경이 발생한다면, 스레드를 소멸한 후 다시 생성하지 않고 스레드 인스턴스를 보존해야 한다.
- ViewModel의 생애는 사용자가 인식하는 프래그먼트 생애와 일치한다. 하지만 ViewModel에서 스레드를 관리하는 것은 구현이 어렵기 떄문에 FlickFetchr와 같은 레포지터리 컴포넌트에서 하는 것이 바람직하다.
- 우선 PhotoGalleryFragment 인스턴스의 생애가 사용자가 인식하는 프래그먼트 생애와 연동되도록 Fragment를 유보(retain)한다.
- 이는 프래그먼트의 retainInstance 속성을 true로 설정한다.
- 일반적으로 프래그먼트 유보는 피하는 것이 좋다. 그러나 프래그먼트를 유보하면 이와 관련된 코드를 추가로 작성하지 않아도 된다.
- 여기서는 onCreate가 호출될 때 백그라운드 스레드를 시작시키고, onDestroy()가 호출될 때 백그라운드 스레드가 중단도록 한다.

생명주기 관찰자(lifecycler observer)라고 하는 생명주기 인식 컴포넌트는 생명주기 소유자(lifecycler owner)의 생명주기를 관찰한다.
액티비티가 생명주기 소유자의 대표적인 예이며, 이것들은 자체적으로 생명주기를 가지고 LifeCycleOwner 인터페이스를 구현한다.

HandlerThread 시작 또는 중단시키기
- start()를 호출해서 스레드로 시작시킨 후에 Looper를 사용한다. 이것이 스레드가 준비되도록 보장하는 방법이다.
- quit()를 호출해서 스레드를 중단시켜야 한다. 만약 quit()를 하지 않으면 스레드가 계속 살아있게 된다.

메시지와 메시지 핸들러
- 백그라운드 스레드는 UI의 뷰를 변경하는 코드를 실행할 수 없으며, main 스레드에서만 가능하다.
- main 스레드는 시간이 오래 걸리는 작업을 실행할 수 없고, 백그라운드 스레드에서만 가능하다.
- 이에 좋은 해결책은 각 플래시에 메시지 수신함을 주는 방법이다.
- 플래시 1이 메시지를 메시지함에 넣으면 플래시2가 시간이 남을 때 메시지에 있는 자신의 일을 수행한다.
- 안드로이드에서는 스레드가 사용하는 메시지 수신함을 메시지 큐(message queue)라고 하며, 메시지 큐를 사용해 동작하는 스레드를 메시지 루프(message loop)라고 한다.
- 스레드는 자신의 큐에서 새로운 메시지를 찾기 위해 반복해서 루프를 실행한다.
- 메시지 루프는 하나의 스레드와 하나의 Looper로 구성되며, Looper는 스레드의 메시지 큐를 관리하는 객체이다.
- main 스레드는 Looper를 갖는 메시지 루프다. main 스레드가 하는 모든 일은 이것의 Looper에 의해 수행되며, Looper는 자신의 메시지 큐에 있는 메시지들으르 꺼내어 해당 메시지가 지정하는 작업을 수행한다.

    main 스레드                 ThumbnailDownloader
(Looper) (MessageQueue)    (Looper) (MessageQueue)
  -> Handler                    ->


메시지 구조
- 플래시가 메시지 수신함에 넣는 메시지는 처리하는 작업이다.
- 메시지는 Message 클래스의 인스턴스이며 많은 속성을 갖는다
1. what - 메시지를 나타내는 사용자 정의 정수 값
2. obj - 메시지와 함께 전달되는 사용자 지정 객체
3. target - 메시지를 처리할 Handler
- 메시지의 대상이 되는 것은 Handler의 인스턴스다. Handler라는 이름은 메시지 핸들러의 줄임말로 생각할 수 있다.
- 메시지는 자동으로 핸들러에 연결되며, 메시지가 처리할 준비가 되면 이것의 처리를 Handler 객체가 맡는다.

핸들러 구조
- 메시지를 사용해서 실제 작업을 하려면 맨 먼저 Handler 인스턴스가 필요하다. Handler는 Message를 처리하는 대상이면서 Message를 생성하고 게시하는 인터페이스다.
- Message는 반드시 Lopper에서 게시되고 소비되어야 한다. Looper는 Message 객체들의 메시지 수신함을 소유하기 때문이다. 따라서 Handler는 Looper의 참조를 갖는다.
- Handler는 정확히 하나의 Looper에 연결되며, Message는 정확히 하나의 대상 Handler에 연결된다.
- Looper는 큐 전체의 Message를 가지며, 다수의 Message가 동일한 대상 Handler를 참조할 수 있다.
- 또한, 다수의 Handler가 하나의 Looper에 연결될 수 있다.

Handler <- MessageQueue
            (Message)
     ->     (Looper)
          (HandlerThread)

이미지 내려받기 참고 라이브러리
- Picasso - 데이터 용량을 제한한다는 단점이 있다.
- Glide
- Fresco(페이스북)

StrictMode
- 안드로이드에서는 앱에서 해서는 안되는 것들이 있다. 예를 들면 main 스레드에서 네트워크 사용을 하면 ANR 이 발생하며 NetWorkOnMainThreadException이 발생한다.
- StrictMode는 코드의 오류나 보안 문제를 알아내는데 도움을 준다.
- 애플리케이션의 성능을 저하하는 오류를 알아낼 때도 도움이 된다. StrictMode.enableDefaults()를 호출하여 활성화 할 수 있다.
- 호출되었을 때 다음 사항에 해당할 경우 로그캣의 메시지로 원인을 알 수 있다.
1. main 스레드에서의 네트워크 사용
2. main 스레드에서의 디스크 파일 읽기와 쓰기
3. 액티비티가 본연의 생명주기를 벗어나 계속 살아있을 때
4. SQLite 데이터베이스 커서를 닫지 않았을 때
5. SSL/TLS로 암호화되지 않은 평문을 네트워크로 전송할 때




 */