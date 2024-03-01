package de.cidaas.quarkus.extension;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RegisterRestClient
public interface CidaasClient {
	@POST
	@Path("/token-srv/introspect")
	Response callIntrospection(TokenIntrospectionRequest payload);
}
