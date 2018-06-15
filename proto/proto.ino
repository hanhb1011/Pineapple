#include <Stepper.h>
#include <SoftwareSerial.h>
#include <Wire.h>
#include <EEPROM.h>
#include <I2Cdev.h>
#include <HMC5883L.h>
//#include <MechaQMC5883.h>
#define address 0x1E
//#include <LowPower.h>

int in1Pin=12,in2Pin=11,in3Pin=10,in4Pin=9;
Stepper motor(32,in1Pin,in2Pin,in3Pin,in4Pin);
const int motorPin = 7;
int blueTx=2;   
int blueRx=3;
SoftwareSerial mySerial(blueTx, blueRx);  
SoftwareSerial BTSerial(blueTx, blueRx);
String myString="";
byte buffer[1024]; 
int bufferPosition; 
int val;
int init_angle=0;
int voiceRecoPin = 5;
int wake = 0;
int wake2 = 1;
HMC5883L mag;
int16_t mx, my, mz;
//int sleepPin = 6;
//MechaQMC5883 qmc;
int input_angle = -1;

void setup(){
  pinMode(in1Pin,OUTPUT); pinMode(in2Pin,OUTPUT);
  pinMode(in3Pin,OUTPUT); pinMode(in4Pin,OUTPUT);
  pinMode(voiceRecoPin,INPUT);
  //pinMode(sleepPin, INPUT);
  BTSerial.begin(9600);
  Wire.begin();
  Serial.begin(9600);
  motor.setSpeed(300);
  mySerial.begin(9600);
  bufferPosition = 0; 
  Wire.beginTransmission(address);
  Wire.write(0x02); 
  Wire.write(0x00); 
  Wire.endTransmission();
  mag.initialize();
  //qmc.init();
  //qmc.setMode(Mode_Continuous,ODR_200Hz,RNG_2G,OSR_256);
  init_motor(EEPROM.read(wake2));
}

int correct(int angle){
  if(abs(angle)>180) {
     if(angle>0){
        return angle-360;
     } else {
        return angle+360;
     }
  } else {
    return angle;
  }
}

void dir_cor(int dst_angle, int cur_angle){
  int final_angle = dst_angle - cur_angle;
  final_angle = correct(final_angle);
  final_angle -= init_angle;
  final_angle = correct(final_angle);

  init_angle += final_angle;
  init_angle = correct(init_angle);
  
  Serial.println(dst_angle);
  Serial.println(cur_angle);
  Serial.println(init_angle);
  Serial.println(final_angle);
  Serial.println("\n");
 
  if (init_angle >= 0)
 {
  EEPROM.write(wake, init_angle);
  EEPROM.write(wake2, 0);
 }

  else if (init_angle < 0)
  {
   EEPROM.write(wake, -init_angle);
   EEPROM.write(wake2, 1);
  } 
  
 int val = map(final_angle,0,360,0,2048); 
 motor.step(-val);
}

void wrong_vib(int b){
  if(b == 700){
  analogWrite(7, 200);                     
  delay(3000);   
  analogWrite(7, 0);
  }
}

int compass(){
  /*
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

  angle = 0;
  bearing = 0;

  delay(500);
  */
  
  mag.getHeading(&mx, &my, &mz);
  float heading = atan2(my, mx);
  if(heading < 0)
  heading += 2 * M_PI;
  float  heading2 = heading * 180/M_PI;
  Serial.print("heading:\t");
  Serial.println((int)heading2);
  
  return heading2;
  
    /*
  int x,y,z;
  int a;

  qmc.read(&x,&y,&z);
  a = qmc.azimuth(&y,&x);
  Serial.print("x: ");

  Serial.print(x);

  Serial.print(" y: ");

  Serial.print(y);

  Serial.print(" z: ");

  Serial.print(z);

  Serial.print(" a: ");
  Serial.print(a);
  Serial.println();
  delay(100);

  return a;
  */
}

void init_motor(int a){
  int angle = EEPROM.read(wake);
  
  if(angle!=0){
    int val2 = map(angle,0,360,0,2048);
    
      if (a>0) {
    motor.step(-val2);
    }
    
    else {
    motor.step(val2);
    }
  }
  
  init_angle = 0;
  EEPROM.write(wake, 0);
  EEPROM.write(wake2, 0);
}
/*
void wakeUp()
{
  
}

void sleepMode(int c)
{
  if (c == 1)
  {
  init_motor();

  BTSerial.println("Power Off");
  attachInterrupt(0, wakeUp, LOW);  // LOW, HIGH, RISING, FALLING, CHANGE

  LowPower.powerDown(SLEEP_FOREVER, ADC_OFF, BOD_OFF);

  detachInterrupt(0);
  }
  
  else if (c == 0)
  {
    wakeUp();
  }
}
*/

void voiceReco (int d)
{
  if (d==1)
  {
    BTSerial.println("button"); 
  }
}

void loop(){
// int sleepButtonInput = digitalRead(sleepPin);
  int voiceRecoInput = digitalRead(voiceRecoPin);
  bool readSomething = false;
  
   while(mySerial.available())  
  {
    int inChar = mySerial.read();
    if (isDigit(inChar) || inChar == '-'){
      myString += (char)inChar; 
      readSomething = true; 
    }
  }
  
  if(readSomething && myString.toInt() <= 360 && 0 <= myString.toInt()) {
    input_angle = myString.toInt();
  }
  
  compass();
  
  delay(500);
  wrong_vib(myString.toInt()); 
  
  if (myString.toInt() <= 360 && 0 <= myString.toInt() && input_angle != -1){
    dir_cor(input_angle, compass());
   }
  
   /*
  if (sleepButtonInput == 1)
  {
    wake += sleepButtonInput;  
  }
  */
//  sleepMode(sleepButtonInput);
  
  voiceReco(voiceRecoInput);
  
  if(!myString.equals(""))  
  {
    Serial.println("input value: "+myString); 
    
    myString = "";  
  }

}
