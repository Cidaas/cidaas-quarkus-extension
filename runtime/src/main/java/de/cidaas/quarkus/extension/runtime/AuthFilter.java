package de.cidaas.quarkus.extension.runtime;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import de.cidaas.quarkus.extension.Group;
import de.cidaas.quarkus.extension.GroupsAllowed;
import de.cidaas.quarkus.extension.RolesAllowed;
import de.cidaas.quarkus.extension.ScopesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Response;

public class AuthFilter {
	
	@Inject ResourceInfo resourceInfo;
	
	@Inject CidaasService cidaasService;

	@ServerRequestFilter
	public Optional<RestResponse<Void>> getFilter(ContainerRequestContext requestContext) {
		if (resourceInfo == null) {
			return Optional.empty();
		}
		
		Method method = resourceInfo.getResourceMethod();
		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
		GroupsAllowed groupsAllowed = method.getAnnotation(GroupsAllowed.class);
		ScopesAllowed scopesAllowed = method.getAnnotation(ScopesAllowed.class);
		if (rolesAllowed == null && groupsAllowed == null && scopesAllowed == null) {
			return Optional.empty();
		}
		
		String accessToken = requestContext.getHeaderString("Authorization");
		if (accessToken == null) {
			return Optional.of(RestResponse.status(Response.Status.UNAUTHORIZED));
		}
		
		accessToken = accessToken.replace("Bearer ", "");
		List<String> roles = AnnotationsMapper.mapToRoles(rolesAllowed);
		List<Group> groups = AnnotationsMapper.mapToGroups(groupsAllowed);
		List<String> scopes = AnnotationsMapper.mapToScopes(scopesAllowed);
		
		boolean valid = cidaasService.introspectToken(accessToken, roles, groups, scopes);
		if (valid == false) {
			return Optional.of(RestResponse.status(Response.Status.UNAUTHORIZED));
		}
		return Optional.empty();
	}
}
