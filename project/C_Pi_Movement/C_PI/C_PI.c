#include <stdio.h>
#include <string.h>
#include <ctype.h>

#include "pico/stdlib.h"
#include "pico/cyw43_arch.h"
#include "pico/unique_id.h"

#include "lwip/apps/mqtt.h"
#include "lwip/ip_addr.h"
#include "lwip/netif.h"

#include "WLANConf.h"

// -------------------- App Settings --------------------
#ifndef DEVICE_NAME
#define DEVICE_NAME "Koje 1"
#endif

#define MQTT_PORT            1883
#define MQTT_KEEP_ALIVE_S    60

#define MQTT_PUBLISH_QOS     1
#define MQTT_PUBLISH_RETAIN  1   // retain last state

#define SEND_COUNT           10
#define SEND_INTERVAL_MS     10000

// Motion Sensor Pin
#define MOTION_SENSOR_PIN 18

// Unique ID string length
#define UNIQUE_ID_STR_LEN (PICO_UNIQUE_BOARD_ID_SIZE_BYTES * 2 + 1)

// -------------------- Logging --------------------
#ifndef INFO_printf
#define INFO_printf printf
#endif

#ifndef ERROR_printf
#define ERROR_printf printf
#endif

// -------------------- State --------------------
typedef struct {
    mqtt_client_t *mqtt_client_inst;
    struct mqtt_connect_client_info_t mqtt_client_info;
    ip_addr_t mqtt_server_address;
    bool connect_done;
} MQTT_CLIENT_DATA_T;

// -------------------- MQTT helpers --------------------
static void pub_request_cb(__unused void *arg, err_t err) {
    if (err != ERR_OK) {
        ERROR_printf("MQTT publish failed: %d\n", (int)err);
    }
}

static void publish_status(MQTT_CLIENT_DATA_T *state, bool status) {
    // LED reflects status
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, status);

    char payload[128];
    snprintf(payload, sizeof(payload),
             "{\"name\":\"%s\",\"status\":%s}",
             DEVICE_NAME,
             status ? "true" : "false");

    mqtt_publish(state->mqtt_client_inst,
                 "pico-data",
                 payload,
                 (u16_t)strlen(payload),
                 MQTT_PUBLISH_QOS,
                 MQTT_PUBLISH_RETAIN,
                 pub_request_cb,
                 state);
}

// -------------------- MQTT connect --------------------
static void mqtt_connection_cb(mqtt_client_t *client, void *arg,
                               mqtt_connection_status_t status) {
    (void)client;
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;

    INFO_printf("MQTT status=%d\n", (int)status);

    if (status == MQTT_CONNECT_ACCEPTED) {
        state->connect_done = true;
        INFO_printf("MQTT connected (client_id=%s)\n",
                    state->mqtt_client_info.client_id);
    } else {
        ERROR_printf("MQTT connect failed\n");
    }
}

// -------------------- Start MQTT --------------------
static void start_client(MQTT_CLIENT_DATA_T *state) {
    state->mqtt_client_inst = mqtt_client_new();
    if (!state->mqtt_client_inst) {
        ERROR_printf("MQTT client creation failed\n");
        return;
    }

    INFO_printf("Connecting MQTT to %s:%d\n",
                ipaddr_ntoa(&state->mqtt_server_address), MQTT_PORT);

    cyw43_arch_lwip_begin();
    err_t err = mqtt_client_connect(state->mqtt_client_inst,
                                   &state->mqtt_server_address,
                                   MQTT_PORT,
                                   mqtt_connection_cb,
                                   state,
                                   &state->mqtt_client_info);
    cyw43_arch_lwip_end();

    if (err != ERR_OK) {
        ERROR_printf("mqtt_client_connect error: %d\n", (int)err);
    }
}

// -------------------- Board init --------------------
static int pico_init_board_peripherals(void) {
    stdio_init_all();

    if (cyw43_arch_init() != 0) {
        ERROR_printf("cyw43 init failed\n");
        return PICO_ERROR_CONNECT_FAILED;
    }
    gpio_init(MOTION_SENSOR_PIN);
    gpio_set_dir(MOTION_SENSOR_PIN, GPIO_IN);

    return PICO_OK;
}

// -------------------- Main --------------------
int main() {
    sleep_ms(1500);

    hard_assert(pico_init_board_peripherals() == PICO_OK);

    static MQTT_CLIENT_DATA_T state;
    memset(&state, 0, sizeof(state));

    // ---- Client ID: DEVICE_NAME + unique ID
    char unique_id_buf[UNIQUE_ID_STR_LEN];
    pico_get_unique_board_id_string(unique_id_buf, sizeof(unique_id_buf));
    for (int i = 0; unique_id_buf[i]; i++)
        unique_id_buf[i] = (char)tolower((unsigned char)unique_id_buf[i]);

    static char client_id[96];
    snprintf(client_id, sizeof(client_id),
             "%s_%s", DEVICE_NAME, unique_id_buf);

    state.mqtt_client_info.client_id  = client_id;
    state.mqtt_client_info.keep_alive = MQTT_KEEP_ALIVE_S;

#if defined(MQTT_USERNAMEV) && defined(MQTT_PASSWORDV)
    state.mqtt_client_info.client_user = MQTT_USERNAMEV;
    state.mqtt_client_info.client_pass = MQTT_PASSWORDV;
#endif

    cyw43_arch_enable_sta_mode();
    cyw43_arch_wifi_connect_timeout_ms(
        WIFI_NAME,
        WIFI_PASSWORDV,
        CYW43_AUTH_WPA2_AES_PSK,
        30000
    );

    sleep_ms(500);
    INFO_printf("WiFi IP: %s\n",
                ipaddr_ntoa(&(netif_list->ip_addr)));

    ipaddr_aton(MQTT_SERVERV, &state.mqtt_server_address);
    start_client(&state);

    while (!state.connect_done) {
        cyw43_arch_poll();
        sleep_ms(100);
    }

    for (int i = 0; i < SEND_COUNT; i++) {
        cyw43_arch_poll();

        publish_status(&state, !gpio_get(MOTION_SENSOR_PIN));
        INFO_printf("Sent status=false (%d/%d)\n", i + 1, SEND_COUNT);

        for (int t = 0; t < SEND_INTERVAL_MS / 100; t++) {
            cyw43_arch_poll();
            sleep_ms(100);
        }
    }

    INFO_printf("Finished sending messages\n");

    cyw43_arch_deinit();
    return 0;
}