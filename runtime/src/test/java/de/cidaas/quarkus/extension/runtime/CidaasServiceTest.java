package de.cidaas.quarkus.extension.runtime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

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
	public void testCallIntrospectTokenWithNull() {
		cidaasService.introspectToken(null);
		verify(cidaasClient, times(1)).callIntrospection(null);
	}
	
	@Test
	public void testCallIntrospectTokenWithRequest() {
		TokenIntrospectionRequest request = mockService.createIntrospectionRequest();
		cidaasService.introspectToken(request);
		verify(cidaasClient, times(1)).callIntrospection(request);
	}
}
