package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.CidaasClient;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class CidaasService {
	
	@Inject
	@RestClient
	CidaasClient cidaasClient;
		
	public boolean introspectToken(TokenIntrospectionRequest tokenIntrospectionRequest) {
		Response response = cidaasClient.callIntrospection(tokenIntrospectionRequest);
		if (response == null) {
			return false;
		}
		JsonObject output = response.readEntity(JsonObject.class);
		return output.getBoolean("active");
	}
}
