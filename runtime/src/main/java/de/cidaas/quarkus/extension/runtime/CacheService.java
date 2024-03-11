package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.CidaasClient;
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
public class CacheService {
	@Inject
	@RestClient
	CidaasClient cidaasClient;
	
	
	void onStart(@Observes StartupEvent ev) {    
		getJwks();
    }
	
	@CacheResult(cacheName = "jwk-cache")
	JsonObject getJwks() {
		Response response = cidaasClient.getJwks();
		if (response == null) {
			return null;
		}
		return response.readEntity(JsonObject.class);
	}
	
	@CacheInvalidate(cacheName = "jwk-cache")
	void clearJwkCache() {}
	
	// TODO: override refresh value from property file
	@Scheduled(every = "${de.cidaas.quarkus.extension.cache-refresh-rate:86400s}") // 1 day by default
	void refreshJwks() {
		System.out.println("jwk refreshed");
		clearJwkCache();
		getJwks();
	}
	
	void onStop(@Observes ShutdownEvent ev) {
		clearJwkCache();
    }
}
