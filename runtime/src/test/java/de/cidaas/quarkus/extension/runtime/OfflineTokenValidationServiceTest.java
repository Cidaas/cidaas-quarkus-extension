package de.cidaas.quarkus.extension.runtime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import de.cidaas.quarkus.extension.runtime.MockService.IntrospectionOptions;
import de.cidaas.quarkus.extension.runtime.MockService.PayloadOptions;
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
	
	@Inject
	MockService mockService;
	
	@InjectMock
	CacheService cacheService;
	
	JsonObject header;
	TokenIntrospectionRequest tokenIntrospectionRequest;
	
	@BeforeEach
	public void initEach() {
		when(cacheService.getJwks()).thenReturn(mockService.createJwks());
		header = mockService.createHeader();
		tokenIntrospectionRequest = mockService.createIntrospectionRequest();
	}
	
	@Test
	public void testIntrospectToken_noIntrospectionRequest() {
		assertFalse(offlineTokenValidationService.introspectToken(null));
	}
	
	@Test
	public void testIntrospectToken_noHeader() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
		    mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(null);
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_noPayload() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
		    mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(null);
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_scopeFlexibleInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.SCOPE, PayloadOptions.SCOPE_NOT_EXIST)));			
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_scopeFlexibleValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.SCOPE, PayloadOptions.SCOPE_NOT_COMPLETE)));			
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_scopeStrictInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.SCOPE, PayloadOptions.SCOPE_NOT_COMPLETE)));
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.SCOPE, IntrospectionOptions.SCOPE_STRICT));
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_scopeStrictValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.SCOPE)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.SCOPE, IntrospectionOptions.SCOPE_STRICT));
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_roleFlexibleInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE, PayloadOptions.ROLE_NOT_EXIST)));			
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_roleFlexibleValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE, PayloadOptions.ROLE_NOT_COMPLETE)));			
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_roleStrictInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE, PayloadOptions.ROLE_NOT_COMPLETE)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.ROLE, IntrospectionOptions.ROLE_STRICT));
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_roleStrictValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.ROLE, IntrospectionOptions.ROLE_STRICT));
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_groupFlexibleInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUP, PayloadOptions.GROUP_NOT_EXIST)));		
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_groupFlexibleValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUP, PayloadOptions.GROUP_NOT_COMPLETE)));			
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_groupStrictInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUP, PayloadOptions.GROUP_NOT_COMPLETE)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.GROUP, IntrospectionOptions.GROUP_STRICT));
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_groupStrictValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUP)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.GROUP, IntrospectionOptions.GROUP_STRICT));
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_grouproleFlexibleInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUPROLE, PayloadOptions.GROUPROLE_NOT_EXIST)));			
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_grouproleFlexibleValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUPROLE, PayloadOptions.GROUPROLE_NOT_COMPLETE)));			
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_grouproleStrictInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUPROLE, PayloadOptions.GROUPROLE_NOT_COMPLETE)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.GROUP, IntrospectionOptions.GROUPROLE_STRICT));
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_grouproleStrictValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.GROUPROLE)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.GROUP, IntrospectionOptions.GROUPROLE_STRICT));
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_flexibleValidationInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE, PayloadOptions.ROLE_NOT_EXIST, PayloadOptions.SCOPE, PayloadOptions.SCOPE_NOT_EXIST)));	
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_flexibleValidationValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE, PayloadOptions.SCOPE, PayloadOptions.SCOPE_NOT_EXIST)));
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_strictValidationInvalid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE, PayloadOptions.SCOPE, PayloadOptions.SCOPE_NOT_EXIST)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.ROLE, IntrospectionOptions.SCOPE, IntrospectionOptions.VALIDATION_STRICT));
			assertFalse(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testIntrospectToken_strictValidationValid() {
		try (MockedStatic<JwtUtil> mockStatic = Mockito.mockStatic(JwtUtil.class)) {
			mockStatic.when(() -> JwtUtil.decodeHeader(null)).thenReturn(header);
			mockStatic.when(() -> JwtUtil.decodePayload(null)).thenReturn(
					mockService.createPayload(Arrays.asList(PayloadOptions.ROLE, PayloadOptions.SCOPE)));	
			tokenIntrospectionRequest = mockService.createIntrospectionRequest(Arrays.asList(IntrospectionOptions.ROLE, IntrospectionOptions.SCOPE, IntrospectionOptions.VALIDATION_STRICT));
			assertTrue(offlineTokenValidationService.introspectToken(tokenIntrospectionRequest));
		}
	}
	
	@Test
	public void testValidateTokenHeader_emptyJwks() {
		JsonObject emptyJwks = Json.createObjectBuilder()
			.add("keys", Json.createArrayBuilder())
			.build();
		when(cacheService.getJwks()).thenReturn(emptyJwks);		
		assertFalse(offlineTokenValidationService.validateTokenHeader(header));
	}
	
	@Test
	public void testValidateTokenHeader_missingHeaderClaim() {
		JsonObject header = Json.createObjectBuilder()
			.add("alg", "123")
			.build();
		assertFalse(offlineTokenValidationService.validateTokenHeader(header));
	}
	
	@Test
	public void testValidateTokenHeader_invalidCombination() {		
		header = Json.createObjectBuilder()
			.add("alg", "abc")
			.add("kid", "456")
			.build();
		assertFalse(offlineTokenValidationService.validateTokenHeader(header));
	}
	
	@Test
	public void testValidateTokenHeader_validHeader() {
		assertTrue(offlineTokenValidationService.validateTokenHeader(header));
	}
	
	@Test
	public void testValidateGeneralInfo_missingClaim() {
		JsonObject payload = Json.createObjectBuilder()
			.add("test", "test")
			.build();
		assertFalse(offlineTokenValidationService.validateGeneralInfo(payload));
	}
	
	@Test
	public void testValidateGeneralInfo_invalidIss() {
		JsonObject payload = mockService.createPayload(Arrays.asList(PayloadOptions.ISS_INVALID));
		assertFalse(offlineTokenValidationService.validateGeneralInfo(payload));
	}
	
	@Test
	public void testValidateGeneralInfo_expiredToken() {
		JsonObject payload = mockService.createPayload(Arrays.asList(PayloadOptions.EXP_INVALID));
		assertFalse(offlineTokenValidationService.validateGeneralInfo(payload));
	}
	
	@Test
	public void testValidateGeneralInfo_validPayload() {
		JsonObject payload = mockService.createPayload(new ArrayList<PayloadOptions>());
		assertTrue(offlineTokenValidationService.validateGeneralInfo(payload));
	}

}
