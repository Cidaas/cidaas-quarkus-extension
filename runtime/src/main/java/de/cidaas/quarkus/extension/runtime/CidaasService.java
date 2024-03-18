package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.token.validation.ValidationService;
import de.cidaas.quarkus.extension.token.validation.TokenValidationRequest;
import de.cidaas.quarkus.extension.token.validation.TokenValidationException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class CidaasService implements ValidationService {

	@Inject
	@RestClient
	CidaasClient cidaasClient;

	@Override
	public boolean validateToken(TokenValidationRequest tokenValidationRequest) {
		Response response = cidaasClient.callValidateToken(tokenValidationRequest);
		if (response == null) {
			throw new TokenValidationException("response of callValidateToken is null!");
		}
		JsonObject output = response.readEntity(JsonObject.class);
		return output.getBoolean("active");
	}
}
