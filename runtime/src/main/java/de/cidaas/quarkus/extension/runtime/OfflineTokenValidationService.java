package de.cidaas.quarkus.extension.runtime;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cidaas.quarkus.extension.CidaasQuarkusException;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(OfflineTokenValidationService.class);
	
	/**
     * introspect token without calling introspection endpoint.
     *
     * @param TokenIntrospectionRequest contain access token & definition which claims to be validated and how.
     * 
     * @return true if tokenIntrospectionRequest is valid, false if invalid
     */
	public boolean introspectToken(TokenIntrospectionRequest tokenIntrospectionRequest) {	
		JsonObject header = JwtUtil.decodeHeader(tokenIntrospectionRequest.getToken());
		
		if (header == null || validateTokenHeader(header) == false) {
			return false;
		}

		JsonObject payload = JwtUtil.decodePayload(tokenIntrospectionRequest.getToken());
		
		if (payload == null || validateGeneralInfo(payload) == false) {
			return false;
		}
		
		List<Boolean> toBeValidated = new ArrayList<>();
		
		if (tokenIntrospectionRequest.getScopes() != null && !tokenIntrospectionRequest.getScopes().isEmpty()) {
			toBeValidated.add(validateScopes(tokenIntrospectionRequest, payload));
		}
		
		if (tokenIntrospectionRequest.getRoles() != null && !tokenIntrospectionRequest.getRoles().isEmpty()) {
			toBeValidated.add(validateRoles(tokenIntrospectionRequest, payload));
		}
		
		if (tokenIntrospectionRequest.getGroups() != null && !tokenIntrospectionRequest.getGroups().isEmpty()) {
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
	
	/**
     * validate header part of access token.
     *
     * @param header to be validated.
     * 
     * @return true if header‚ is valid, false if invalid
     */
	boolean validateTokenHeader(JsonObject header) {
		JsonObject jwks = cacheService.getJwks();
		
		if (jwks == null) {
			LOG.error("jwk is null!");
			throw new CidaasQuarkusException("JWK invalid!");
		}
		
		JsonArray keys = jwks.getJsonArray("keys");
		
		if (keys == null || keys.isEmpty()) {
			LOG.error("keys couldn't be found!");
			throw new CidaasQuarkusException("JWK invalid!");
		}
		
		String kid = this.getStringFromJsonOrNull(header, "kid");
		String alg = this.getStringFromJsonOrNull(header, "alg");
		
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
	
	/**
     * validate payload part of access token.
     *
     * @param payload to be validated.
     * 
     * @return true if payload is valid, false if invalid
     */
	boolean validateGeneralInfo(JsonObject payload) {
		String baseUrl = ConfigProvider.getConfig().getValue("de.cidaas.quarkus.extension.CidaasClient/mp-rest/url", String.class);
		
		if (this.getStringFromJsonOrNull(payload, "iss") == null) {
			LOG.warn("token doesn't have iss!");
			return false;
		}
		
		if (!this.getStringFromJsonOrNull(payload, "iss").equals(baseUrl)) {
			LOG.warn("iss is invalid!");
			return false;
		}
		
		int dateAsNumber = payload.getInt("exp");
		Instant expirationDate = Instant.ofEpochSecond(dateAsNumber);
		
		if (expirationDate.compareTo(Instant.now()) < 0) {
			LOG.warn("token is expired!");
			return false;
		}
		
		return true;
	}
	
	private boolean validateScopes (TokenIntrospectionRequest tokenIntrospectionRequest, JsonObject payload) {
		JsonArray scopes = payload.getJsonArray("scopes");
		if (scopes == null) {
			return false;
		}
		List<String> scopesFromToken = scopes.getValuesAs(JsonString::getString);
		if (tokenIntrospectionRequest.isStrictScopeValidation() == true && 
				!scopesFromToken.containsAll(tokenIntrospectionRequest.getScopes())) {
			LOG.warn("token doesn't have enough scopes!");
			return false;
		} 
		if (tokenIntrospectionRequest.isStrictScopeValidation() == false && 
				!scopesFromToken.stream().anyMatch(element -> tokenIntrospectionRequest.getScopes().contains(element))) {
			LOG.warn("token doesn't have enough scopes!");
			return false;
		}
		return true;
	}
	
	private boolean validateRoles(TokenIntrospectionRequest tokenIntrospectionRequest, JsonObject payload) { 
		JsonArray roles = payload.getJsonArray("roles");
		if (roles == null) {
			return false;
		}
		List<String> rolesFromToken = roles.getValuesAs(JsonString::getString);
		if(tokenIntrospectionRequest.isStrictRoleValidation() == true && 
				!rolesFromToken.containsAll(tokenIntrospectionRequest.getRoles())) {
			LOG.warn("token doesn't have enough roles!");
			return false;
		} 
		if (tokenIntrospectionRequest.isStrictRoleValidation() == false && 
				!rolesFromToken.stream().anyMatch(element -> tokenIntrospectionRequest.getRoles().contains(element))) {
			LOG.warn("token doesn't have enough roles!");
			return false;
		}
		return true;
	}
	
	private boolean validateGroups(TokenIntrospectionRequest tokenIntrospectionRequest, JsonObject payload) {
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
		
		for (Group groupFromIntrospectionRequest : tokenIntrospectionRequest.getGroups()) {
			boolean isGroupValid = validateGroup(groupFromIntrospectionRequest, groupsFromToken);
			
			if (isGroupValid == true && strictGroupValidation == false) {
				return true;
			}
			if (isGroupValid == false) {
				isAllGroupValid = false;
				if (strictGroupValidation == true) {
					LOG.warn("token doesn't have enough groups!");
					return false;
				}
			}
		}
		
		return isAllGroupValid;

	}
	
	private boolean validateGroup(Group groupFromIntrospectionRequest, List<Group> groupsFromToken ) {
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
		LOG.warn("grouproles is invalid!");
		return false;
	}
	
	private String getStringFromJsonOrNull(JsonObject jsonObject, String key) {
		JsonString jsonString = jsonObject.getJsonString(key);
		if (jsonString == null) {
			return null;
		}
		return jsonString.getString();
	}

}
