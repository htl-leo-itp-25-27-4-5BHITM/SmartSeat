package at.htl.boundary;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class SeatResourceTest {

    @Test
    void getAllSeats() {
        when()
                .get("/api/seat/getAllSeats")
                .then()
                .statusCode(200)
                .body("size()", is(5))
                .body("id", everyItem(notNullValue()))
                .body("name", hasItems("Koje 1", "Koje 5"))
                .body("find { it.name == 'Koje 1'}.floor", is("1OG"))
                .body("find { it.name == 'Koje 5'}.floor", is("2OG"))
                .body("mapX", everyItem(allOf(greaterThanOrEqualTo(0.0f), lessThanOrEqualTo(1.0f))))
                .body("mapY", everyItem(allOf(greaterThanOrEqualTo(0.0f), lessThanOrEqualTo(1.0f))))
                .body("state", everyItem(anyOf(is("FREE"), is("OCCUPIED"))));
    }

    @Test
    void getConfiguredFloors() {
        when().get("/api/seat/getFloors")
                .then().statusCode(200)
                .body("$", contains("1OG", "2OG"));
    }

    @Test
    void occupiedSinceIsAnAbsoluteInstantIndependentOfJvmTimezone() {
        ensureSeatOneIsFree();

        given().redirects().follow(false)
                .when().get("/api/seat/changeStatus/1")
                .then().statusCode(307);

        String timestamp = when().get("/api/seat/getAllSeats")
                .then().statusCode(200)
                .extract().jsonPath().getString("find { it.id == 1 }.occupiedSince");

        assertTrue(timestamp.endsWith("Z") || timestamp.matches(".*[+-]\\d{2}:\\d{2}$"));
        TimeZone original = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Honolulu"));
            Instant honolulu = Instant.parse(timestamp);
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
            Instant tokyo = Instant.parse(timestamp);
            assertEquals(honolulu, tokyo);
        } finally {
            TimeZone.setDefault(original);
            given().redirects().follow(false)
                    .when().get("/api/seat/changeStatus/1")
                    .then().statusCode(307);
        }
    }

    @Test
    void renameSupportsNoOpUniqueDuplicateBlankAndUnknownRequests() {
        given().contentType("application/json")
                .body("{\"id\":1,\"name\":\"  Koje 1  \"}")
                .when().post("/api/dashboard/rename")
                .then().statusCode(200)
                .body("find { it.id == 1 }.name", is("Koje 1"));

        given().contentType("application/json")
                .body("{\"id\":1,\"name\":\"Fensterplatz\"}")
                .when().post("/api/dashboard/rename")
                .then().statusCode(200)
                .body("find { it.id == 1 }.name", is("Fensterplatz"));

        given().contentType("application/json")
                .body("{\"id\":1,\"name\":\"Koje 2\"}")
                .when().post("/api/dashboard/rename")
                .then().statusCode(409);

        given().contentType("application/json")
                .body("{\"id\":1,\"name\":\"   \"}")
                .when().post("/api/dashboard/rename")
                .then().statusCode(400);

        given().contentType("application/json")
                .body("{\"id\":999,\"name\":\"Unbekannt\"}")
                .when().post("/api/dashboard/rename")
                .then().statusCode(404);

        given().contentType("application/json")
                .body("{\"id\":1,\"name\":\"Koje 1\"}")
                .when().post("/api/dashboard/rename")
                .then().statusCode(200);
    }

    private void ensureSeatOneIsFree() {
        boolean free = when().get("/api/seat/getAllSeats")
                .then().statusCode(200)
                .extract().jsonPath().getBoolean("find { it.id == 1 }.status");
        if (!free) {
            given().redirects().follow(false)
                    .when().get("/api/seat/changeStatus/1")
                    .then().statusCode(307);
        }

    }
}
