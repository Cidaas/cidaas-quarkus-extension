package de.cidaas.quarkus.extension.runtime;

import java.util.Optional;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

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
	
	@ServerRequestFilter
	public Optional<RestResponse<Void>> getFilter(ContainerRequestContext requestContext) {
		if (resourceInfo == null) {
			return Optional.empty();
		}
		
		TokenValidation tokenValidation = resourceInfo.getResourceMethod().getAnnotation(TokenValidation.class);
		if (tokenValidation == null) {
			return Optional.empty();
		}
		
		String authorizationHeader = requestContext.getHeaderString("Authorization");
		if (authorizationHeader == null) {
			return Optional.of(RestResponse.status(Response.Status.UNAUTHORIZED));
		}
		
		String[] arr = authorizationHeader.split(" ", 0);
		String accessToken = arr[arr.length - 1];
		
		TokenIntrospectionRequest tokenIntrospectionRequest = AnnotationsMapper.mapToIntrospectionRequest(accessToken, tokenValidation);
		
		boolean valid = tokenValidation.offlineValidation() == true ? 
						offlineTokenValidationService.introspectToken(tokenIntrospectionRequest) :
						cidaasService.introspectToken(tokenIntrospectionRequest);
		
		if (valid == false) {
			return Optional.of(RestResponse.status(Response.Status.UNAUTHORIZED));
		}
		
		return Optional.empty();
	}
}
