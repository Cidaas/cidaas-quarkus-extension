package de.cidaas.quarkus.extension.address.validation;

public class AddressValidationException extends RuntimeException {
	public AddressValidationException(String message) {
		super(message);
	}
}
