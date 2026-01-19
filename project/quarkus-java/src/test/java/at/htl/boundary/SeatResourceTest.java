package at.htl.boundary;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SeatResourceTest {

    @Test
    void getAllSeats() {
        when()
                .get("/api/seat/getAllSeats")
                .then()
                .statusCode(200)
                .body("find { it.name == 'Koje 5'}.status", is(true));

    }
}