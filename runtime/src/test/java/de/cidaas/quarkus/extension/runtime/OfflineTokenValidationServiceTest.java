package de.cidaas.quarkus.extension.runtime;

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
	public void testIntrospectToken_noIntrospectionRequest() {
		boolean isValid = offlineTokenValidationService.introspectToken(null);
		assertFalse(isValid);
	}
	
	@Test
	public void testIntrospectToken_noHeader() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(null);
		    boolean isValid = offlineTokenValidationService.introspectToken(null);
			assertFalse(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_noPayload() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(null);
		    boolean isValid = offlineTokenValidationService.introspectToken(null);
			assertFalse(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_scopeFlexibleValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("scopes", Json.createArrayBuilder().add("scope1").add("scopeOther"))
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_scopeStrictValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("scopes", Json.createArrayBuilder().add("scope1").add("scope2"))
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
			tokenIntrospectionRequest.setStrictScopeValidation(true);
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_roleFlexibleValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("roles", Json.createArrayBuilder().add("role1").add("roleOther"))
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_roleStrictValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("roles", Json.createArrayBuilder().add("role1").add("role2"))
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
			tokenIntrospectionRequest.setStrictRoleValidation(true);
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_groupFlexibleValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("scopes", Json.createArrayBuilder().add("scope1").add("scopeOther"))
					.add("roles", Json.createArrayBuilder().add("roleOther").add("roleOther"))
					.add("groups", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
							.add("groupId", "groupOther1")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole1")
								.add("grouprole2")
							)
						)
						.add(Json.createObjectBuilder()
							.add("groupId", "groupOther2")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole3")
								.add("grouprole4")
							)
						)
					)
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_groupStrictValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("scopes", Json.createArrayBuilder().add("scope1").add("scope2"))
					.add("roles", Json.createArrayBuilder().add("role1").add("role2"))
					.add("groups", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
							.add("groupId", "group1")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole1")
								.add("grouprole2")
							)
						)
						.add(Json.createObjectBuilder()
							.add("groupId", "group2")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole3")
								.add("grouprole4")
							)
						)
					)
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
			tokenIntrospectionRequest.setStrictValidation(true);
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_FlexibleValidation() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("groups", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
							.add("groupId", "group1")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole1")
								.add("grouprole2")
							)
						)
						.add(Json.createObjectBuilder()
							.add("groupId", "group2")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole3")
								.add("grouprole4")
							)
						)
					)
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
			tokenIntrospectionRequest.setStrictGroupValidation(true);
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void testIntrospectToken_StrictValidation() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    when(cacheService.getJwks()).thenReturn(createValidJwks());
			JsonObject header = Json.createObjectBuilder()
				.add("alg", "123")
				.add("kid", "456")
				.build();
			mockStatic.when(() -> JwtUtil.decodeHeader(anyString())).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			
			int exp = (int) Instant.now().getEpochSecond() + 3000;
			JsonObject payload = Json.createObjectBuilder()
					.add("iss", "mockUrl")
					.add("exp", exp)
					.add("groups", Json.createArrayBuilder()
						.add(Json.createObjectBuilder()
							.add("groupId", "group1")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole1")
								.add("grouprole2")
							)
						)
						.add(Json.createObjectBuilder()
							.add("groupId", "group2")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole3")
								.add("grouprole4")
							)
						)
					)
					.build();
			mockStatic.when(() -> JwtUtil.decodePayload(anyString())).thenReturn(payload);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(payload);
			
			TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
			tokenIntrospectionRequest.setStrictGroupValidation(true);
		    boolean isValid = offlineTokenValidationService.introspectToken(tokenIntrospectionRequest);
			assertTrue(isValid);
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
	
	@Test
	public void testValidateRoles_noGroupValidationNeededInRequest() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		tokenIntrospectionRequest.setGroups(new ArrayList<Group>());
		JsonObject payload = createGroupPayload();
		
		boolean isValid = offlineTokenValidationService.validateGroups(tokenIntrospectionRequest, payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateRoles_groupValidationNeededInRequest_flexibleValid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		List<Group> groups = new ArrayList<>();
		groups.add(new Group("group1", Arrays.asList("grouprole1", "grouprole2")));
		tokenIntrospectionRequest.setGroups(groups);		
		JsonObject payload = createGroupPayload();
		
		boolean isValid = offlineTokenValidationService.validateGroups(tokenIntrospectionRequest, payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateRoles_groupValidationNeededInRequest_flexibleInvalid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		List<Group> groups = new ArrayList<>();
		groups.add(new Group("groupOther", Arrays.asList("grouprole1", "grouprole2")));
		tokenIntrospectionRequest.setGroups(groups);		
		JsonObject payload = createGroupPayload();
		
		boolean isValid = offlineTokenValidationService.validateGroups(tokenIntrospectionRequest, payload);
		assertFalse(isValid);
	}
	
	@Test
	public void testValidateRoles_groupValidationNeededInRequest_strictValid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		List<Group> groups = new ArrayList<>();
		groups.add(new Group("group2", Arrays.asList("grouprole3", "grouprole4"), true));
		tokenIntrospectionRequest.setGroups(groups);		
		JsonObject payload = createGroupPayload();
		
		boolean isValid = offlineTokenValidationService.validateGroups(tokenIntrospectionRequest, payload);
		assertTrue(isValid);
	}
	
	@Test
	public void testValidateRoles_groupValidationNeededInRequest_strictInvalid() {
		TokenIntrospectionRequest tokenIntrospectionRequest = createTokenIntrospectionRequest();
		List<Group> groups = new ArrayList<>();
		groups.add(new Group("group2", Arrays.asList("grouprole3", "grouproleOther"), true));
		groups.add(new Group("invalidGroup", Arrays.asList("grouprole3", "grouprole4")));
		tokenIntrospectionRequest.setGroups(groups);		
		JsonObject payload = createGroupPayload();
		
		boolean isValid = offlineTokenValidationService.validateGroups(tokenIntrospectionRequest, payload);
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
	
	private JsonObject createGroupPayload() {
		JsonObject payload = Json.createObjectBuilder()
				.add("groups", Json.createArrayBuilder()
					.add(Json.createObjectBuilder()
						.add("groupId", "group1")
						.add("roles", Json.createArrayBuilder()
							.add("grouprole1")
							.add("grouprole2")
						)
					)
					.add(Json.createObjectBuilder()
							.add("groupId", "group2")
							.add("roles", Json.createArrayBuilder()
								.add("grouprole3")
								.add("grouprole4")
							)
						)
				)
				.build();
		return payload;
	}
	
	private TokenIntrospectionRequest createTokenIntrospectionRequest() {
		TokenIntrospectionRequest result = new TokenIntrospectionRequest();
		
		result.setRoles(Arrays.asList("role1", "role2"));
		
		List<Group> groups = new ArrayList<>();
		groups.add(new Group("group1", Arrays.asList("grouprole1", "grouprole2")));
		groups.add(new Group("group2", Arrays.asList("grouprole3", "grouprole4")));
		result.setGroups(groups);
		
		result.setScopes(Arrays.asList("scope1", "scope2"));
		
		return result;
	}

}
