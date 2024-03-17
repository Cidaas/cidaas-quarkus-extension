package de.cidaas.quarkus.extension.runtime;

import jakarta.json.JsonObject;
import jakarta.json.JsonString;

public class JsonUtil {
	
	static String getStringFromJsonOrNull(JsonObject jsonObject, String key) {
		JsonString jsonString = jsonObject.getJsonString(key);
		if (jsonString == null) {
			return null;
		}
		return jsonString.getString();
	}
	
}
