# Description #
시각장애인을 위한 내비게이션 App과 촉각을 이용한 방향 안내 지팡이

# 코드 기여도 #

## 이기혁 ##
### 디렉토리 ###
Pineapple/LSTM/
### 파일 ###
LSTM2.py : LSTM 모델로 경로 이탈을 학습시키고 이 모델을 pb파일로 변형하는 코드 작성 <br>
TrainingDataGenerator.java : 트레이닝 데이타, 테스트 데이타 생성하는 코드 작성 <br>
testData.csv, trainingData.csv : TrainingDataGenerator.java 실행 결과물 <br>
checkpoint, frozen_test2.pb, optimized_test2.pb, test.pb, test2.ckpt.data-00000-of-00001, test2.ckpt.index, test2.ckpt.meta, test2.pbtxt : LSTM2.py 실행 결과물 <br>

### 디렉토리 ###
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/Model
### 파일 ###
Feature.java, FindTheWay.java, FindTheWayRequest.java, Geometry.java, Place.java, Properties.java : T Map API를 사용하여 출발지부터 목적지까지 경로 정보를 담는 모델 클래스 <br>

### 디렉토리 ###
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/Retrofit
### 파일 ###
RetrofitClient.java, RetrofitService.java : 레트로핏2 라이브러리를 이용해 json을 파싱해주는 클래스 <br>

### 디렉토리 ###
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/Util
### 파일 ###
ApiUtils.java : Base URL 이 담겨있고 RetrofitService 객체를 생성해주는 유틸 <br>
GpsInfoService.java : 모바일의 위치를 파악하기 위해 만든 클래스 <br>
Navigation.java : 내비게이션 구동에 필요한 싱글톤 클래스 작성 <br>
NavigationBody.java : Post방식으로 http 통신하기위해 필요한 RequestBody 객체를 생성해주는 자바 클래스 <br>

### 디렉토리 ###
Pineapple/Pineapple_Android/app/src/main/java/org/androidtown/pineapple_android/
### 파일 ###
PathFragment.java : 사용자에게 내비게이션 이동 경로를 보여주는 프래그먼트 <br>

 
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


