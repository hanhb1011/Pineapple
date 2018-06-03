#include <SoftwareSerial.h>

const int motorPin = 9; 
int blueTx=4;   
int blueRx=5;   
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
  analogWrite(9, 200);                     
  delay(3000);   
  analogWrite(9, 0);
  }
  if(myString.equals("A")){
  analogWrite(9, 200);                     
  delay(3000);   
  analogWrite(9, 0);
  }
  
  else if(myString.equals("B")){
  analogWrite(9, 200);                     
  delay(1000);   
  analogWrite(9 , 0);
  }
  
  if(!myString.equals(""))  
  {
    Serial.println("input value: "+myString); 
    
    myString="";  
  }

}

