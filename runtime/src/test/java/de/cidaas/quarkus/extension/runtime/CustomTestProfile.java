package de.cidaas.quarkus.extension.runtime;

import java.util.Collections;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class CustomTestProfile implements QuarkusTestProfile {
	@Override
	public Map<String, String> getConfigOverrides() {
		return Collections.singletonMap("de.cidaas.quarkus.extension.runtime.CidaasClient/mp-rest/url", "mockUrl");
	}

}
