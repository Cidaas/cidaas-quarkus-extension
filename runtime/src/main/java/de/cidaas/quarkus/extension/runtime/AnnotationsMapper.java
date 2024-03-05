package de.cidaas.quarkus.extension.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.cidaas.quarkus.extension.Group;
import de.cidaas.quarkus.extension.GroupAllowed;
import de.cidaas.quarkus.extension.TokenIntrospectionRequest;
import de.cidaas.quarkus.extension.TokenValidation;

public class AnnotationsMapper {
	static TokenIntrospectionRequest mapToIntrospectionRequest(String accessToken, TokenValidation tokenValidation) {
		TokenIntrospectionRequest request = new TokenIntrospectionRequest();
		request.setToken(accessToken);
		request.setToken_type_hint(tokenValidation.tokenTypeHint());
		request.setRoles(Arrays.asList(tokenValidation.roles()));
		request.setGroups(mapToGroups(tokenValidation.groups()));
		request.setScopes(Arrays.asList(tokenValidation.scopes()));
		request.setStrictRoleValidation(tokenValidation.strictRoleValidation());
		request.setStrictGroupValidation(tokenValidation.strictGroupValidation());
		request.setStrictScopeValidation(tokenValidation.strictScopeValidation());
		request.setStrictValidation(tokenValidation.strictValidation());
		return request;
	}
	
	static List<Group> mapToGroups(GroupAllowed[] groupsAllowed) {
		List<Group> result = new ArrayList<>();
		List<GroupAllowed> groups = Arrays.asList(groupsAllowed);
		for(GroupAllowed group : groups) {
			result.add(new Group(group.id(), Arrays.asList(group.roles()), group.strictRoleValidation()));
		}
		return result;
	}
}
