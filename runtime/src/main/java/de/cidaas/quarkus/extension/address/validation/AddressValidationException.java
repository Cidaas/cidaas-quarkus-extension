package de.cidaas.quarkus.extension.address.validation;

public class AddressValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AddressValidationException(String message) {
		super(message);
	}
}
