#include <SoftwareSerial.h>
 
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
  if(!myString.equals(""))  
  {
    Serial.println("input value: "+myString); 
    myString="";  
  }
}

