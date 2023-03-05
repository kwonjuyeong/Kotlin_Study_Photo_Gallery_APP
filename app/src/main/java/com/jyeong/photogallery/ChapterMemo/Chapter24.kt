package com.jyeong.photogallery.ChapterMemo

/*
Chapter 24
HTTP와 백그라운드 태스크
(Retrofit, Rest API, GSON)

Rest API를 Retrofit 라이브러리를 통해 HTTP 네트워킹을 한 것을 GSON으로 Json 파싱을 해 사용한다.

PhotoGalleryAPP
- 사진 공유 사이트인 플리커의 클라이언트 앱
- 가장 많은 관심을 받은 오늘의 공개 사진들을 띄어준다.
- 오늘날 거의 모든 웹 서비스 프로그래밍은 HTTP 네트워킹 프로토콜을 기반으로 한다.

Retrofit을 사용한 네트워킹
- Retrofit은 Square 사에서 제공하는 오픈소스 라이브러리로, REST API를 안드로이드에서 쉽게 사용하게 해주며, OkHTTP 라이브러리를 자신의 HTTP 클라이언트로 사용한다.
- Retrofit은 HTTP 게이트웨이 클래스 생성을 도와준다.
- 애노테이션이 지정된 함수를 갖는 인터페이스를 작성하면, Retrofit이 이 인터페이스의 구현 클래스를 생성한다.
- 이 클래스는 HTTP 요청을 하고 OkHTTP.ResponseBody 로 HTTP 응답을 파싱한다.
1. Retrofit 의존성 추가
- implementation 'com.squareup.retrofit2:retrofit:2.5.0'
2. API 인터페이스 생성하기
- HTTP 요청함수 어노테이션을 지정해준다. (@GET, POST, DELETE, PUT, @HEAD)
- 상대경로를 지정해준다.
3. Retrofit 인스턴스와 API 인스턴스 생성하기
- Retrofit 인스턴스는 API 인스턴스를 구현하고 생성하는 일을 한다.
- 정의한 API 인터페이스를 기반으로 웹 요청을 만드려면 Retrofit이 인터페이스를 구현해서 인스턴스로 생성하게 해야한다.
4. 문자열 변환기 추가하기
- implementation 'com.squareup.retrofit2:converter-scalars:2.5.0'
- Retrofit이 응답을 문자열로 역직렬화하도록 Retrofit 객체를 생성할 때 변환기를 지정한다.
- 변환기에서는 ResponseBody 객체를 필요한 객체 타입으로 변환한다.

웹 요청 실행하기
- Call 객체를 생성한다.
- Call.enqueue는 Call 객체가 나타내는 웹 요청을 백그라운드 스레드에서 실행한다.
- 백그라운드 스레드는 해야할 작업을 큐나 List에 유지하고 관리한다.
- main 스레드에서의 네트워크 작업 수행을 하면, 안드로이드가 NetworkOnMainThreadException을 발생시킨다.

Retrofit은 두 가지 안드로이드 스레드 규칙을 지키도록 한다.
- 시간이 걸리는 작업을 main 스레드가 아닌 백그라운드 스레드에서 실행한다.
- UI 변경은 main 스레드에서만 하며 절대로 백그라운드 스레드에서 하지 않는다.

네트워크 퍼미션 요청하기
- 네트워크 권한을 요청해야 한다.
- <uses-permission android:name="android.permission.INTERNET"/>

플리커에서 JSON 가져오기
- JSON은 자바스크립트 객체 표기(JavaScript Object Notation를 의미하는 널리 사용되는 데이터 형식으로, 특히 웹 서비스에서 많이 사용된다.

GSON
- 안드로이드는 org.json 표준 패키지를 포함하며, 이 패키지에는 JSON 텍스트를 생성 및 파싱하는 클래스들이 있다(JSONObject, JSONArray)
- JSON 텍스트를 쉽게 자바 객체로 (역직렬화) 또는 그 반대로(직렬화) 변환하는 라이브러리들이 많이 있다.
- 그런 라이브러리 중 하나가 Gson이다.
- Gson은 JSON 데이터를 코틀린 객체로 자동 변환하므로 파싱하는 코드를 작성할 필요가 없다. 대신에 JSON 데이터를 JSON 계층 구조의 객체로 연관시키는 코틀린 클래스를 정의하면 된다.
-  implementation 'com.google.code.gson:gson:2.9.0'
   implementation 'com.squareup.retrofit2:converter-gson:2.4.0'

@SerializedName 어노테이션
- Gson에게 이 속성과 연관되는 JSON 객체의 필드 이름을 지정해준다.

구성 변경 시의 네트워크 요청
- 화면 전환 등 장치의 구성이 변경했을 경우 instance를 재요청 하기 떄문에 불필요한 네트워크 통신을 하게 된다.
- 따라서 데이터 사용량이 증가하면 앱의 성능에도 영향을 미칠 수 있다.
- 바로 이럴 떄 ViewModel을 사용하면 문제가 해결된다.
- implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0'



 */