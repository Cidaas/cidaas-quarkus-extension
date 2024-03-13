package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.CidaasClient;
import de.cidaas.quarkus.extension.CidaasQuarkusException;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class CidaasService implements IntrospectionService {
	
	@Inject
	@RestClient
	CidaasClient cidaasClient;
		
	@Override
	public boolean introspectToken(TokenIntrospectionRequest tokenIntrospectionRequest) {
		Response response = cidaasClient.callIntrospection(tokenIntrospectionRequest);
		if (response == null) {
			throw new CidaasQuarkusException("response of callIntrospection is null!");
		}
		JsonObject output = response.readEntity(JsonObject.class);
		return output.getBoolean("active");
	}
}
