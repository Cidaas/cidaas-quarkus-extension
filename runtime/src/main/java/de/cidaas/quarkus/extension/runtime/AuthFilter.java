package de.cidaas.quarkus.extension.runtime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
		
		String accessToken = requestContext.getHeaderString("Authorization");
		
		Method method = resourceInfo.getResourceMethod();
		
		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
		List<String> roles = parseRolesFromAnnotation(rolesAllowed);
		
		GroupsAllowed groupsAllowed = method.getAnnotation(GroupsAllowed.class);
		List<Group> groups = parseGroupsFromAnnotation(groupsAllowed);
		
		ScopesAllowed scopesAllowed = method.getAnnotation(ScopesAllowed.class);
		List<String> scopes = parseScopesFromAnnotation(scopesAllowed);
		
		if (accessToken != null) {
			accessToken = accessToken.replace("Bearer ", "");
			boolean valid = cidaasService.introspectToken(accessToken, roles, groups, scopes);
			if (valid == false) {
				responseContext.setStatus(403);
				responseContext.setEntity(null);
			}
		}
	}
	
	List<String> parseRolesFromAnnotation(RolesAllowed rolesAllowed) {
		if (rolesAllowed == null) {
			return null;
		}
		
		return Arrays.asList(rolesAllowed.value());
	}
	
	List<Group> parseGroupsFromAnnotation(GroupsAllowed groupsAllowed) {
		if (groupsAllowed == null) {
			return null;
		}
		
		List<Group> result = new ArrayList<>();
		
		List<String> groups = Arrays.asList(groupsAllowed.value());
		
		for(String group : groups) {
			String[] parts = group.split(":");
			String groupId = parts[0];
			String role = parts[1];
			Group groupEntity = new Group(groupId, Arrays.asList(role), false);
			result.add(groupEntity);
		}
		
		return result;
	}
	
	List<String> parseScopesFromAnnotation(ScopesAllowed scopesAllowed) {
		if (scopesAllowed == null) {
			return null;
		}
		
		return Arrays.asList(scopesAllowed.value());
	}
}
