package de.cidaas.quarkus.extension.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.cidaas.quarkus.extension.Group;
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
		
		List<String> groups = Arrays.asList(groupsAllowed.value());
		
		for(String group : groups) {
			String[] parts = group.split(":");
			String groupId = parts[0];
			String role = parts[1];
			Group groupEntity = new Group(groupId, Arrays.asList(role), false);
			result.add(groupEntity);
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
