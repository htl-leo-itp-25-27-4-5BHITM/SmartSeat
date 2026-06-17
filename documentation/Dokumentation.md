# **Smartseat Gebrauchsanweisungs**

## MQTT und der Sensor

Damit wir wissen welche Kojen besetzt sind, verweden wir einen Pico 2W mit einem Bewegungssensor.

### Entwicklung 

Für Entwicklungszwecken kann man die MQTT-Eingaben, durch Programme wie MQTT-Explorer simulieren, dabei verwenden wir den MQTT Server vom Herrn Professor Stützt.

![Mqttexplorer Anbindung](/documentation/Dokumentationsbilder/MQTT/MQTTExplorerConfig.png)

Als nächstes muss das neue Topic `pico-data` erstellt werden. Danach muss man unten JSON auswählen. Dieses JSON besitzt einen `id` und `status` Parameter, wobei der `id` Parameter für genau eine Koje da ist und der `status` Parameter die Statusänderung angebit, dabei kann man die genaue Struktur unten auslesen. 

![Mqtt Payload](/documentation/Dokumentationsbilder/MQTT/MQTT_Payload.png)

### Normale Verwendung 

#### Pico 2W

Im Echtbetrieb brauchen die Sensoren eine USB-Schnittstelle die 5V liefert, da das Programm direkt startet sobald der Pico mit Strom beliefert wird, und einen Router, wodurch sich der Pico mit dem Internet verbinden kann. Wir verweden unser eigenkonfiguriertes WLAN namens Smartseat, wofür wir unseren Router verweden. Man sollte den Pico erst einschalten sobalt der Router `blau` und nicht mehr `rot` leuchtet. 

Damit man aber nachschauen kann was der Pico wirklich macht, kann man ihn an einem PC, über USB, anschließen. Doch das man die Ausgabe sehen kann braucht man ein paar Sachen: 

1. Visual Studio Code, bzw ein Terminal 
2. Serial Monitor Erweiterung 

Sobald der Pico angeschlossen ist muss man bei dem Serial Monitor COM5, bzw serielles USB-Gerät auswählen und auf das Play Symbol klicken. Nun kann sehen was er Pico macht. Solang er nicht irgeneinen Fehler wirft, befindet er sich im Normalbetrieb. 

#### Sensor 

Das der Sensor nicht kaputt geht, muss man diesen, mit dem Pico, richtig verkabeln. Es gibt dadbei 3 Kabel; VCC (Spannung), Ground und die Ausgabe.

1. VCC (Sensor) auf VSYS (Pico) 
2. GND (Sensor) auf GND (Pico)
3. SIG (Sensor) auf GP18 

Hier unten sieht man welcher Pin, welcher ist.

![Pico PINS](/documentation/Dokumentationsbilder/Pico/pinout.png)

