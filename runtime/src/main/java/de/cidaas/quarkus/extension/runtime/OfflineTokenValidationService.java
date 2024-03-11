package de.cidaas.quarkus.extension.runtime;

import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import de.cidaas.quarkus.extension.CidaasClient;
import de.cidaas.quarkus.extension.Group;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class OfflineTokenValidationService {
	
	@Inject
	CacheService cacheService;
	
	public boolean introspectToken(TokenIntrospectionRequest tokenIntrospectionRequest) {	
		if (validateTokenHeader(header) == false) {
			return false;
		}
		
		if (validateGeneralInfo(payload) == false) {
		JsonObject header = JwtUtil.decodeHeader(tokenIntrospectionRequest.getToken());
		JsonObject payload = JwtUtil.decodePayload(tokenIntrospectionRequest.getToken());
			return false;
		}
		
		List<Boolean> toBeValidated = new ArrayList<>();
		
		if (!tokenIntrospectionRequest.getScopes().isEmpty()) {
			toBeValidated.add(validateScopes(tokenIntrospectionRequest, payload));
		}
		
		if (!tokenIntrospectionRequest.getRoles().isEmpty()) {
			toBeValidated.add(validateRoles(tokenIntrospectionRequest, payload));
		}
		
		if (!tokenIntrospectionRequest.getGroups().isEmpty()) {
			toBeValidated.add(validateGroups(tokenIntrospectionRequest, payload));
		}
		
		if (toBeValidated.isEmpty() == true) {
			return true;
		}
		
		if (tokenIntrospectionRequest.isStrictValidation() == true) {
			return !toBeValidated.contains(false);
		}
		
		return toBeValidated.contains(true);
		
	}
	
	public boolean validateTokenHeader(JsonObject header) {
		JsonObject jwks = cacheService.getJwks();
		JsonArray keys = jwks.getJsonArray("keys");
		
		String kid = header.getString("kid");
		String alg = header.getString("alg");
		
		for (int i = 0; i < keys.size(); i++) {
			JsonObject key = keys.getJsonObject(i);
			String keyKid = key.getString("kid");
			String keyAlg = key.getString("alg");
			if (keyKid.equals(kid) && keyAlg.equals(alg)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean validateGeneralInfo(JsonObject payload) {
		String baseUrl = ConfigProvider.getConfig().getValue("de.cidaas.quarkus.extension.CidaasClient/mp-rest/url", String.class);
		
		if (!payload.getString("iss").equals(baseUrl)) {
			return false;
		}
		
		int dateAsNumber = payload.getInt("exp");
		Instant expirationDate = Instant.ofEpochSecond(dateAsNumber);
		
		if (expirationDate.compareTo(Instant.now()) < 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean validateScopes (TokenIntrospectionRequest tokenIntrospectionRequest, JsonObject payload) {
		List<String> scopesFromIntrospectionRequest = tokenIntrospectionRequest.getScopes();
		if(!scopesFromIntrospectionRequest.isEmpty()) {
			JsonArray scopes = payload.getJsonArray("scopes");
			if (scopes == null) {
				return false;
			}
			List<String> scopesFromToken = scopes.getValuesAs(JsonString::getString);
			if (tokenIntrospectionRequest.isStrictScopeValidation() == true && 
					!scopesFromToken.containsAll(scopesFromIntrospectionRequest)) {
				return false;
			} 
			if (tokenIntrospectionRequest.isStrictScopeValidation() == false && 
					!scopesFromToken.stream().anyMatch(element -> scopesFromIntrospectionRequest.contains(element))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validateRoles(TokenIntrospectionRequest tokenIntrospectionRequest, JsonObject payload) {
		List<String> rolesFromIntrospectionRequest = tokenIntrospectionRequest.getRoles();
		if(!rolesFromIntrospectionRequest.isEmpty()) { 
			JsonArray roles = payload.getJsonArray("roles");
			if (roles == null) {
				return false;
			}
			List<String> rolesFromToken = roles.getValuesAs(JsonString::getString);
			if(tokenIntrospectionRequest.isStrictRoleValidation() == true && 
					!rolesFromToken.containsAll(rolesFromIntrospectionRequest)) {
				return false;
			} 
			if (tokenIntrospectionRequest.isStrictRoleValidation() == false && 
					!rolesFromToken.stream().anyMatch(element -> rolesFromIntrospectionRequest.contains(element))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean validateGroups(TokenIntrospectionRequest tokenIntrospectionRequest, JsonObject payload) {
		
		List<Group> groupsFromIntrospectionRequest = tokenIntrospectionRequest.getGroups();
		if(groupsFromIntrospectionRequest.isEmpty()) {
			return true;
		}
		JsonArray groups = payload.getJsonArray("groups");
		if (groups == null) {
			return false;
		}
		boolean strictGroupValidation = tokenIntrospectionRequest.isStrictGroupValidation();
		boolean isAllGroupValid = true;
		
		
		List<Group> groupsFromToken = new ArrayList<>();
		for (int i = 0; i < groups.size(); i++) {
			JsonObject groupFromToken = groups.getJsonObject(i);
			String groupIdFromToken = groupFromToken.getString("groupId");
			List<String> groupRolesFromToken = groupFromToken.getJsonArray("roles").getValuesAs(JsonString::getString);
			Group group = new Group (groupIdFromToken, groupRolesFromToken);
			groupsFromToken.add(group);			
		}
		
		for (Group groupFromIntrospectionRequest : groupsFromIntrospectionRequest) {
			boolean isGroupValid = validateGroup(groupFromIntrospectionRequest, groupsFromToken);
			
			if (isGroupValid == true && strictGroupValidation == false) {
				return true;
			}
			if (isGroupValid == false) {
				isAllGroupValid = false;
				if (strictGroupValidation == true) {
					return false;
				}
			}
		}
		
		return isAllGroupValid;

	}
	
	public boolean validateGroup(Group groupFromIntrospectionRequest, List<Group> groupsFromToken ) {
		String groupIdFromIntrospectionRequest = groupFromIntrospectionRequest.getGroupId();
		List<String> groupRolesFromIntrospectionRequest = groupFromIntrospectionRequest.getRoles();
		boolean strictGroupRoleValidation = groupFromIntrospectionRequest.isStrictRoleValidation();
		
		for (Group groupFromToken: groupsFromToken) {
			if (groupFromToken.getGroupId().equals(groupIdFromIntrospectionRequest)) {
				if (strictGroupRoleValidation == true && groupFromToken.getRoles().containsAll(groupRolesFromIntrospectionRequest)) {
					return true;
				}
				if (strictGroupRoleValidation == false && groupFromToken.getRoles().stream().anyMatch(element -> groupRolesFromIntrospectionRequest.contains(element))) {
					return true;
				}		
			}
		}
		return false;
	}

}
