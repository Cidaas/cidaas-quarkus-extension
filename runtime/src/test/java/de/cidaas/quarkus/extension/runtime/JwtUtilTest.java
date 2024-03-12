package de.cidaas.quarkus.extension.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;

@QuarkusTest
public class JwtUtilTest {
	
	@Inject
	MockService mockService;
	
	@Test
	public void testDecodeHeader() {
		JsonObject header = JwtUtil.decodeHeader(mockService.getToken());
		assertEquals(header.getString("alg"), "HS256");
	}
	
	@Test
	public void testDecodePayload() {
		JsonObject payload = JwtUtil.decodePayload(mockService.getToken());
		assertEquals(payload.getString("role"), "USER");
		assertEquals(payload.getString("iss"), "Issuer");
	}
}
