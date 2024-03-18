package de.cidaas.quarkus.extension.address.validation;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class AddressValidationTestProfile implements QuarkusTestProfile {
	@Override
	public Map<String, String> getConfigOverrides() {
		Map<String, String> quarkusConfig = new HashMap<>();
		quarkusConfig.put("de.cidaas.quarkus.extension.address.validation.apicid", "apicid");
		quarkusConfig.put("de.cidaas.quarkus.extension.address.validation.apikey", "apikey");
		return quarkusConfig;
	}

}
