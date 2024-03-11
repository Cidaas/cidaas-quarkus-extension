package de.cidaas.quarkus.extension.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.cidaas.quarkus.extension.Group;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;


@QuarkusTest
@TestProfile(CustomTestProfile.class)
public class OfflineTokenValidationServiceTest {
	
	@Inject
	OfflineTokenValidationService offlineTokenValidationService;
	
	@InjectMock
	CacheService cacheService;
	
	@Test
	public void testIntrospectTokenHeader_noIntrospectionRequest() {
		boolean isValid = offlineTokenValidationService.introspectToken(null);
		assertFalse(isValid);
	}
	
	@Test
	public void testIntrospectTokenHeader_noHeader() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {

		    mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(null);

		    assertEquals(null, JwtUtil.decodeHeader(""));
		    mockStatic.verify(() -> JwtUtil.decodeHeader(""));
		    
		    boolean isValid = offlineTokenValidationService.introspectToken(null);
			assertFalse(isValid);
		  }
	}
	
	@Test
	public void testValidateTokenHeader_emptyJwks() {
		JsonObject jwks = Json.createObjectBuilder()
			.add("keys", Json.createArrayBuilder())
			.build();
		when(cacheService.getJwks()).thenReturn(jwks);
		
		JsonObject header = Json.createObjectBuilder()
			.add("alg", "123")
			.add("kid", "456")
			.build();
		
		boolean isValid = offlineTokenValidationService.validateTokenHeader(header);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateTokenHeader_missingHeaderClaim() {
		when(cacheService.getJwks()).thenReturn(createValidJwks());
		
		JsonObject header = Json.createObjectBuilder()
			.add("alg", "123")
			.build();
		
		boolean isValid = offlineTokenValidationService.validateTokenHeader(header);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateTokenHeader_invalidCombination() {
		when(cacheService.getJwks()).thenReturn(createValidJwks());
		
		JsonObject header = Json.createObjectBuilder()
			.add("alg", "abc")
			.add("kid", "456")
			.build();
		
		boolean isValid = offlineTokenValidationService.validateTokenHeader(header);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateTokenHeader_validHeader() {
		when(cacheService.getJwks()).thenReturn(createValidJwks());
		
		JsonObject header = Json.createObjectBuilder()
			.add("alg", "123")
			.add("kid", "456")
			.build();
		
		boolean isValid = offlineTokenValidationService.validateTokenHeader(header);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateGeneralInfo_missingClaim() {
		JsonObject payload = Json.createObjectBuilder()
			.add("test", "test")
			.build();
		
		boolean isValid = offlineTokenValidationService.validateGeneralInfo(payload);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateGeneralInfo_invalidIss() {
		JsonObject payload = Json.createObjectBuilder()
			.add("iss", "invalidUrl")
			.build();
		
		boolean isValid = offlineTokenValidationService.validateGeneralInfo(payload);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateGeneralInfo_expiredToken() {
		int exp = (int) Instant.now().getEpochSecond() - 3000;
		JsonObject payload = Json.createObjectBuilder()
			.add("iss", "mockUrl")
			.add("exp", exp)
			.build();
		
		boolean isValid = offlineTokenValidationService.validateGeneralInfo(payload);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateGeneralInfo_validPayload() {
		int exp = (int) Instant.now().getEpochSecond() + 3000;
		JsonObject payload = Json.createObjectBuilder()
			.add("iss", "mockUrl")
			.add("exp", exp)
			.build();
		
		boolean isValid = offlineTokenValidationService.validateGeneralInfo(payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateScopes_noScopeValidationNeededInRequest() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		tokenIntrospectionRequest.setScopes(new ArrayList<String>());
		JsonObject payload = Json.createObjectBuilder()
				.add("scopes", Json.createArrayBuilder().add("scope1").add("scope2")).build();
		
		boolean isValid = offlineTokenValidationService.validateScopes(tokenIntrospectionRequest, payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateScopes_scopeValidationNeededInRequest_valid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		JsonObject payload = Json.createObjectBuilder()
				.add("scopes", Json.createArrayBuilder().add("scope1").add("scope2")).build();
		
		boolean isValid = offlineTokenValidationService.validateScopes(tokenIntrospectionRequest, payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateScopes_scopeValidationNeededInRequest_invalid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		tokenIntrospectionRequest.setScopes(Arrays.asList("scope3", "scope4"));
		JsonObject payload = Json.createObjectBuilder()
				.add("scopes", Json.createArrayBuilder().add("scope1").add("scope2")).build();
		
		boolean isValid = offlineTokenValidationService.validateScopes(tokenIntrospectionRequest, payload);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateRoles_noRoleValidationNeededInRequest() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		tokenIntrospectionRequest.setRoles(new ArrayList<String>());
		JsonObject payload = Json.createObjectBuilder()
				.add("roles", Json.createArrayBuilder().add("role1").add("role2")).build();
		
		boolean isValid = offlineTokenValidationService.validateRoles(tokenIntrospectionRequest, payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateRoles_roleValidationNeededInRequest_valid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		JsonObject payload = Json.createObjectBuilder()
				.add("roles", Json.createArrayBuilder().add("role1").add("role2")).build();
		
		boolean isValid = offlineTokenValidationService.validateRoles(tokenIntrospectionRequest, payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateRoles_roleValidationNeededInRequest_invalid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		tokenIntrospectionRequest.setRoles(Arrays.asList("role3", "role4"));
		JsonObject payload = Json.createObjectBuilder()
				.add("roles", Json.createArrayBuilder().add("role1").add("role2")).build();
		
		boolean isValid = offlineTokenValidationService.validateRoles(tokenIntrospectionRequest, payload);
		assertFalse(isValid);
	}
	
	private JsonObject createValidJwks() {
		JsonObject jwks = Json.createObjectBuilder()
			.add("keys", Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
					.add("alg", "abc")
					.add("kid", "def")
				)
				.add(Json.createObjectBuilder()
					.add("alg", "123")
					.add("kid", "456")
				)
			)
			.build();
		return jwks;
	}
	
	private TokenIntrospectionRequest createTokenIntrospectionRequest() {
		TokenIntrospectionRequest result = new TokenIntrospectionRequest();
		
		result.setRoles(Arrays.asList("role1", "role2"));
		
		List<Group> groups = new ArrayList<>();
		groups.add(new Group("group1", Arrays.asList("grouprole1", "grouprole2")));
		groups.add(new Group("group2", Arrays.asList("grouprole3", "grouprole4")));
		result.setGroups(groups);
		
		result.setScopes(Arrays.asList("scope1", "scope2"));
		result.setStrictRoleValidation(true);
		result.setStrictGroupValidation(true);
		result.setStrictScopeValidation(true);
		result.setStrictValidation(true);
		
		return result;
	}

}
