#!/usr/bin/python
import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO
import time

client = mqtt.Client("dHC04_mqtt") # MQTT client nesnesi
client.connect("test.mosquitto.org", 1883); #Baglanti
client.subscribe("dHC04_mqtt"); #Abone olma
try:
	GPIO.setmode(GPIO.BOARD)

	PIN_TRIGGER = 7
	PIN_ECHO = 11

	GPIO.setup(PIN_TRIGGER, GPIO.OUT)
	GPIO.setup(PIN_ECHO, GPIO.IN)

	GPIO.output(PIN_TRIGGER, GPIO.LOW)

	print("Waiting for sensor to settle");

	time.sleep(2)

	print("Calculating distance")
	while True:
		GPIO.output(PIN_TRIGGER, GPIO.HIGH)

		time.sleep(0.00001)

		GPIO.output(PIN_TRIGGER, GPIO.LOW)
		pulse_start_time,pulse_end_time = 0,0;
	
		while GPIO.input(PIN_ECHO)==0:
            		pulse_start_time = time.time()
		while GPIO.input(PIN_ECHO)==1:
            		pulse_end_time = time.time()

		pulse_duration = pulse_end_time - pulse_start_time
		distance = round(pulse_duration * 17150, 2);
		print ("Distance:",distance,"cm");

		client.publish("Distance",distance);
		time.sleep(1);


except KeyboardInterrupt:  
    # CTRL+C icin
    print ("Kullanici istegiyle sonlandirilmistir\n");
except Exception as e:  
	print("Bir hata meydana geldi.\n");
	print(e);
finally:
      GPIO.cleanup()

