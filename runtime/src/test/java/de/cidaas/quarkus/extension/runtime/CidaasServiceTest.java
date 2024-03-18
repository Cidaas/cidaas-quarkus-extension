package de.cidaas.quarkus.extension.runtime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import de.cidaas.quarkus.extension.token.validation.MockService;
import de.cidaas.quarkus.extension.token.validation.TokenValidationRequest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@QuarkusTest
public class CidaasServiceTest {
	@InjectMock
	@RestClient
	MockCidaasClient cidaasClient;

	@Inject
	CidaasService cidaasService;

	@Inject
	MockService mockService;

	@Test
	public void testCallValidateTokenWithRequest() {
		JsonObject body = Json.createObjectBuilder().add("active", true).build();
		Response response = Response.ok(body).build();
		TokenValidationRequest request = mockService.createValidationRequest();
		when(cidaasClient.callValidateToken(request)).thenReturn(response);
		assertTrue(cidaasService.validateToken(request));
		verify(cidaasClient, times(1)).callValidateToken(request);
	}
}
