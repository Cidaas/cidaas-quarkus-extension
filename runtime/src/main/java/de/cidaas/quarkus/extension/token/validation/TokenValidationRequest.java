package de.cidaas.quarkus.extension.token.validation;

import java.util.List;

public class TokenValidationRequest {
	private String token;
	private String token_type_hint;
	private List<String> roles;
	private List<Group> groups;
	private List<String> scopes;
	private boolean strictGroupValidation;
	private boolean strictScopeValidation;
	private boolean strictRoleValidation;
	private boolean strictValidation;

	public TokenValidationRequest() {
		super();
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken_type_hint() {
		return token_type_hint;
	}

	public void setToken_type_hint(String token_type_hint) {
		this.token_type_hint = token_type_hint;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	public boolean isStrictGroupValidation() {
		return strictGroupValidation;
	}

	public void setStrictGroupValidation(boolean strictGroupValidation) {
		this.strictGroupValidation = strictGroupValidation;
	}

	public boolean isStrictScopeValidation() {
		return strictScopeValidation;
	}

	public void setStrictScopeValidation(boolean strictScopeValidation) {
		this.strictScopeValidation = strictScopeValidation;
	}

	public boolean isStrictRoleValidation() {
		return strictRoleValidation;
	}

	public void setStrictRoleValidation(boolean strictRoleValidation) {
		this.strictRoleValidation = strictRoleValidation;
	}

	public boolean isStrictValidation() {
		return strictValidation;
	}

	public void setStrictValidation(boolean strictValidation) {
		this.strictValidation = strictValidation;
	}

}
