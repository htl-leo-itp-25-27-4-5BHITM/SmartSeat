#include <stdio.h>
#include "pico/stdlib.h"
#include "pico/cyw43_arch.h"

#ifndef MOTION_SENSOR_PIN
#define MOTION_SENSOR_PIN 18
#endif

int pico_init_board_peripherals(void) {
    stdio_init_all();

    if (cyw43_arch_init()) {
        printf("Could not connect to the wifi %n");
        return PICO_ERROR_CONNECT_FAILED;
    }

    gpio_init(MOTION_SENSOR_PIN);
    gpio_set_dir(MOTION_SENSOR_PIN, GPIO_IN);
    return PICO_OK;
}

void change_led_status(bool ledOn) {
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, ledOn);
}

int main() {
    int rc = pico_init_board_peripherals();
    hard_assert(rc == PICO_OK);
    printf("Starting Pico \n");
    // INIT BLINK
    for (int i = 0; i < 5; i++){
        change_led_status(true);
        sleep_ms(250);
        change_led_status(false);
        sleep_ms(250);
    }
    while (true) {

        if (gpio_get(MOTION_SENSOR_PIN) == 1 ) {
            printf("Koje besetzt: %d \n", gpio_get(MOTION_SENSOR_PIN));
            change_led_status(true);
            sleep_ms(500);
        }
        else {
            change_led_status(false);
        }

        sleep_ms(1);
    }
}
