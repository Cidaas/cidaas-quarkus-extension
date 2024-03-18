package de.cidaas.quarkus.extension.token.validation;

public class TokenValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TokenValidationException(String message) {
		super(message);
	}
}
