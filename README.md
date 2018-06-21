# Description #
시각장애인을 위한 내비게이션 App과 촉각을 이용한 방향 안내 지팡이

# 코드 기여도 #

## 이기혁 ##
#### 디렉토리 ####
Pineapple/LSTM/
#### 파일 ####
LSTM2.py : LSTM 모델로 경로 이탈을 학습시키고 이 모델을 pb파일로 변형하는 코드 작성 <br>
TrainingDataGenerator.java : 트레이닝 데이타, 테스트 데이타 생성하는 코드 작성 <br>
testData.csv, trainingData.csv : TrainingDataGenerator.java 실행 결과물 <br>
checkpoint, frozen_test2.pb, optimized_test2.pb, test.pb, test2.ckpt.data-00000-of-00001, test2.ckpt.index, test2.ckpt.meta, test2.pbtxt : LSTM2.py 실행 결과물 <br>

#### 디렉토리 ####
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/Model
#### 파일 ####
Feature.java, FindTheWay.java, FindTheWayRequest.java, Geometry.java, Place.java, Properties.java : T Map API를 사용하여 출발지부터 목적지까지 경로 정보를 담는 모델 클래스 <br>

#### 디렉토리 ####
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/Retrofit
#### 파일 ####
RetrofitClient.java, RetrofitService.java : 레트로핏2 라이브러리를 이용해 json을 파싱해주는 클래스 <br>

#### 디렉토리 ####
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/Util
#### 파일 ####
ApiUtils.java : Base URL 이 담겨있고 RetrofitService 객체를 생성해주는 유틸 <br>
GpsInfoService.java : 모바일의 위치를 파악하기 위해 만든 클래스 <br>
Navigation.java : 내비게이션 구동에 필요한 싱글톤 클래스 작성 <br>
NavigationBody.java : Post방식으로 http 통신하기위해 필요한 RequestBody 객체를 생성해주는 자바 클래스 <br>

#### 디렉토리 ####
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/
#### 파일 ####
PathFragment.java : 사용자에게 내비게이션 이동 경로를 보여주는 프래그먼트 <br>

## 박근준 ##

#### 디렉토리 ####
Pineapple/bluetooth/bluetooth_buzzer/
#### 파일 ####
bluetooth_buzzer.ino : 아두이노의 블루투스 통신 모듈 설치를 위해 블루투스 통신을 통해 부저가 울리는 간단한 실험 <br>

#### 디렉토리 ####
Pineapple/bluetooth/bluetooth_char/
#### 파일 ####
bluetooth_char.ino :  아두이노의 블루투스 통신 설계를 위해 블루투스 통신을 통해 문자열을 방으면 아두이노 상 시리얼에 출력하는 간단한 실험 <br>

#### 디렉토리 ####
Pineapple/compass/
#### 파일 ####
compass.ino : 아두이노 상에서 지자기 센서의 센서값을 이용하여 방위각을 계산해서 시리얼에 출력 <br>

#### 디렉토리 ####
Pineapple/proto/
#### 파일 ####
proto.ino : 아두이노와 아두이노에 설치한 모듈을 작동하기 위한 코딩. <br>
            
주요 기능 <br>
            
1) 블루투스 모듈 <br>

   안드로이드와 통신하기 위한 블루투스 모듈. 간단한 String 값 송신 및 수신을 담당한다. <br>
   
2) 음성 인식 작동 버튼 <br>

   버튼을 누르면 특정 문자열을 블루투스 통신을 통해 안드로이드로 전송하여, 안드로이드 상에서 음성 인식이 작동되도록 한다.<br>
   
3) 스테퍼 모터 <br>

   안드로이드로부터, 다음 노드로의 방위각 값과 지자기 센서로 계산한 사용자가 바라보는 쪽의 방위각의 차이를 계산하여 그만큼 스테퍼 모터를 회전시킨다. <br>
   
4) 진동 모터 <br>

   경로 이탈 시 특정 값을 블루투스 통신을 통해 안드로이드로 부터 받아, 진동모터를 작동시킨다. <br>
 
5) 지자기 센서 <br>

   지자기 센서로 측정한 값을 통해 방위각을 계산하여, 현재 바라보고 있는 방향의 방위각을 얻는다. <br>
   
6) 모터 초기화 기능 <br>

   EEPROM 이라는 아두이노 상 비휘발성 메모리 공간을 활용하여, 해당 메모리에 돌아간 각도 값을 저장한다. 전원을 끄고 다시 켤 때, EEPROM에 저장된 각 만큼 모터를 회전시켜, 회전되었던 모터를 원위치로 초기화한다. <br>
   
  
# 시각장애인용 애플리케이션 #
<p align="center">
  <img src="sources/1.png" width="250"/>
  <img src="sources/6.png" width="250"/>
  <img src="sources/7.png" width="250"/>
</p>
<br>

# 방향 안내 지팡이 #
<p align="center">
  <img src="sources/5.jpg" width="250"/>
</p>


# 보호자용 애플리케이션 #
<p align="center">
  <img src="sources/3.jpg" width="250"/>
  <img src="sources/2.jpg" width="250"/>
  <img src="sources/4.jpg" width="250"/>
</p>
<br>

# Developer #
한효병 이기혁 범효원 박근준


