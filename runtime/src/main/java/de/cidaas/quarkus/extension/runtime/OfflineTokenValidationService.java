package de.cidaas.quarkus.extension.runtime;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;

import de.cidaas.quarkus.extension.Group;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;

@ApplicationScoped
public class OfflineTokenValidationService {
	
	@Inject
	CacheService cacheService;
	
	public boolean introspectToken(TokenIntrospectionRequest tokenIntrospectionRequest) {	
		if (tokenIntrospectionRequest == null) {
			return false;
		}
		
		JsonObject header = JwtUtil.decodeHeader(tokenIntrospectionRequest.getToken());
		
		if (header == null || validateTokenHeader(header) == false) {
			return false;
		}

		JsonObject payload = JwtUtil.decodePayload(tokenIntrospectionRequest.getToken());
		
		if (payload == null || validateGeneralInfo(payload) == false) {
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
		
		if (jwks == null) {
			return false;
		}
		
		JsonArray keys = jwks.getJsonArray("keys");
		
		if (keys == null || keys.isEmpty()) {
			return false;
		}
		
		String kid = this.getString(header, "kid");
		String alg = this.getString(header, "alg");
		
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
		
		if (this.getString(payload, "iss") == null) {
			return false;
		}
		
		if (!this.getString(payload, "iss").equals(baseUrl)) {
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
	
	String getString(JsonObject jsonObject, String key) {
		JsonString jsonString = jsonObject.getJsonString(key);
		if (jsonString == null) {
			return null;
		}
		return jsonString.getString();
	}

}
