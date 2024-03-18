package de.cidaas.quarkus.extension.address.validation;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@Alternative()
@Priority(1)
@ApplicationScoped
@RestClient
public class MockAddressValidationClient implements AddressValidationClient {

	@Override
	public Response callValidateAddress(String street, String hno, String zip, String city, String country,
			String apicid, String apikey, String product) {
		JsonObject body = Json.createObjectBuilder()
				.add("sc",
						Json.createArrayBuilder()
								.add(Json.createObjectBuilder().add("street", "").add("hno", "").add("zip", "")
										.add("city", "").add("district", "").add("points", 0).add("resultcode", "")
										.add("resulttext", "OK").add("trafficlight", "")))
				.build();
		return Response.ok(body).build();
	}

}
