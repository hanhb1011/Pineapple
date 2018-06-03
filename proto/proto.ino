#include <Stepper.h>
#include <SoftwareSerial.h>
#include <Wire.h>
#define address 0x1E

int in1Pin=11,in2Pin=10,in3Pin=9,in4Pin=8;
Stepper motor(200,in1Pin,in2Pin,in3Pin,in4Pin);
const int motorPin = 7;
int blueTx=2;   
int blueRx=3;
SoftwareSerial mySerial(blueTx, blueRx);  
SoftwareSerial BTSerial(blueTx, blueRx);
String myString="";   
byte buffer[1024]; 
int bufferPosition; 


void setup(){
  pinMode(in1Pin,OUTPUT); pinMode(in2Pin,OUTPUT);
  pinMode(in3Pin,OUTPUT); pinMode(in4Pin,OUTPUT);
  BTSerial.begin(9600); 
  Serial.begin(9600);
  motor.setSpeed(100);
  mySerial.begin(9600);
  bufferPosition = 0; 
  Wire.begin();
 
  Wire.beginTransmission(address);
  Wire.write(0x02); 
  Wire.write(0x00); 
  Wire.endTransmission();
}

void dir_cor(int a){

  if(a == 150){
  motor.step( 2000); delay(2000);
  }
  
  else if(a == 60){
  motor.step(-2000); delay(2000);
  }


}

void wrong_vib(int b){
  if(b == 150){
  analogWrite(7, 200);                     
  delay(3000);   
  analogWrite(7, 0);
  }
  
  else if(b == 60){
  analogWrite(7, 200);                     
  delay(1000);   
  analogWrite(7 , 0);
  }
}

void compass(){
  int x,y,z; //triple axis data
  double angle;
  
  
  Wire.beginTransmission(address);
  Wire.write(0x03);
  Wire.endTransmission();
 

  Wire.requestFrom(address, 6);
  if(6<=Wire.available()){
    x = Wire.read()<<8; //X msb
    x |= Wire.read(); //X lsb
    z = Wire.read()<<8; //Z msb
    z |= Wire.read(); //Z lsb
    y = Wire.read()<<8; //Y msb
    y |= Wire.read(); //Y lsb
  }  
  angle = (double)atan2(y, x);
  
  float declinationAngle = -(8+26/60)*PI/180; 
  angle += declinationAngle; 

  if (angle < 0){
    angle += 2*PI;
  }

  if (angle > 2*PI){
    angle -= 2*PI;
  }
  
  float bearing = angle * 180/PI;
  
  Serial.println("Azimuth : "+String(bearing));

  
  BTSerial.print("Azimuth : ");
  BTSerial.println(bearing);

  angle = 0;
  bearing = 0;

  delay(500);
}

void loop(){

   while(mySerial.available())  
  {
    int inChar = mySerial.read();
    if (isDigit(inChar)){
      myString += (char)inChar; 
    }

  }

  compass();
  delay(500);
  wrong_vib(myString.toInt()); 
  dir_cor(myString.toInt());

   if(!myString.equals(""))  
  {
    Serial.println("input value: "+myString); 
    
    myString = "";  
  }

}



