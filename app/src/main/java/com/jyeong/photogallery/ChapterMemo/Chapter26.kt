package com.jyeong.photogallery.ChapterMemo

/*
Chapter 26
SearchView와 공유 프리퍼런스(SharedPreference)

- 플러커 검색 API 사용
- SearchView에서 쿼리 입력하여 검색 요청
- RecyclerView에 응답 결과 표시
- 공유 프리퍼런스로 파일 시스템에 저장

플러커 검색하기
- 플러커의 사진을 텍스트로 검색할 때는 플러커 API 메서드인 flickr.photos.search를 호출한다.
- 이 때 제목, 설명, 태그에 검색 텍스트가 포함된 사진의 메타데이터가 반환된다.
- 쿼리 어노테이션은 쿼리 매개변수를 URL 끝에 동적으로 추가해줄 수 있도록 해준다.
- 공유되는 매개변수와 값은 인터셉터를 사용해서 처리하면 편하다

인터셉터
- 인터셉터는 Square사에서 만든 오픈소스 http 클라이언트인 OKHttp Interceptor 인터페이스로 제공되며, 안드로이드 5.0부터 Http Core 라이브러리로 내장되었다.
- 인터셉터는 요청이나 응답의 정보를 가로채서 우리가 원하는 처리를 할 수 있게 해준다.
- 즉 요청이 서버에 전송되기 전에 매개변수와 값을 URL에 추가하거나 변경할 수 있으며, 서버로부터 수신된 응답에서 원하는 데이터만 발췌하여 사용할 수 있다.
인터셉터 통신
1. intercepter(chain) 함수는 URL 매개변수와 값을 요청에 추가하고 새로운 URL로 교체한다.
2. 원래 요청을 가져오기 위해 chain.request()를 호출한다.
3. originalRequest.url()을 호출해서 요청에 포함된 원래 URL을 가져온 후 HttpUrl.Builder를 통해 해당 URL에 쿼리 매개변수들을 추가한다.
4. HttpUrl.Builder는 원래의 요청을 기반으로 새로운 Request 객체를 생성하고 원래의 URL을 새로운 URL로 교체한다.
5. chain.proceed(newRequest)를 호출해 요청을 전송하고 응답을 나타내는 Response 객체를 받는다. 이 함수를 호출하지 않으면 네트워크 요청이 수행되지 않는다.

SearchView 사용하기
- menu에 SearchView 추가하기
app:actionViewClass="androidx.appcompat.widget.SearchView"
- onCreateOptionMenu 호출하기

SearchView에서 사용자에게 응답하기
- 최근 쿼리 문자열 저장 및 검색 결과 변경 - Transformations.switchMap(trigger : LiveData<X>, transformFunction:Function<X, LiveData<Y>>)
- SearchView 쿼리 문자열을 변경할 때마다 쿼리 문자열 값을 변경하도록 SearchView.OnQueryTextListener 인터페이스를 사용한다.
- setOnQueryTextListener, onQueryTextChange를 통해 SearchView 텍스트의 변화를 감지한다.
- SearchView에서 사용자에게 입력받은 값을 Query를 통해 전송한 후 Response data를 받아서 화면에 표시해준다.

공유 프리퍼런스를 사용한 간단한 데이터 보존
- 공유 프리퍼런스는 간단한 데이터를 보존하기 위해 사용한다.
- 현재 앱에서 공유 프리퍼런스에 쿼리 문자열을 저장하고, 앱이 최초 실행되면 공유 프리퍼런스에 저장된 쿼리 문자열을 가져와서 검색을 실행하는 데 사용한다.
- 공유 프리퍼런스는 파일 시스템의 파일이며, SharedPreference 인터페이스를 구현하는 클래스를 사용해서 데이터를 읽거나 수정한다.
- SharedPreference의 인스턴스는 Bundle과 같이 키와 값의 쌍으로 데이터를 저장한다.
- 키는 문자열이고, 값은 원자적 데이터 타입이다. 데이터가 저장된 공유 프리퍼런스 파일은 간단한 XML 파일로 저장된다.
- 공유 프리퍼런스 파일은 앱의 샌드박스에 저장된다. 따라서 암호와 같이 민감한 정보는 저장하지 않는 것이 좋다.

공유 프리퍼런스 사용하기
- QueryPreference는 모든 다른 컴포넌트가 공유하므로 앱에서 하나의 QueryPreference 인스턴스만 생성하면 된다.
- 코틀린 object의 함수는 QueryPreference.setStoredQuery와 같이 ObjectName.funtionName 형태로 사용할 수 있다.
- 공유 프리퍼런스에 저장된 값을 읽을 때는 SharedPreference.get자료형()으로 읽어온다.
- setStoredQuery(Context) 함수에서는 인자로 받은 컨텍스트의 공유 프리퍼런스에 쿼리 문자열을 저장한다.
- SharedPreference.Editor를 통해 값을 저장하거나 삭제할 수 있고, apply를 통해 적용시킬 수 있다.

***ViewModel에서 QueryPreference의 함수들을 사용하려면 Application Context가 필요하다. 따라서 Application 객체 참조를 받아 속성으로 보존하며 ViewModel을 AndroidViewModel의 서브 클래스로 변경한다.



 */