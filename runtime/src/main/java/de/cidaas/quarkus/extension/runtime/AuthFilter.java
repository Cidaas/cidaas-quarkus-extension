package de.cidaas.quarkus.extension.runtime;

import java.lang.reflect.Method;
import java.util.Optional;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cidaas.quarkus.extension.CidaasQuarkusException;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import de.cidaas.quarkus.extension.TokenValidation;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;

public class AuthFilter {
	
	@Inject ResourceInfo resourceInfo;
	
	@Inject CidaasService cidaasService;
	
	@Inject OfflineTokenValidationService offlineTokenValidationService;
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class);
	
	@ServerRequestFilter
	public Optional<RestResponse<Void>> getFilter(ContainerRequestContext requestContext) {
		if (resourceInfo == null) {
			throw new CidaasQuarkusException("resourceInfo is null!");
		}
		
		if (resourceInfo.getResourceMethod() == null) {
			throw new CidaasQuarkusException("resourceMethod is null!");
		}
		
		Method method = resourceInfo.getResourceMethod();
		
		TokenValidation tokenValidation = method.getAnnotation(TokenValidation.class);
		if (tokenValidation == null) {
			return Optional.empty();
		}
		
		LOG.info("Filtering request to: {}", requestContext.getUriInfo().getAbsolutePath().toString());
		
		String authorizationHeader = requestContext.getHeaderString("Authorization");
		if (authorizationHeader == null) {
			LOG.warn("Method has no authorization header:" + method.getName());
			return Optional.of(RestResponse.status(Response.Status.UNAUTHORIZED));
		}
		
		String[] arr = authorizationHeader.split(" ", 0);
		String accessToken = arr[arr.length - 1];
		
		if (accessToken == null) {
			LOG.warn("accessToken is null!");
			return Optional.of(RestResponse.status(Response.Status.UNAUTHORIZED));
		}
		
		TokenIntrospectionRequest tokenIntrospectionRequest = AnnotationsMapper.mapToIntrospectionRequest(accessToken, tokenValidation);
		
		if (tokenIntrospectionRequest == null) {
			throw new CidaasQuarkusException("tokenIntrospectionRequest is null!");
		}
		
		boolean valid = tokenValidation.offlineValidation() == true ? 
						offlineTokenValidationService.introspectToken(tokenIntrospectionRequest) :
						cidaasService.introspectToken(tokenIntrospectionRequest);
		
		return valid ? Optional.empty() : Optional.of(RestResponse.status(Response.Status.UNAUTHORIZED));
	}
}
