package de.cidaas.quarkus.extension.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.cidaas.quarkus.extension.Group;
import de.cidaas.quarkus.extension.GroupAllowed;
import de.cidaas.quarkus.extension.GroupsAllowed;
import de.cidaas.quarkus.extension.RolesAllowed;
import de.cidaas.quarkus.extension.ScopesAllowed;

public class AnnotationsMapper {
	static List<String> mapToRoles(RolesAllowed rolesAllowed) {
		if (rolesAllowed == null) {
			return null;
		}
		
		return Arrays.asList(rolesAllowed.value());
	}
	
	static List<Group> mapToGroups(GroupsAllowed groupsAllowed) {
		if (groupsAllowed == null) {
			return null;
		}
		
		List<Group> result = new ArrayList<>();
		List<GroupAllowed> groups = Arrays.asList(groupsAllowed.value());
		
		for(GroupAllowed group : groups) {
			result.add(new Group(group.id(), Arrays.asList(group.roles()), group.strictRolesValidation()));
		}
		return result;
	}
	
	static List<String> mapToScopes(ScopesAllowed scopesAllowed) {
		if (scopesAllowed == null) {
			return null;
		}
		
		return Arrays.asList(scopesAllowed.value());
	}
}
