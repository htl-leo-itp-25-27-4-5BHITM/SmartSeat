import network
import utime
from machine import Pin
from umqtt.simple import MQTTClient
from secrets import  WIFI_SSID, WIFI_PASSWORD, MQTT_SERVER

# WIFI config over env

# MQTT config

# mqtt_server = get_env('MQTT_ADRESS');
mqtt_server = MQTT_SERVER;

client_id = 'Koje 1';
topic_pub = b'pico-data';

wlan = network.WLAN(network.STA_IF);
wlan.active();
wlan.connect(WIFI_SSID, WIFI_PASSWORD);
print(wlan.active())
 
# Wait for connect or fail
max_wait = 10
while max_wait > 0:
    if wlan.status() < 0 or wlan.status() >= 3:
        break
    max_wait -= 1
    print('waiting for connection...')
    utime.sleep_ms(100)
 
# Handle connection error
if wlan.status() != 3:
    raise RuntimeError('network connection failed')
else:
    print('connected')
    status = wlan.ifconfig()
    print( 'ip = ' + status[0] 

utime.sleep_ms(200);
print(wlan.isconnected());

# Define PINS
motion_sensor = Pin(18, Pin.IN);
led_pin = Pin("LED", Pin.OUT);



# Switch LED Power
def ledOnOff (ledOn):
    
    led_pin.on() if ledOn else led_pin.off();
    pico_msg = b"{" + f"\"name\": \"Koje1\", \"status\": {ledOn}" + "}";
    client.publish(topic_pub,pico_msg);
    time.sleep(3);


# Connect
def mqtt_connect():
    client = MQTTClient(client_id, mqtt_server, keepalive=3600);
    client.connect();
    
    print ("Connected to %s Mqtt Broker" %(mqtt_server));
    return client;


# Reconnect
def mqtt_reconnect ():
    print('Failed to connect to the MQTT Broker. Reconnecting...');
    time.sleep(5);
    machine.reset();



try:
    client = mqtt.connect();
except OSError as e:
    mqtt_reconnect();


while True:
    if motion_sensor.value():
        ledOnOff(True);
    
    else:
        ledOnOff(False);
        pass;