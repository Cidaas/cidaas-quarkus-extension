package de.cidaas.quarkus.extension.address.validation;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cidaas.quarkus.extension.runtime.CacheService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AddressValidationService {

	private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);

	@Inject
	@RestClient
	AddressValidationClient addressValidationClient;

	/**
	 * Checks whether address is valid.
	 *
	 * @param request contains address information to be validated such as street,
	 *                house number, zip code, city and country.
	 * @return AddressValidationResult contains details about Address Validation
	 *         Result
	 */
	public AddressValidationResult validateAddress(AddressValidationRequest request) {
		validateInput(request);

		long validationStart = System.currentTimeMillis();
		String apicid = ConfigProvider.getConfig().getValue("de.cidaas.quarkus.extension.address.validation.apicid",
				String.class);
		String apikey = ConfigProvider.getConfig().getValue("de.cidaas.quarkus.extension.address.validation.apikey",
				String.class);
		String productid = "SC";
		String country = request.getCountry() == null ? request.getCountry() : "DE";

		long adresslaborStart = System.currentTimeMillis();
		Response response = addressValidationClient.callValidateAddress(request.getStreet(), request.getHouseNumber(),
				request.getZipCode(), request.getCity(), country, apicid, apikey, productid);
		LOG.info("Request to adresslabor.de took {}ms", System.currentTimeMillis() - adresslaborStart);

		AddressValidationResult result = mapResponseToResult(response);
		LOG.info("Request to validate postal address took {}ms", System.currentTimeMillis() - validationStart);
		return result;
	}

	/**
	 * Checks whether email is valid.
	 *
	 * @param email address to be validated
	 * @return true if email is valid, false if email is invalid
	 */
	public boolean validateEmail(String email) {
		if (StringUtils.isBlank(email)) {
			LOG.error("Provided email must not be null or blank");
			throw new AddressValidationException("Provided email must not be null or blank.");
		}
		boolean isEmailValid = SMTPMXLookup.isAddressValid(email);
		if (isEmailValid) {
			LOG.info("Email {}  is valid", email);
		} else {
			LOG.info("Email {} is invalid!! ", email);
		}
		return isEmailValid;
	}

	private AddressValidationResult mapResponseToResult(Response response) {
		if (response == null || response.readEntity(JsonObject.class) == null
				|| response.readEntity(JsonObject.class).getJsonArray("sc") == null
				|| response.readEntity(JsonObject.class).getJsonArray("sc").getJsonObject(0) == null) {
			LOG.error("response of callValidateAddress has unexpected format");
			throw new AddressValidationException("response of callValidateAddress has unexpected format.");
		}
		JsonObject resultAsJson = response.readEntity(JsonObject.class).getJsonArray("sc").getJsonObject(0);

		AddressValidationResult result = new AddressValidationResult();
		result.setStreet(resultAsJson.getString("street"));
		result.setHouseNumber(resultAsJson.getString("hno"));
		result.setZipCode(resultAsJson.getString("zip"));
		result.setCity(resultAsJson.getString("city"));
		result.setDistrict(resultAsJson.getString("district"));
		result.setCountry(resultAsJson.getString("country", null));
		result.setPoints(resultAsJson.getInt("points") + "");
		result.setResultcode(resultAsJson.getString("resultcode"));
		result.setResulttext(resultAsJson.getString("resulttext"));
		String trafficlight = resultAsJson.getString("trafficlight");
		if (trafficlight != null) {
			result.setTrafficlight(trafficlight.toUpperCase());
		}
		return result;
	}

	private void validateInput(AddressValidationRequest request) throws AddressValidationException {
		if (StringUtils.isBlank(request.getStreet())) {
			LOG.error("Provided street must not be null or blank");
			throw new AddressValidationException("Provided street must not be null or blank.");
		}
		if (StringUtils.isBlank(request.getZipCode())) {
			LOG.error("Provided zip code must not be null or blank.");
			throw new AddressValidationException("Provided zip code must not be null or blank.");
		}
		if (StringUtils.isBlank(request.getCity())) {
			LOG.error("Provided city must not be null or blank.");
			throw new AddressValidationException("Provided city must not be null or blank.");
		}
	}

}
