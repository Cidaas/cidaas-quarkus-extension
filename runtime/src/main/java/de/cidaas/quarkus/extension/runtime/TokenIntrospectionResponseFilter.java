package de.cidaas.quarkus.extension.runtime;

import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;

public class TokenIntrospectionResponseFilter {
	
	@Inject CidaasService cidaasService;

	@ServerResponseFilter
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext, Throwable t) {
		boolean valid = cidaasService.introspectToken();
		System.out.println("valid: " + valid);
		responseContext.setStatus(403);
		responseContext.setEntity(null); 
	}
}
