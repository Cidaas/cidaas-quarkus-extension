package de.cidaas.quarkus.extension.runtime;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);
	
	void onStart(@Observes StartupEvent ev) {  
		getJwks();
		LOG.info("get JWK cache.");
    }
	
	@CacheResult(cacheName = "jwk-cache")
	JsonObject getJwks() {
		Response response = cidaasClient.getJwks();
		if (response == null) {
			throw new CidaasQuarkusException("response of jwks is null!");
		}
		return response.readEntity(JsonObject.class);
	}
	
	@CacheInvalidate(cacheName = "jwk-cache")
	void clearJwkCache() {}
	
	@Scheduled(every = "${de.cidaas.quarkus.extension.cache-refresh-rate:86400s}") // 1 day by default
	void refreshJwks() {
		clearJwkCache();
		getJwks();
		LOG.info("refresh JWK cache!");
	}
	
	void onStop(@Observes ShutdownEvent ev) {
		clearJwkCache();
		LOG.info("clear JWK cache.");
    }
}
