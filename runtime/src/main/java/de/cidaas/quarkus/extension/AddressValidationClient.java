package de.cidaas.quarkus.extension;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(baseUri="https://api.adresslabor.de")
public interface AddressValidationClient {
	@POST
	@Path("/v1/de/check")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	Response callValidateAddress(
				@FormParam("street") String street,
				@FormParam("hno") String hno,
				@FormParam("zip") String zip,
				@FormParam("city") String city,
				@FormParam("country") String country,
				@FormParam("apicid") String apicid,
				@FormParam("apikey") String apikey,
				@FormParam("product") String product
			);
}
