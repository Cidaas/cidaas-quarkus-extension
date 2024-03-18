package de.cidaas.quarkus.extension.address.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;

@QuarkusTest
@TestProfile(AddressValidationTestProfile.class)
public class AddressValidationServiceTest {
	@Inject
	AddressValidationService addressValidationService;

	@Test
	public void testValidateAddress_InvalidInput() {
		AddressValidationException exception = assertThrows(AddressValidationException.class, () -> {
			addressValidationService.validateAddress(new AddressValidationRequest());
		});
		assertTrue(exception.getMessage().contains("Provided street must not be null or blank."));
	}

	@Test
	public void testValidateAddress_ValidInput() {
		AddressValidationRequest request = new AddressValidationRequest();
		request.setStreet("street");
		request.setHouseNumber("houseNumber");
		request.setZipCode("11111");
		request.setCity("city");

		AddressValidationResult result = addressValidationService.validateAddress(request);
		assertEquals(result.getResulttext(), "OK");
	}

	@Test
	public void testValidateEmail_InvalidEmail() {
		assertFalse(addressValidationService.validateEmail("invalidEmail"));
	}

	@Test
	public void testValidateEmail_ValidEmail() {
		assertTrue(addressValidationService.validateEmail("testertest@widas.de"));
	}
}
