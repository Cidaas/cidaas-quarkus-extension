package de.cidaas.quarkus.extension.runtime;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.CidaasClient;
import de.cidaas.quarkus.extension.Group;
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
	
	public boolean introspectToken(String accessToken, List<String> roles, List<Group> groups, List<String> scopes) {
		TokenIntrospectionRequest request = new TokenIntrospectionRequest();
		request.setToken(accessToken);
		request.setRoles(roles);
		request.setGroups(groups);
		request.setScopes(scopes);
		
		Response response = cidaasClient.callIntrospection(request);
		JsonObject output = response.readEntity(JsonObject.class);
		return output.getBoolean("active");
	}
}
