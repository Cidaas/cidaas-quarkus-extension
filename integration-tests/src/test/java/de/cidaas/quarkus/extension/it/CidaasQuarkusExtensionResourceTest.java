package de.cidaas.quarkus.extension.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CidaasQuarkusExtensionResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/cidaas-quarkus-extension")
                .then()
                .statusCode(200)
                .body(is("Hello cidaas-quarkus-extension"));
    }
}
