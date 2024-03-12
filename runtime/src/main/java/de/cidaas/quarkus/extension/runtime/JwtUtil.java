package de.cidaas.quarkus.extension.runtime;

import java.io.StringReader;
import java.util.Base64;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class JwtUtil {
	static JsonObject decodeHeader(String accessToken) {
		String[] arr = accessToken.split("\\.", 0);
		return decode(arr[0]);
	}
	
	static JsonObject decodePayload(String accessToken) {
		String[] arr = accessToken.split("\\.", 0);
		return decode(arr[1]);
	}
	
	private static JsonObject decode(String encoded) {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes = decoder.decode(encoded);
		String decodedString = new String(decodedBytes);
		JsonReader reader = Json.createReader(new StringReader(decodedString));
		return reader.readObject();
	}
}
