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
- 공유되는 매개변수와 값은 인터셉터를 사용해서 처리하면 편하다

인터셉터
- 인터셉터는 Square사에서 만든 오픈소스 http 클라이언트인 OKHttp Interceptor 인터페이스로 제공되며, 안드로이드 5.0부터 Http Core 라이브러리로 내장되었다.
- 인터셉터는 요청이나 응답의 정보를 가로채서 우리가 원하는 처리를 할 수 있게 해준다.
- 즉 요청이 서버에 전송되기 전에 매개변수와 값을 URL에 추가하거나 변경할 수 있으며, 서버로부터 수신된 응답에서 원하는 데이터만 발췌하여 사용할 수 있다.







 */