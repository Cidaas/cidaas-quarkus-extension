package de.cidaas.quarkus.extension.token.validation;

public interface ValidationService {
	public boolean validateToken(TokenValidationRequest tokenValidationRequest);
}
