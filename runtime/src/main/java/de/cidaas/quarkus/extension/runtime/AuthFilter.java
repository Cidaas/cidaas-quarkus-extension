package de.cidaas.quarkus.extension.runtime;

import java.lang.reflect.Method;
import java.util.List;

import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import de.cidaas.quarkus.extension.Group;
import de.cidaas.quarkus.extension.GroupsAllowed;
import de.cidaas.quarkus.extension.RolesAllowed;
import de.cidaas.quarkus.extension.ScopesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ResourceInfo;

public class AuthFilter {
	
	@Inject ResourceInfo resourceInfo;
	
	@Inject CidaasService cidaasService;

	@ServerResponseFilter
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext, Throwable t) {
		if (resourceInfo == null) {
			return;
		}
		
		Method method = resourceInfo.getResourceMethod();
		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
		GroupsAllowed groupsAllowed = method.getAnnotation(GroupsAllowed.class);
		ScopesAllowed scopesAllowed = method.getAnnotation(ScopesAllowed.class);
		if (rolesAllowed == null && groupsAllowed == null && scopesAllowed == null) {
			return;
		}
		
		String accessToken = requestContext.getHeaderString("Authorization");
		if (accessToken == null) {
			responseContext.setStatus(401);
			responseContext.setEntity(null);
			return;
		}
		
		accessToken = accessToken.replace("Bearer ", "");
		List<String> roles = AnnotationsMapper.mapToRoles(rolesAllowed);
		List<Group> groups = AnnotationsMapper.mapToGroups(groupsAllowed);
		List<String> scopes = AnnotationsMapper.mapToScopes(scopesAllowed);
		
		boolean valid = cidaasService.introspectToken(accessToken, roles, groups, scopes);
		if (valid == false) {
			responseContext.setStatus(401);
			responseContext.setEntity(null);
		}
	}
}
