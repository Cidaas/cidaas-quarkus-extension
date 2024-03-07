package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.CidaasClient;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class CidaasService {
	
	@Inject
	@RestClient
	CidaasClient cidaasClient;
	
	void onStart(@Observes StartupEvent ev) {          
		getJwks();
    }
	
	@CacheResult(cacheName = "jwk-cache")
	JsonObject getJwks() {
		Response response = cidaasClient.getJwks();
		return response.readEntity(JsonObject.class);
	}
	
	@CacheInvalidate(cacheName = "jwk-cache")
	void clearJwkCache() {}
	
	@Scheduled(every = "86400s") // 1 day
	void refreshJwks() {
		clearJwkCache();
		getJwks();
	}
		
	public boolean introspectToken(TokenIntrospectionRequest tokenIntrospectionRequest) {
		Response response = cidaasClient.callIntrospection(tokenIntrospectionRequest);
		JsonObject output = response.readEntity(JsonObject.class);
		return output.getBoolean("active");
	}
	
	public boolean offlineTokenValidation(TokenIntrospectionRequest tokenIntrospectionRequest) {
		JsonObject jwks = getJwks();
		
		return false;
	}
	
	void onStop(@Observes ShutdownEvent ev) {  
		clearJwkCache();
    }
}
