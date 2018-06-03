#include <SoftwareSerial.h>

const int speakerPin = 9; 
int blueTx=2;   
int blueRx=3;   
SoftwareSerial mySerial(blueTx, blueRx);  
String myString=""; 
 
void setup() {
  Serial.begin(9600); 
  mySerial.begin(9600);
}
 
void loop() {
  while(mySerial.available())  
  {
    char myChar = (char)mySerial.read();  
    myString+=myChar;   
    delay(5);          
  }
    if (mySerial.available()) { //블루투스에서 넘어온 데이터가 있다면
    Serial.write(mySerial.read()); //시리얼모니터에 데이터를 출력
  }
  if (Serial.available()) {    //시리얼모니터에 입력된 데이터가 있다면
    mySerial.write(Serial.read());  //블루투스를 통해 입력된 데이터 전달
  }
    if(myString.equals("A")){
  int frequency = map(1, 0, 1023, 100, 5000);
  int duration = 200;
  tone(speakerPin, frequency, duration);
  }
  
  else if(myString.equals("B")){
  int frequency = map(2, 0, 1023, 100, 5000);
  int duration = 1000;
  tone(speakerPin, frequency, duration);
  }
  
  if(!myString.equals(""))  
  {
    Serial.println("input value: "+myString); 
    
    myString="";  
  }

}

