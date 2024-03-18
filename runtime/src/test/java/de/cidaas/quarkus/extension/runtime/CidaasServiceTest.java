package de.cidaas.quarkus.extension.runtime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
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
	public void testCallIntrospectTokenWithRequest() {
		JsonObject body = Json.createObjectBuilder().add("active", true).build();
		Response response = Response.ok(body).build();
		TokenIntrospectionRequest request = mockService.createIntrospectionRequest();
		when(cidaasClient.callIntrospection(request)).thenReturn(response);
		assertTrue(cidaasService.introspectToken(request));
		verify(cidaasClient, times(1)).callIntrospection(request);
	}
}
