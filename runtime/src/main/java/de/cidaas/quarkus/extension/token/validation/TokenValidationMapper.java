package de.cidaas.quarkus.extension.token.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.cidaas.quarkus.extension.annotation.GroupAllowed;
import de.cidaas.quarkus.extension.annotation.TokenValidation;

public class TokenValidationMapper {

	/**
	 * map TokenValidation to TokenValidationRequest
	 *
	 * @param accessToken     token to be put on TokenValidationRequest.
	 * @param tokenValidation input from method annotation.
	 * 
	 * @return TokenValidationRequest
	 */
	public static TokenValidationRequest mapToValidationRequest(String accessToken, TokenValidation tokenValidation) {
		TokenValidationRequest request = new TokenValidationRequest();

		request.setToken(accessToken);
		request.setToken_type_hint(tokenValidation.tokenTypeHint());

		if (tokenValidation.roles() != null) {
			request.setRoles(Arrays.asList(tokenValidation.roles()));
		}

		if (tokenValidation.groups() != null) {
			request.setGroups(mapToGroups(tokenValidation.groups()));
		}

		if (tokenValidation.scopes() != null) {
			request.setScopes(Arrays.asList(tokenValidation.scopes()));
		}

		request.setStrictRoleValidation(tokenValidation.strictRoleValidation());
		request.setStrictGroupValidation(tokenValidation.strictGroupValidation());
		request.setStrictScopeValidation(tokenValidation.strictScopeValidation());
		request.setStrictValidation(tokenValidation.strictValidation());

		return request;
	}

	private static List<Group> mapToGroups(GroupAllowed[] groupsAllowed) {
		List<Group> result = new ArrayList<>();
		List<GroupAllowed> groups = Arrays.asList(groupsAllowed);
		for (GroupAllowed group : groups) {
			result.add(new Group(group.id(), Arrays.asList(group.roles()), group.strictRoleValidation()));
		}
		return result;
	}
}
