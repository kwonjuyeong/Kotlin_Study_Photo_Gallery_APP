package com.jyeong.photogallery.ChapterMemo

/*
Chapter 30
커스텀 뷰와 터치 이벤트

목표 : View의 커스텀 서브 클래스인 BoxDrawingView를 작성해 터치 이벤트를 처리한다.
- 사용자의 화면 터치와 끌기에 대한 응답으로 박스를 그린다.

커스텀 뷰 생성하기
- 안드로이드에서는 떄로는 특유의 비주얼을 보여주는 커스텀 뷰가 필요하다
- 커스텀 뷰에는 여러 종류가 있지만 크게 두 가지 유형으로 분류할 수 있다.
1. 단순(simple) : 단순 뷰는 내부적으로 복잡할 수 있지만, 자식 뷰가 없어서 구조가 간단하다. 대부분 커스텀 렌더링을 수행한다.
2. 복합(composite) : 복합 뷰는 서로 다른 뷰 객체들로 구성된다. 일반적으로 복합 뷰는 자식 뷰들을 관리하지만, 자신은 커스텀 랜더링을 하지 않는다.
대신에 랜더링은 각 자식 뷰에게 위임한다.

커스텀 뷰를 생성하려면 다음의 세 단계를 거친다.
1. 슈퍼 클래스를 선택한다. 단순 커스텀 뷰에서 View는 비어 있는 캔버스와 같아서 가장 많이 사용된다.
복합 커스텀 뷰에서는 FrameLayout같이 적합한 레이아웃 클래스를 선택한다.
2. 1번에서 선택한 슈퍼 클래스의 서브 클래스를 만들고, 해당 슈퍼 클래스의 생성자를 오버라이드 한다.
3. 슈퍼 클래스의 주요 함수들을 오버라이드해 커스터마이징한다.

BoxDrawingView 생성하기
- BoxDrawingView는 단순 뷰이면서 View의 직계 서브 클래스가 된다.
- AttributeSet에 기본 값을 지정하면, 실제로는 두 개의 생성자가 제공된다.
- 뷰의 인스턴스가 코드 또는 레이아웃 XML 파일로부터 생성될 수 있어야하기 떄문이다.

터치 이벤트 처리하기
- 터치 이벤트를 리스닝할 떄는 View 함수를 사용해서 터치 이벤트 리스너를 설정한다.
fun setOnTouchListener(l: View.OnTouchListener)
- 이 함수는 setOnClickListener(View.OnClickListener)와 같은 방법으로 작동한다.
- 함수의 인자로 View.OnTouchListener를 구현한 리스너 객체를 전달하면 터치 이벤트가 발생할 때마다 onTouchEvent 함수가 호출된다.

override fun onTouchEvent(event : MotionEvent) : Boolean
- 이 함수는 MotionEvent 인스턴스를 인자로 받는다.
- Motion Event는 터치 이벤트를 나타내는 클래스이며, 화면을 터치한 위치와 액션을 포함한다.
ACTION_DOWN : 사용자가 화면을 손가락으로 터치함
ACTION_MOVE : 사용자가 화면 위에서 손가락을 움직임
ACTION_UP : 사용자가 화면에서 손가락을 뗌
ACTION_CANCEL : 부모 뷰가 터치 이벤트를 가로챔

onTouchEvent(MotionEvent)의 구현 코드에서는 MotionEvent 객체의 다음 함수를 호출해 액션의 값을 확인할 수 있다.
- final fun getAction() : Int

모션 이벤트 추적하기
- BoxDrawingView에서는 좌표만 로깅하는게 아니라 화면에 박스들을 그려야 하기 때문에
1. 시작 지점과 현재 지점이 반드시 필요하다.
2. 그 다음 하나 이상의 MotionEvent로부터 발생하는 데이터를 추적해야하며, 이 데이터를 Box 객체에 저장해야 한다.
- ACTION_DOWN 모션 이벤트를 받을 떄마다 currentBox 속성을 새로운 Box 객체로 설정한다.
- 이 객체는 이벤트가 발생한 위치를 시작 지점으로 가지며 박스 List에 저장된다.
- 사용자의 손가락이 화면을 이동하거나 화면에서 떨어지면 currentBox.end를 변경한다.
- 터치가 취소되거나 사용자의 손가락이 화면에서 떨어지면 그리기를 끝내기 위해 currentBox를 null로 변경한다.
- 즉 Box 객체는 List에 안전하게 저장되지만, 모션 이벤트에 관해서는 더 이상 변경이 생기지 않는다.
- updateCurrentBox() 함수에서 invalidate() 를 호출한다. 이는 뷰가 무효라는 것을 안드로이드에게 알려준다.

onDraw(Canvas) 내부에서 렌더링하기
- 뷰가 화면에 그려지가 하려면 다음 View 함수를 오버라이드 해야한다.
protected fun onDraw(canvas : Canvas)
- onTouchEvent(MotionEvent)의 ACTION_MOVE에 대한 응답에서 호출한 invalidate() 함수는 BoxDrawingView를 다시 무효 상태로 만든다.
- 이로써 BoxDrawingView는 자신을 다시 그리게 되고 이 때 onDraw(Canvas)가 다시 호출된다.

Canvas의 매개변수
- Canvas 클래스는 모든 그리기 함수를 갖고 있다. 우리가 호출하는 Canvas의 함수들은 그리는 위치와 선, 원, 단어, 사각형 등의 형태를 결정한다.
- Paint 클래스는 이런 함수들이 어떻게 수행되는지를 결정한다. 즉, 우리가 호출하는 Paint 함수들은 도형이 채워져야 하는지, 어떤 폰트의 텍스트를 그리는지, 어떤 색의 선인지와 같은 특성을 지정한다.




*/