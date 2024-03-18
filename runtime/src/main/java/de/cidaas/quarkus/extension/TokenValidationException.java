package de.cidaas.quarkus.extension;

public class TokenValidationException extends RuntimeException {
	public TokenValidationException(String message) {
		super(message);
	}
}
