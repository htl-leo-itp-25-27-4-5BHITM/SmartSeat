#include <stdio.h>
#include <string.h>
#include "pico/stdlib.h"
#include "pico/cyw43_arch.h"
#include "pico/unique_id.h"
#include "hardware/gpio.h"
#include "hardware/irq.h"
#include "hardware/adc.h"
#include "lwip/apps/mqtt.h"
#include "lwip/apps/mqtt_priv.h" // needed to set hostname
#include "lwip/dns.h"
#include "lwip/altcp_tls.h"
#include "MqttConf.h"

// HARDWARE SETTINGS
#ifndef MOTION_SENSOR_PIN
#define MOTION_SENSOR_PIN 18
#endif

// MQTT from the Rasperry PI github

#ifdef MQTT_CERT_INC
#include MQTT_CERT_INC
#endif

#ifndef MQTT_TOPIC_LEN
#define MQTT_TOPIC_LEN 100
#endif

typedef struct
{
    mqtt_client_t *mqtt_client_inst;
    struct mqtt_connect_client_info_t mqtt_client_info;
    char data[MQTT_OUTPUT_RINGBUF_SIZE];
    char topic[MQTT_TOPIC_LEN];
    uint32_t len;
    ip_addr_t mqtt_server_address;
    bool connect_done;
    int subscribe_count;
    bool stop_client;
} MQTT_CLIENT_DATA_T;

#ifndef DEBUG_printf
#ifndef NDEBUG
#define DEBUG_printf printf
#else
#define DEBUG_printf(...)
#endif
#endif

#ifndef INFO_printf
#define INFO_printf printf
#endif

#ifndef ERROR_printf
#define ERROR_printf printf
#endif

#define MOTION_WORKER_TIME_10
#define MQTT_KEEP_ALIVE_S 60

#define MQTT_SUBSCRIBE_QOS 1
#define MQTT_PUBLISH_QOS 1
#define MQTT_PUBLISH_RETAIN 0

#define MQTT_WILL_TOPIC "/pico-data"
#define MQTT_WILL_MSG "0"
#define MQTT_WILL_QOS 1

#ifndef MQTT_DEVICE_NAME
#define MQTT_DEVICE_NAME "Koje 1"
#endif

#ifndef MQTT_UNIQUE_TOPIC
#define MQTT_UNIQUE_TOPIC 1
#endif

static void pub_request_cb(__unused void *arg, err_t err) {
    if (err != 0)
    {
        ERROR_printf("pub_request_cb failed %d", err);
    }
}

static const char *full_topic(MQTT_CLIENT_DATA_T *state, const char *name) {
#if MQTT_UNIQUE_TOPIC
    static char full_topic[MQTT_TOPIC_LEN];
    snprintf(full_topic, sizeof(full_topic), "/%s%s", state->mqtt_client_info.client_id, name);
    return full_topic;
#else
    return name;
#endif
}

static void sub_request_cb(void *arg, err_t err) {
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;
    if (err != 0)
    {
        INFO_printf("subscribe request failed %d \n", err);
    }
    state->subscribe_count++;
}

static void unsub_request_cb(void *arg, err_t err) {
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;
    if (err != 0)
    {
        INFO_printf("unsubscribe request failed %d \n", err);
    }
    state->subscribe_count--;
    assert(state->subscribe_count >= 0);

    // Stop if requested
    if (state->subscribe_count <= 0 && state->stop_client)
    {
        mqtt_disconnect(state->mqtt_client_inst);
    }
}
static void sub_unsub_topics(MQTT_CLIENT_DATA_T *state, bool sub) {
    mqtt_request_cb_t cb = sub ? sub_request_cb : unsub_request_cb;
    mqtt_sub_unsub(state->mqtt_client_inst, full_topic(state, "/led"), MQTT_SUBSCRIBE_QOS, cb, state, sub);
    mqtt_sub_unsub(state->mqtt_client_inst, full_topic(state, "/print"), MQTT_SUBSCRIBE_QOS, cb, state, sub);
    mqtt_sub_unsub(state->mqtt_client_inst, full_topic(state, "/ping"), MQTT_SUBSCRIBE_QOS, cb, state, sub);
    mqtt_sub_unsub(state->mqtt_client_inst, full_topic(state, "/exit"), MQTT_SUBSCRIBE_QOS, cb, state, sub);
}
static void mqtt_incoming_data_cb(void *arg, const u8_t *data, u16_t len, u8_t flags) {
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;
#if MQTT_UNIQUE_TOPIC
    const char *basic_topic = state->topic + strlen(state->mqtt_client_info.client_id) + 1;
#else
    const char *basic_topic = state->topic;
#endif
    strncpy(state->data, (const char *)data, len);
    state->len = len;
    state->data[len] = '\0';

    DEBUG_printf("Topic: %s, Message: %s\n", state->topic, state->data);
    // if (strcmp(basic_topic, "/led") == 0)
    // {
    //     if (lwip_stricmp((const char *)state->data, "On") == 0 || strcmp((const char *)state->data, "1") == 0)
    //         control_led(state, true);
    //     else if (lwip_stricmp((const char *)state->data, "Off") == 0 || strcmp((const char *)state->data, "0") == 0)
    //         control_led(state, false);
    // } else if (strcmp(basic_topic, "/print") == 0) {
    //     INFO_printf("%.*s\n", len, data);
    // } else if (strcmp(basic_topic, "/ping") == 0) {
    //     char buf[11];
    //     snprintf(buf, sizeof(buf), "%u", to_ms_since_boot(get_absolute_time()) / 1000);
    //     mqtt_publish(state->mqtt_client_inst, full_topic(state, "/uptime"), buf, strlen(buf), MQTT_PUBLISH_QOS, MQTT_PUBLISH_RETAIN, pub_request_cb, state);
    // } else if (strcmp(basic_topic, "/exit") == 0) {
    //     state->stop_client = true; // stop the client when ALL subscriptions are stopped
    //     sub_unsub_topics(state, false); // unsubscribe
    // }
}
static void mqtt_incoming_publish_cb(void *arg, const char *topic, u32_t tot_len) {
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;
    strncpy(state->topic, topic, sizeof(state->topic));
}
static void mqtt_connection_cb(mqtt_client_t *client, void *arg, mqtt_connection_status_t status) {
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;
    if (status == MQTT_CONNECT_ACCEPTED)
    {
        state->connect_done = true;
        sub_unsub_topics(state, true); // subscribe;

        // indicate online
        if (state->mqtt_client_info.will_topic)
        {
            mqtt_publish(state->mqtt_client_inst, state->mqtt_client_info.will_topic, "1", 1, MQTT_WILL_QOS, true, pub_request_cb, state);
        }

        // temperature_worker.user_data = state;
        // async_context_add_at_time_worker_in_ms(cyw43_arch_async_context(), &temperature_worker, 0);
    }
    else if (status == MQTT_CONNECT_DISCONNECTED)
    {
        if (!state->connect_done)
        {
            INFO_printf("Failed to connect to mqtt server \n");
        }
    }
    else
    {
        INFO_printf("Unexpected status\n");
    }
}
static void start_client(MQTT_CLIENT_DATA_T *state) {
#if LWIP_ALTCP && LWIP_ALTCP_TLS
    const int port = MQTT_TLS_PORT;
    INFO_printf("Using TLS\n");
#else
    const int port = 1883;
    INFO_printf("Warning: Not using TLS\n");
#endif
    state->mqtt_client_inst = mqtt_client_new();
    if (!state->mqtt_client_inst)
    {
        INFO_printf("MQTT client instance creation error \n");
    }
    INFO_printf("IP address of this device %s\n", ipaddr_ntoa(&(netif_list->ip_addr)));
    INFO_printf("Connecting to mqtt server at %s\n", ipaddr_ntoa(&state->mqtt_server_address));

    cyw43_arch_lwip_begin();
    if (mqtt_client_connect(state->mqtt_client_inst, &state->mqtt_server_address, port, mqtt_connection_cb, state, &state->mqtt_client_info) != ERR_OK)
    {
        INFO_printf("MQTT broker connection error \n");
    }
#if LWIP_ALTCP && LWIP_ALTCP_TLS
    // This is important for MBEDTLS_SSL_SERVER_NAME_INDICATION
    mbedtls_ssl_set_hostname(altcp_tls_context(state->mqtt_client_inst->conn), MQTT_SERVER);
#endif
    mqtt_set_inpub_callback(state->mqtt_client_inst, mqtt_incoming_publish_cb, mqtt_incoming_data_cb, state);
    cyw43_arch_lwip_end();
}
static void dns_found(const char *hostname, const ip_addr_t *ipaddr, void *arg) {
    MQTT_CLIENT_DATA_T *state = (MQTT_CLIENT_DATA_T *)arg;
    if (ipaddr)
    {
        state->mqtt_server_address = *ipaddr;
        start_client(state);
    }
    else
    {
        INFO_printf("dns request failed \n");
    }
}

int pico_init_board_peripherals(void) {
    stdio_init_all();

    if (cyw43_arch_init())
    {
        INFO_printf("Could not connect to the wifi\n");
        return PICO_ERROR_CONNECT_FAILED;
    }
    // cyw43_arch_enable_sta_mode();
    // cyw43_arch_disable_ap_mode();

    gpio_init(MOTION_SENSOR_PIN);
    gpio_set_dir(MOTION_SENSOR_PIN, GPIO_IN);

    return PICO_OK;
}

static void change_led_status(MQTT_CLIENT_DATA_T *state, bool ledOn) {
    char message[100] = "{\"Name\":  \"Koje 1\", \"Status\": ";
    char *ledOnValue = ledOn ? "true" : "false";
    char *json3 = "}";

    strcat(message, ledOnValue);
    strcat(message, json3);

    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, ledOn);
    mqtt_publish(state->mqtt_client_inst, full_topic(state, "/pico-date"), message, strlen(message), MQTT_PUBLISH_QOS, MQTT_PUBLISH_RETAIN, pub_request_cb, state);
}

int main() {
    sleep_ms(2000);
    int rc = pico_init_board_peripherals();
    hard_assert(rc == PICO_OK);

    for (int i = 0; i < 5; i++)  {
        printf("PICO 2W is starting \n");
        sleep_ms(250);
        printf("PICO 2W is starting \n");
        sleep_ms(250);
    }

    static MQTT_CLIENT_DATA_T state;

    char unique_id_buf[5];
    pico_get_unique_board_id_string(unique_id_buf, sizeof(unique_id_buf));
    for (int i = 0; i < sizeof(unique_id_buf) - 1; i++) {
        unique_id_buf[i] = tolower(unique_id_buf[i]);
    }

    char client_id_buf[sizeof(MQTT_DEVICE_NAME) + sizeof(unique_id_buf) - 1];
    memcpy(&client_id_buf[0], MQTT_DEVICE_NAME, sizeof(MQTT_DEVICE_NAME) - 1);
    memcpy(&client_id_buf[sizeof(MQTT_DEVICE_NAME) - 1], unique_id_buf, sizeof(unique_id_buf) - 1);
    client_id_buf[sizeof(client_id_buf) - 1] = 0;
    INFO_printf("291 Device name: %s\n", client_id_buf);

    state.mqtt_client_info.client_id = client_id_buf;
    state.mqtt_client_info.keep_alive = MQTT_KEEP_ALIVE_S; // Keep alive in sec

#if defined(MQTT_USERNAME) && defined(MQTT_PASSWORD)
    state.mqtt_client_info.client_user = MQTT_USERNAME;
    state.mqtt_client_info.client_pass = MQTT_PASSWORD;
#else
    state.mqtt_client_info.client_user = NULL;
    state.mqtt_client_info.client_pass = NULL;
#endif
    static char will_topic[MQTT_TOPIC_LEN];
    strncpy(will_topic, full_topic(&state, MQTT_WILL_TOPIC), sizeof(will_topic));
    state.mqtt_client_info.will_topic = will_topic;
    state.mqtt_client_info.will_msg = MQTT_WILL_MSG;
    state.mqtt_client_info.will_qos = MQTT_WILL_QOS;
    state.mqtt_client_info.will_retain = true;
#if LWIP_ALTCP && LWIP_ALTCP_TLS
    // TLS enabled
#ifdef MQTT_CERT_INC
    static const uint8_t ca_cert[] = TLS_ROOT_CERT;
    static const uint8_t client_key[] = TLS_CLIENT_KEY;
    static const uint8_t client_cert[] = TLS_CLIENT_CERT;
    // This confirms the indentity of the server and the client
    state.mqtt_client_info.tls_config = altcp_tls_create_config_client_2wayauth(ca_cert, sizeof(ca_cert),
                                                                                client_key, sizeof(client_key), NULL, 0, client_cert, sizeof(client_cert));
#if ALTCP_MBEDTLS_AUTHMODE != MBEDTLS_SSL_VERIFY_REQUIRED
    WARN_printf("Warning: tls without verification is insecure\n");
#endif
#else
    state->client_info.tls_config = altcp_tls_create_config_client(NULL, 0);
    WARN_printf("Warning: tls without a certificate is insecure\n");
#endif
#endif

    cyw43_arch_enable_sta_mode();
    cyw43_arch_disable_ap_mode();

    INFO_printf("WIFI CREDENTIALS: %s %s %s \n", WIFI_NAME, WIFI_PASSWORDV, MQTT_SERVERV);
    //cyw43_arch_wifi_connect_timeout_ms
    //cyw43_arch_wifi_connect_blocking(WIFI_NAME,WIFI_PASSWORDV, CYW43_AUTH_WPA2_MIXED_PSK)4
    if (cyw43_arch_wifi_connect_timeout_ms(WIFI_NAME, WIFI_PASSWORDV, CYW43_AUTH_WPA2_AES_PSK, 10000) != 0) {
        panic("TIMED OUT! %d \n", cyw43_wifi_link_status(&cyw43_state, CYW43_ITF_STA));
        sleep_ms(500);
    }

        sleep_ms(1000);
        INFO_printf("IP address of this device %s\n", ipaddr_ntoa(&(netif_list->ip_addr)));
        INFO_printf("\n Connected to Wifi: %d \n", cyw43_wifi_link_status(&cyw43_state, CYW43_ITF_STA));

        cyw43_arch_lwip_begin();
        int err = dns_gethostbyname(MQTT_SERVERV, &state.mqtt_server_address, dns_found, &state);
        cyw43_arch_lwip_end();

        if (err == ERR_OK) {
            // We have the address, just start the client
            start_client(&state);
        }
        else if (err != ERR_INPROGRESS) { // ERR_INPROGRESS means expect a callback
            INFO_printf("dns request failed\n");
        }

        while (!state.connect_done || mqtt_client_is_connected(state.mqtt_client_inst)) {
            INFO_printf("Working and checking... \n");
            // if (!state.connect_done || mqtt_client_is_connected(state.mqtt_client_inst)) {
            //     INFO_printf("\nPico is connected to the mqtt client\n");
            //     cyw43_arch_poll();
            // } else {
            //     INFO_printf("\nPico got disconnected\n");
            // }

            if (gpio_get(MOTION_SENSOR_PIN) == 1) {
                INFO_printf("Koje besetzt: %d \n", gpio_get(MOTION_SENSOR_PIN));
                change_led_status(&state, true);
                sleep_ms(500);
            }
            else {
                change_led_status(&state, false);
            }
            cyw43_arch_poll();

            sleep_ms(100);
        }
    

    return 0;
}
