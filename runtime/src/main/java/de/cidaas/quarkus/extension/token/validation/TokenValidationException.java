package de.cidaas.quarkus.extension.token.validation;

public class TokenValidationException extends RuntimeException {
	public TokenValidationException(String message) {
		super(message);
	}
}
