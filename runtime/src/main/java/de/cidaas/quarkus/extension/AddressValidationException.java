package de.cidaas.quarkus.extension;

public class AddressValidationException extends RuntimeException {
	public AddressValidationException(String message){
		super(message);
	}
}
