package de.cidaas.quarkus.extension.runtime;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

public class TokenIntrospectionResponseFilter implements ContainerResponseFilter {
	
	@Inject CidaasService cidaasService;

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		boolean valid = cidaasService.introspectToken();
		System.out.println("valid: " + valid);
		responseContext.setStatus(403);
		responseContext.setEntity(null); 
	}
}
