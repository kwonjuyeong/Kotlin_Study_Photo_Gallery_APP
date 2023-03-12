package com.jyeong.photogallery.ChapterMemo

/*
Chapter 27
WorkManager

사용자가 아직 보지 못한 새로 등록된 사진들이 플리커에 있는지 폴링(polling) 하는 기능을 추가한다.
- 이 작업은 백그라운드에서 수행된다.(사용자가 앱을 현재 사용중이지 않아도 실행된다.)
- 새로 등록된 사진들이 있으면 사용자가 앱으로 돌아와서 볼 수 있게 알림도 보여준다.
- Jetpack WorkManager 아키텍쳐 컴포넌트 라이브러리를 사용해서 플리커의 새로 등록된 사진을 정기적으로 확인하는 작업을 처리한다.
- 즉, Worker 클래스의 서브 클래스를 생성하고 일정한 시간 간격으로 실행하도록 스케줄링하여 새로 등록된 사진을 찾으면, NotificationManager를 이용해 사용자에게 알림을 보여준다.

Worker 생성하기
- Worker 서브 클래스에는 백그라운드에서 수행하고자 하는 작업을 처리하는 로직을 넣는다.
- 그리고 이 클래스가 준비되면 WorkRequest의 서브 클래스 중 하나를 사용해서 작업 요청을 생성한다.
- 의존성 추가
implementation 'androidx.work:work-runtime-ktx:$version'
- Work 서브 클래스에는 context와 WorkParameter가 필요하다.
- doWork() 함수를 오버라이드하여 콘솔에 로그를 출력한다.
- doWork() 함수는 백그라운드 스레드에서 호출된다. 따라서 이 함수는 오래 실행되는(최대 10분) 작업을 할 수 있다.
- 이 함수의 반환 값은 ListenableWorker.Result 클래스의 인스턴스이며, 함수의 작업 상태를 나타낸다.
작업 상태로는 success(성공), failure(실패), retry(일시적인 에러, 작업을 다시 실행)가 있다.
- PollWorker는 백그라운드 작업을 실행하는 방법만 안다. 따라서 작업을 스케줄링 하려면 다른 컴포넌트가 필요하다.

작업 스케줄링하기
- Worker의 실행을 스케줄링하려면 추상 클래스인 WorkRequest가 필요하다. 그러므로 실행해야 하는 작업의 유형에 따라 WorkRequest의 서브 클래스 중 하나를 사용해야 한다.
- 한번만 실행하는 작업에서는 OneTimeWorkRequest를 사용하며, 주기적으로 실행하는 작업은 PeriodicWorkRequest를 사용한다.
- OneTimeWorkRequest 클래스는 자신의 중첩 클래스인 Builder를 사용해서 인스턴스를 생성한다. 이때 작업이 실행될 Worker 클래스를 생성자에 전달한다.
- 그 다음에 WorkManager 클래스를 사용해서 OneTimeWorkRequest 인스턴스를 스케쥴링해야 한다.
- 이 때 getInstance() 함수를 호출해 WorkManager의 싱글톤 인스턴스를 얻은 후 OneTimeWorkRequest 인스턴스를 인자로 전달해서 enqueue를 호출한다.

제약 걸기(Constraints 클래스)
- 대부분의 경우에 백그라운드에서 실행하고자 하는 작업은 네트워크와 연결된다.
- 즉, 사용자가 아직 보지 않은 새로운 정보를 네트워크를 통해 폴링하거나, 로컬 데이터베이스의 변경 데이터를 원격 서버에 저장하는 작업이 된다.
- 이런 작업에서는 불필요하게 비용이 드는 데이터 사용을 피해야 한다. 따라서 데이터 이용비가 들지 않는 네트워크 와이파이에 장치가 연결될 때 하는 것이 좋다.
- Constraints 클래스를 사용하면 그런 정보(특정 제약 조건)를 작업 요청에 추가할 수 있다.
- 특정 네트워크 타입을 요구하거나 충분히 충전된 배터리나 장치의 충전기 연결과 같은 것들을 요구할 수 있다.

새로운 사진 확인하기
- 사용자가 보았던 가장 최근 사진의 ID를 공유 프리퍼런스에 저장하고, 플러커 서버에서 새로운 사진들을 가져온 후 저장된 ID와 비교하도록 PollWorker를 변경한다.

사용자에게 알림 보여주기
- PollWorker가 백그라운드로 실행되면서 새로운 사진을 확인하지만, 사용자는 모르기 때문에 새로운 사진을 찾으면 사용자가 볼 수 있게 해야한다.
- 이 때 유용한 도구가 알림이다. 알림은 화면 위에서붕터 끌어서 사용할 수있는 알림 드로어에 나타나는 항목이다.

Notification 객체
- 알림을 게시하려면 Notification 객체를 생성해야 한다.
- Notification 객체는 최소한 다음을 갖는다.
1. 상태 바에 보여줄 아이콘(icon)
2. 알림 드로어에 보여줄 알림 자신인 뷰(view)
3. 알림 드로어의 알림을 사용자가 누르면 실행될 pendingIntent
4. 알림 채널(Notification channel)








 */