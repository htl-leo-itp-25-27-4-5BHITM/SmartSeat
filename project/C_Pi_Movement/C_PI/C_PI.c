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

#define MQTT_PORT 1883
#define MQTT_KEEP_ALIVE_S 60

#define MQTT_PUBLISH_QOS 1
#define MQTT_PUBLISH_RETAIN 1

#define TOPIC_STATUS "pico-data"

#define MOTION_SENSOR_PIN 18
#define SEND_INTERVAL_MS 500

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
typedef struct
{
    mqtt_client_t *mqtt_client_inst;
    struct mqtt_connect_client_info_t mqtt_client_info;
    ip_addr_t mqtt_server_address;
    bool connect_done;
} MQTT_CLIENT_DATA_T;

// -------------------- MQTT helpers --------------------
static void pub_request_cb(__unused void *arg, err_t err)
{
    if (err != ERR_OK)
    {
        ERROR_printf("MQTT publish failed: %d\n", (int)err);
    }
}

static void publish_status(MQTT_CLIENT_DATA_T *state, bool motion)
{
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, motion);

    char payload[128];
    snprintf(payload, sizeof(payload),
             "{\"name\":\"%s\",\"status\":%s}",
             DEVICE_NAME,
             !motion ? "true" : "false");

    cyw43_arch_lwip_begin();
    err_t err = mqtt_publish(state->mqtt_client_inst,
                             TOPIC_STATUS,
                             payload,
                             (u16_t)strlen(payload),
                             MQTT_PUBLISH_QOS,
                             MQTT_PUBLISH_RETAIN,
                             pub_request_cb,
                             state);
    cyw43_arch_lwip_end();

    if (err != ERR_OK)
    {
        ERROR_printf("mqtt_publish error: %d\n", (int)err);
    }
}

// -------------------- MQTT connect --------------------
static void mqtt_connection_cb(mqtt_client_t *client, void *arg,
                               mqtt_connection_status_t status)
{
    (void)client;
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;

    INFO_printf("MQTT status=%d\n", (int)status);

    if (status == MQTT_CONNECT_ACCEPTED)
    {
        state->connect_done = true;
        INFO_printf("MQTT connected (client_id=%s)\n",
                    state->mqtt_client_info.client_id);
    }
    else
    {
        state->connect_done = false;
        ERROR_printf("MQTT connect failed\n");
    }
}

// -------------------- Start MQTT --------------------
static bool start_client(MQTT_CLIENT_DATA_T *state)
{
    state->mqtt_client_inst = mqtt_client_new();
    if (!state->mqtt_client_inst)
    {
        ERROR_printf("MQTT client creation failed\n");
        return false;
    }

    INFO_printf("Connecting MQTT to %s:%d\n",
                ipaddr_ntoa(&state->mqtt_server_address), MQTT_PORT);

    state->connect_done = false;

    cyw43_arch_lwip_begin();
    err_t err = mqtt_client_connect(state->mqtt_client_inst,
                                    &state->mqtt_server_address,
                                    MQTT_PORT,
                                    mqtt_connection_cb,
                                    state,
                                    &state->mqtt_client_info);
    cyw43_arch_lwip_end();

    if (err != ERR_OK)
    {
        ERROR_printf("mqtt_client_connect error: %d\n", (int)err);
        return false;
    }
    return true;
}

// -------------------- Board init --------------------
static int pico_init_board_peripherals(void)
{
    stdio_init_all();

    gpio_init(MOTION_SENSOR_PIN);
    gpio_set_dir(MOTION_SENSOR_PIN, GPIO_IN);

    gpio_pull_down(MOTION_SENSOR_PIN);

    if (cyw43_arch_init() != 0)
    {
        ERROR_printf("cyw43 init failed\n");
        return PICO_ERROR_CONNECT_FAILED;
    }

    return PICO_OK;
}

static void poll_sleep_ms(uint32_t ms)
{
    for (uint32_t t = 0; t < ms / 100; t++)
    {
        cyw43_arch_poll();
        sleep_ms(100);
    }
}

// -------------------- Main --------------------
int main()
{
    sleep_ms(1500);

    hard_assert(pico_init_board_peripherals() == PICO_OK);

    static MQTT_CLIENT_DATA_T state;
    memset(&state, 0, sizeof(state));

    char unique_id_buf[UNIQUE_ID_STR_LEN];
    pico_get_unique_board_id_string(unique_id_buf, sizeof(unique_id_buf));
    for (int i = 0; unique_id_buf[i]; i++)
        unique_id_buf[i] = (char)tolower((unsigned char)unique_id_buf[i]);

    static char client_id[96];
    snprintf(client_id, sizeof(client_id), "%s_%s", DEVICE_NAME, unique_id_buf);

    state.mqtt_client_info.client_id = client_id;
    state.mqtt_client_info.keep_alive = MQTT_KEEP_ALIVE_S;

#if defined(MQTT_USERNAMEV) && defined(MQTT_PASSWORDV)
    state.mqtt_client_info.client_user = MQTT_USERNAMEV;
    state.mqtt_client_info.client_pass = MQTT_PASSWORDV;
#endif

    cyw43_arch_enable_sta_mode();
    int w = cyw43_arch_wifi_connect_timeout_ms(
        WIFI_NAME,
        WIFI_PASSWORDV,
        CYW43_AUTH_WPA2_AES_PSK,
        30000);
    if (w != 0)
    {
        ERROR_printf("WiFi connect failed: %d\n", w);
        cyw43_arch_deinit();
        return 1;
    }

    sleep_ms(500);
    INFO_printf("WiFi IP: %s\n", ipaddr_ntoa(&(netif_list->ip_addr)));

    if (!ipaddr_aton(MQTT_SERVERV, &state.mqtt_server_address))
    {
        ERROR_printf("Invalid MQTT_SERVERV: %s\n", MQTT_SERVERV);
        cyw43_arch_deinit();
        return 1;
    }

    if (!start_client(&state))
    {
        ERROR_printf("Failed to start MQTT client\n");
        cyw43_arch_deinit();
        return 1;
    }

    while (!state.connect_done)
    {
        cyw43_arch_poll();
        sleep_ms(100);
    }

    INFO_printf("Publishing motion status every %d ms on topic '%s'\n",
                SEND_INTERVAL_MS, TOPIC_STATUS);

    bool last_motion = false;
    bool first_run = true;

    while (true)
    {
        cyw43_arch_poll();

        bool motion = gpio_get(MOTION_SENSOR_PIN) ? true : false;

        if (first_run || motion != last_motion)
        {
            publish_status(&state, motion);
            INFO_printf("Motion changed -> %s (published)\n", !motion ? "true" : "false");

            last_motion = motion;
            first_run = false;
        }

        poll_sleep_ms(SEND_INTERVAL_MS);
    }
}