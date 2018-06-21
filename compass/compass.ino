#include <Wire.h> //I2C Arduino Library
#include <I2Cdev.h>
#include <HMC5883L.h>
#define address 0x1E //0011110b, I2C 7bit address of HMC5883

HMC5883L mag;
int16_t mx, my, mz; 

void setup(){
  //Initialize Serial and I2C communications
  Wire.begin();
  Serial.begin(9600);
  
  //Put the HMC5883 IC into the correct operating mode
  Wire.beginTransmission(address); //open communication with HMC5883
  Wire.write(0x02); //select mode register
  Wire.write(0x00); //continuous measurement mode
  Wire.endTransmission();
  mag.initialize();
}

void loop(){
  mag.getHeading(&mx, &my, &mz);
  float heading = atan2(my, mx);
  if(heading < 0)
  heading += 2 * M_PI;
  float  heading2 = heading * 180/M_PI;
  //Serial.print("heading:\t");
  //Serial.println((int)heading2);

  return heading2;
}
