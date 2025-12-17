#include <stdio.h>
#include "pico/stdlib.h"

// #define MOTION_LED 16;
// #define MOTION_SENSOR 17
const uint MOTION_LED_PIN = 16;
const uint MOTION_SENSOR_Pin = 17;

int main() {
    // stdio_init_all();
    //INIT pins
    gpio_init(MOTION_LED_PIN);
    gpio_init(MOTION_SENSOR_Pin);

    gpio_set_dir(MOTION_LED_PIN, GPIO_OUT);
    gpio_set_dir(MOTION_SENSOR_Pin,GPIO_IN);    


    while (true) {
        if (gpio_get(MOTION_SENSOR_Pin)) {
            gpio_put(MOTION_LED_PIN, true);
            printf("Motion sensor: %d %n Seat is occupied ", gpio_get(MOTION_SENSOR_Pin));

        } else  {
            gpio_put(MOTION_LED_PIN,false);
        } 
        sleep_ms(250);



     
    }
}

