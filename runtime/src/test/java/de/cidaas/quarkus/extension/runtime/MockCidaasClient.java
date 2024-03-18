package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.token.validation.MockService;
import de.cidaas.quarkus.extension.token.validation.TokenValidationRequest;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@Alternative()
@Priority(1)
@ApplicationScoped
@RestClient
public class MockCidaasClient implements CidaasClient {

	@Inject
	MockService mockService;

	@Override
	public Response getJwks() {
		JsonObject jwks = mockService.createJwks();
		return Response.ok(jwks).build();
	}

	@Override
	public Response callValidateToken(TokenValidationRequest payload) {
		return null;
	}

}
