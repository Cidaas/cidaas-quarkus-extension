package de.cidaas.quarkus.extension;

import java.util.List;

public class Group {
	private String groupId;
	private List<String> roles;
	private boolean strictRoleValidation;
	
	public Group(String groupId, List<String> roles, boolean strictRoleValidation) {
		super();
		this.groupId = groupId;
		this.roles = roles;
		this.strictRoleValidation = strictRoleValidation;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public boolean isStrictRoleValidation() {
		return strictRoleValidation;
	}

	public void setStrictRoleValidation(boolean strictRoleValidation) {
		this.strictRoleValidation = strictRoleValidation;
	}
	
}
