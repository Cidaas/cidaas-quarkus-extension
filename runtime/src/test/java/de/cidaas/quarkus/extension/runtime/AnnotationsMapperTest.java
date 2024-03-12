package de.cidaas.quarkus.extension.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.cidaas.quarkus.extension.GroupAllowed;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import de.cidaas.quarkus.extension.TokenValidation;

public class AnnotationsMapperTest {
	@Test
	public void testMapAnnotationEmpty() {
		String accessToken = "";
		TokenValidation tokenValidation = new TokenValidation() {
			@Override
            public Class<? extends Annotation> annotationType()
            {
                return TokenValidation.class;
            }
			@Override
			public String tokenTypeHint() {
				return null;
			}
			@Override
			public String[] roles() {
				return null;
			}
			@Override
			public GroupAllowed[] groups() {
				return null;
			}
			@Override
			public String[] scopes() {
				return null;
			}
			@Override
			public boolean strictRoleValidation() {
				return false;
			}
			@Override
			public boolean strictGroupValidation() {
				return false;
			}
			@Override
			public boolean strictScopeValidation() {
				return false;
			}
			@Override
			public boolean strictValidation() {
				return false;
			}
			@Override
			public boolean offlineValidation() {
				return false;
			}
		};
		TokenIntrospectionRequest result = AnnotationsMapper.mapToIntrospectionRequest(accessToken, tokenValidation);
		assertEquals(result.getToken(), accessToken);
		assertNull(result.getToken_type_hint());
		assertNull(result.getRoles());
		assertNull(result.getGroups());
		assertNull(result.getScopes());
		assertFalse(result.isStrictGroupValidation());
		assertFalse(result.isStrictScopeValidation());
		assertFalse(result.isStrictRoleValidation());
		assertFalse(result.isStrictValidation());
	}
	
	@Test
	public void testMapAnnotationWithValue() {
		String accessToken = "accessToken";
		TokenValidation tokenValidation = new TokenValidation() {
			@Override
            public Class<? extends Annotation> annotationType()
            {
                return TokenValidation.class;
            }

			@Override
			public String tokenTypeHint() {
				return "access_token";
			}

			@Override
			public String[] roles() {
				String[] roles = {"role1", "role2"};
				return roles;
			}

			@Override
			public GroupAllowed[] groups() {
				GroupAllowed group1 = new GroupAllowed() {
					@Override
					public Class<? extends Annotation> annotationType() {
						return GroupAllowed.class;
					}
					@Override
					public String id() {
						return "group1";
					}
					@Override
					public String[] roles() {
						String[] roles = {"grouprole1"};
						return roles;
					}
					@Override
					public boolean strictRoleValidation() {
						return false;
					}
				};
				GroupAllowed group2 = new GroupAllowed() {
					@Override
					public Class<? extends Annotation> annotationType() {
						return GroupAllowed.class;
					}
					@Override
					public String id() {
						return "group2";
					}
					@Override
					public String[] roles() {
						String[] roles = {"grouprole2"};
						return roles;
					}
					@Override
					public boolean strictRoleValidation() {
						return true;
					}
				};
				GroupAllowed[] groups = {group1, group2};
				return groups;
			}

			@Override
			public String[] scopes() {
				String[] scopes = {"scope1", "scope2"};
				return scopes;
			}

			@Override
			public boolean strictRoleValidation() {
				return true;
			}

			@Override
			public boolean strictGroupValidation() {
				return true;
			}

			@Override
			public boolean strictScopeValidation() {
				return true;
			}

			@Override
			public boolean strictValidation() {
				return true;
			}

			@Override
			public boolean offlineValidation() {
				return false;
			}
		};
		TokenIntrospectionRequest result = AnnotationsMapper.mapToIntrospectionRequest(accessToken, tokenValidation);
		assertEquals(result.getToken(), accessToken);
		assertEquals(result.getToken_type_hint(), tokenValidation.tokenTypeHint());
		assertEquals(result.getRoles(), Arrays.asList(tokenValidation.roles()));
		assertEquals(result.getGroups().get(0).getGroupId(), tokenValidation.groups()[0].id());
		assertEquals(result.getGroups().get(0).getRoles(), Arrays.asList(tokenValidation.groups()[0].roles()));
		assertEquals(result.getGroups().get(0).isStrictRoleValidation(), tokenValidation.groups()[0].strictRoleValidation());
		assertEquals(result.getGroups().get(1).getGroupId(), tokenValidation.groups()[1].id());
		assertEquals(result.getGroups().get(1).getRoles(), Arrays.asList(tokenValidation.groups()[1].roles()));
		assertEquals(result.getGroups().get(1).isStrictRoleValidation(), tokenValidation.groups()[1].strictRoleValidation());
		assertEquals(result.getScopes(), Arrays.asList(tokenValidation.scopes()));
		assertEquals(result.isStrictGroupValidation(), tokenValidation.strictGroupValidation());
		assertEquals(result.isStrictScopeValidation(), tokenValidation.strictScopeValidation());
		assertEquals(result.isStrictRoleValidation(), tokenValidation.strictRoleValidation());
		assertEquals(result.isStrictValidation(), tokenValidation.strictValidation());
	}

}
