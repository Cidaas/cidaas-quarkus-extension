package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import de.cidaas.quarkus.extension.token.validation.TokenValidationRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RegisterRestClient
public interface CidaasClient {
	@GET
	@Path("/.well-known/jwks.json")
	Response getJwks();

	@POST
	@Path("/token-srv/introspect")
	Response callValidateToken(TokenValidationRequest payload);
}
